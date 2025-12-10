import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Semantico implements Constants {

    // --- estruturas básicas ---
    private final Simbolo variavel = new Simbolo();
    private final Simbolo temporario = new Simbolo();
    private final TabelaSimbolos tabelaSimbolos = new TabelaSimbolos();

    private final Stack<Integer> pilhaEscopo = new Stack<>();
    private Integer contadorEscopo = 0;

    private final Stack<Integer> pilhaTipo = new Stack<>();
    private Integer tipoAtribuicao;

    private final Stack<Integer> pilhaOperacao = new Stack<>();
    private final List<Simbolo> listaVars = new ArrayList<>();

    // --- flags de controle ---
    private boolean inicio = true;
    private boolean isDeclaracao = false;
    private boolean atribuicaoIsVet = false;
    private boolean flagOperacao = false;
    private boolean auxFlagOp;
    private boolean calculoIndice = false;
    private boolean isFor = false;

    // --- auxiliares diversos ---
    private String nome = "";
    private String nomeVet = "";
    private int ultimoOperador = 0;
    private int contadorParametro = 0;
    private String idAtribuicao;

    // --- temporários e geração de código ---
    private boolean temp1Usado = false;
    private boolean temp2Usado = false;
    private final List<Temp> tabelaTemp = new ArrayList<>();
    private boolean temVetor = false;
    private int contadorTemporarios = 0;
    private int contadorOperando = 0;

    private int contadorRotulo = 0;
    private String operadorRel;
    private final Stack<String> pilhaRotulo = new Stack<>();
    private final List<String> codigoTemporarios = new ArrayList<>();

    // --- controle de for ---
    private String forRotuloInicio = "";
    private String forRotuloFim = "";
    private final List<String> forCodigoIncremento = new ArrayList<>();
    private boolean capturandoIncrementoFor = false;
    private String codigoGeradoBackup = "";

    // --- controle de função ---
    private boolean dentroFuncao = false;
    private String funcaoAtual = "";
    private boolean primeiraFuncao = true;
    private boolean processandoReturn = false;

    // entrypoint default (aceita main/principal/principale)
    private String nomeFuncaoPrincipal = "principale";
    private boolean chamadaFuncaoVoid = false;

    // --- saída ---
    public String codigoGerado = "";
    public String warnings = "";
    private String nomeFunc;
    private String nomeChamada;

    // mapeia tipos do léxico (latim) para nomes internos
    private String mapTypeLexeme(String lex) {
        return switch (lex) {
            case "int" -> "int";
            case "flottans" -> "float";
            case "littera" -> "char";
            case "chorda" -> "string";
            case "verumfalsum" -> "bool";
            case "vacuum" -> "void";
            default -> lex;
        };
    }

    public void executeAction(int action, Token token) throws SemanticError {
        // silencie o log se quiser:
        // System.out.println("Ação #" + action + ", Token: " + token);

        if (inicio) {
            pilhaEscopo.push(contadorEscopo);
            inicio = false;
        }

        switch (action) {
            case 48: { // fim do programa: avisos de variáveis não usadas
                for (Simbolo s : tabelaSimbolos.getListaSimbolos()) {
                    // não avisar sobre funções nem sobre o entrypoint
                    if (Boolean.TRUE.equals(s.getFlagFuncao())) continue;
                    if (s.getId().equalsIgnoreCase(nomeFuncaoPrincipal)) continue;

                    if (s.getFlagUsada() != null && !s.getFlagUsada()) {
                        warnings += String.format("Variavel '%s' nao usada\n", s.getId());
                    }
                }
                break;
            }
            case 49: { // início de declaração
                isDeclaracao = true;
                break;
            }
            case 50: { // fim de uma unidade de declaração
                if (!tabelaSimbolos.declarar(variavel, pilhaEscopo) && !tabelaSimbolos.getListaSimbolos().isEmpty()) {
                    throw new SemanticError(String.format("Variavel %s já declarada", variavel.getId()), token.getPosition());
                }
                tabelaSimbolos.add(variavel);
                isDeclaracao = false;
                nomeVet = "";
                break;
            }
            case 51: { // início de atribuição
                nome = token.getLexeme();
                if (!isDeclaracao) {
                    if (!tabelaSimbolos.declarada(variavel, pilhaEscopo)) {
                        throw new SemanticError(String.format("Variavel %s não declarada", variavel.getId()), token.getPosition());
                    }
                    idAtribuicao = variavel.getId();
                    atribuicaoIsVet = variavel.getFlagVetor();
                    tipoAtribuicao = tabelaSimbolos.getTipo(variavel.getId(), variavel.getFlagFuncao(),
                            variavel.getFlagVetor(), pilhaEscopo);

                    if (atribuicaoIsVet) {
                        temVetor = true;
                        int indice = getTemp(); contadorTemporarios++;
                        GeraCod("STO", "temp" + indice);
                        codigoTemporarios.add("LD temp" + indice);
                        codigoTemporarios.add("STO $indr");
                    }
                } else {
                    if (!tabelaSimbolos.declarar(variavel, pilhaEscopo)) {
                        throw new SemanticError(String.format("Variavel %s já declarada em algum escopo visível", variavel.getId()), token.getPosition());
                    }
                    tabelaSimbolos.add(variavel);
                    idAtribuicao = variavel.getId();
                    atribuicaoIsVet = variavel.getFlagVetor();
                    tipoAtribuicao = tabelaSimbolos.getTipo(variavel.getId(), variavel.getFlagFuncao(),
                            variavel.getFlagVetor(), pilhaEscopo);
                    isDeclaracao = false;

                    if (atribuicaoIsVet) {
                        temVetor = true;
                        int indice = getTemp(); contadorTemporarios++;
                        GeraCod("STO", "temp" + indice);
                        codigoTemporarios.add("LD temp" + indice);
                        codigoTemporarios.add("STO $indr");
                    }
                }
                break;
            }
            case 52: { // fim da atribuição
                int resultAtrib = SemanticTable.atribType(tipoAtribuicao, pilhaTipo.pop());
                if (resultAtrib == -1) throw new SemanticError("Atribuição de tipos incompativeis", token.getPosition());
                if (resultAtrib == 1) warnings += String.format(
                        "Atribuindo um float a um inteiro, possivel perca de precisão (Pos: %d)\n",
                        token.getPosition());

                if (!tabelaSimbolos.setInicializada(idAtribuicao, atribuicaoIsVet, 0)) {
                    throw new SemanticError(String.format("Variavel %s não declarada", idAtribuicao), token.getPosition());
                }

                if (temVetor) {
                    int indice = getTemp(); contadorTemporarios++;
                    GeraCod("STO", "temp" + indice);
                    if ((atribuicaoIsVet || indice != 0) && (!atribuicaoIsVet || indice != 1)) {
                        if (ultimoOperador == 0) codigoTemporarios.add("ADD temp" + indice);
                        else if (ultimoOperador == 1) codigoTemporarios.add("SUB temp" + indice);
                    } else {
                        codigoTemporarios.add("LD temp" + indice);
                    }
                }

                while (!codigoTemporarios.isEmpty()) {
                    String cmd = codigoTemporarios.remove(0);
                    String[] parts = cmd.split(" ");
                    if (parts.length > 1) GeraCod(parts[0], parts[1]); else GeraCod(parts[0], "");
                }

                String nomeParaGeracao = idAtribuicao;
                if (isParametroDaFuncaoAtual(idAtribuicao)) nomeParaGeracao = funcaoAtual + "_" + idAtribuicao;

                if (atribuicaoIsVet) GeraCod("STOV", nomeParaGeracao); else GeraCod("STO", nomeParaGeracao);

                contadorTemporarios = 0;
                contadorOperando = 0;
                atribuicaoIsVet = false;
                temVetor = false;
                freeTemp();
                break;
            }
            case 53: { // marca vetor
                variavel.setFlagVetor(true);
                nomeVet = token.getLexeme();
                break;
            }
            case 54: { // início índice de vetor
                auxFlagOp = flagOperacao;
                if (flagOperacao) {
                    int indice = getTemp(); contadorTemporarios++;
                    GeraCod("STO", "temp" + indice);
                    if ((atribuicaoIsVet || indice != 0) && (!atribuicaoIsVet || indice != 1)) {
                        if (pilhaOperacao.peek() == 0) codigoTemporarios.add("ADD temp" + indice);
                        else if (pilhaOperacao.peek() == 1) codigoTemporarios.add("SUB temp" + indice);
                    } else {
                        codigoTemporarios.add("LD temp" + indice);
                    }
                }
                flagOperacao = false;
                calculoIndice = true;
                copia(variavel, temporario); // salva durante cálculo do índice
                break;
            }
            case 55: { // fim índice
                flagOperacao = auxFlagOp;
                calculoIndice = false;
                copia(temporario, variavel);
                break;
            }
            case 56: { // tipo (latim -> interno)
                variavel.setTipo(mapTypeLexeme(token.getLexeme()));
                break;
            }
            case 57: { // ID
                variavel.setId(token.getLexeme());
                variavel.setEscopo(pilhaEscopo.peek());
                variavel.setFlagVetor(false);
                variavel.setFlagFuncao(false);
                variavel.setFlagParametro(false);
                variavel.setFlagInicializada(false);
                variavel.setFlagUsada(false);
                variavel.setTamanho(0);
                if (isParametroDaFuncaoAtual(token.getLexeme())) variavel.setFlagParametro(true);
                break;
            }
            case 58: { // início de função
                variavel.setFlagFuncao(true);
                nomeFunc = token.getLexeme();
                funcaoAtual = nomeFunc;
                dentroFuncao = true;
                if (primeiraFuncao) primeiraFuncao = false;

                if (nomeFunc.equalsIgnoreCase("main")
                        || nomeFunc.equalsIgnoreCase("principal")
                        || nomeFunc.equalsIgnoreCase("principale")) {
                    nomeFuncaoPrincipal = nomeFunc; // usado no JMP
                }
                GeraCod("_" + nomeFunc + ":", "");
                break;
            }
            case 59: { // fim de função
                processandoReturn = false;
                if (!funcaoAtual.equalsIgnoreCase("main")
                        && !funcaoAtual.equalsIgnoreCase("principal")
                        && !funcaoAtual.equalsIgnoreCase("principale")) {
                    GeraCod("RETURN", "0");
                } else {
                    GeraCod("HLT", "0");
                }
                dentroFuncao = false;
                funcaoAtual = "";
                break;
            }
            case 60: { // parâmetro formal
                variavel.setEscopo(variavel.getEscopo() + 1);
                variavel.setFlagParametro(true);
                variavel.setFlagInicializada(true);
                tabelaSimbolos.addParametro(nomeFunc, variavel.getId(), variavel.getTipo(), variavel.getFlagVetor());
                tabelaSimbolos.add(variavel);
                break;
            }
            case 61: { // abre escopo
                contadorEscopo++;
                pilhaEscopo.push(contadorEscopo);
                break;
            }
            case 62: { // fecha escopo
                pilhaEscopo.pop();
                break;
            }
            case 63: { // escreva
                GeraCod("STO", "$out_port");
                break;
            }
            case 64: { // leia
                if (!tabelaSimbolos.setInicializada(variavel.getId(), variavel.getFlagVetor(), 0)) {
                    throw new SemanticError(String.format("Variavel %s não declarada\n", variavel.getId()), token.getPosition());
                }
                String nomeVar = variavel.getId();
                if (isParametroDaFuncaoAtual(variavel.getId())) nomeVar = funcaoAtual + "_" + variavel.getId();

                if (!variavel.getFlagVetor()) {
                    GeraCod("LD", "$in_port");
                    GeraCod("STO", nomeVar);
                } else {
                    GeraCod("STO", "$indr");
                    while (!codigoTemporarios.isEmpty()) {
                        String cmd = codigoTemporarios.remove(0);
                        String[] parts = cmd.split(" ");
                        if (parts.length > 1) GeraCod(parts[0], parts[1]); else GeraCod(parts[0], "");
                    }
                    GeraCod("LD", "$in_port");
                    GeraCod("STOV", nomeVar);
                    contadorTemporarios = 0;
                    temVetor = false;
                    freeTemp();
                }
                break;
            }
            case 65: { // início condicional
                String rotulo = newRotulo();
                pilhaRotulo.push(rotulo);
                if (operadorRel.equals(">")) GeraCod("BLE", rotulo);
                else if (operadorRel.equals("<")) GeraCod("BGE", rotulo);
                else if (operadorRel.equals(">=")) GeraCod("BLT", rotulo);
                else if (operadorRel.equals("<=")) GeraCod("BGT", rotulo);
                else if (operadorRel.equals("==")) GeraCod("BNE", rotulo);
                else if (operadorRel.equals("!=")) GeraCod("BEQ", rotulo);
                break;
            }
            case 66: { // fecha bloco verdadeiro
                String rotulo = pilhaRotulo.pop();
                GeraCod(rotulo + ":", "");
                break;
            }
            case 67: { // fim do if-else
                if (!pilhaRotulo.isEmpty()) GeraCod(pilhaRotulo.pop() + ":", "");
                break;
            }
            case 68: { // else
                String rotuloIfFalso = pilhaRotulo.pop();
                String rotuloFim = newRotulo();
                pilhaRotulo.push(rotuloFim);
                GeraCod("JMP", rotuloFim);
                GeraCod(rotuloIfFalso + ":", "");
                break;
            }
            case 69: { // início while/do
                String rotuloInicio = newRotulo();
                pilhaRotulo.push(rotuloInicio);
                GeraCod(rotuloInicio + ":", "");
                break;
            }
            case 70: { // fim while
                String rotuloFim = pilhaRotulo.pop();
                String rotuloInicio = pilhaRotulo.pop();
                GeraCod("JMP", rotuloInicio);
                GeraCod(rotuloFim + ":", "");
                break;
            }
            case 71: { // fim do do-while
                String rotuloInicio = pilhaRotulo.pop();
                if (operadorRel.equals(">")) GeraCod("BGT", rotuloInicio);
                else if (operadorRel.equals("<")) GeraCod("BLT", rotuloInicio);
                else if (operadorRel.equals(">=")) GeraCod("BGE", rotuloInicio);
                else if (operadorRel.equals("<=")) GeraCod("BLE", rotuloInicio);
                else if (operadorRel.equals("==")) GeraCod("BEQ", rotuloInicio);
                else if (operadorRel.equals("!=")) GeraCod("BNE", rotuloInicio);
                break;
            }
            case 72: { // início do for (teste rel.)
                isFor = true;
                forRotuloInicio = newRotulo();
                forRotuloFim = newRotulo();

                if (operadorRel.equals(">")) GeraCod("BLE", forRotuloFim);
                else if (operadorRel.equals("<")) GeraCod("BGE", forRotuloFim);
                else if (operadorRel.equals(">=")) GeraCod("BLT", forRotuloFim);
                else if (operadorRel.equals("<=")) GeraCod("BGT", forRotuloFim);
                else if (operadorRel.equals("==")) GeraCod("BNE", forRotuloFim);
                else if (operadorRel.equals("!=")) GeraCod("BEQ", forRotuloFim);

                // insere rótulo de início antes do corpo
                String[] linhas = codigoGerado.split("\n");
                int posInsercao = -1;
                outer:
                for (int i = linhas.length - 1; i >= 0; --i) {
                    String l = linhas[i].trim();
                    if (l.startsWith("STO") && !l.contains("temp")) {
                        for (int j = i + 1; j < linhas.length; ++j) {
                            String lj = linhas[j].trim();
                            if (lj.startsWith("LD") || lj.startsWith("LDI")) { posInsercao = j; break outer; }
                        }
                        break;
                    }
                }
                if (posInsercao > 0) {
                    StringBuilder novo = new StringBuilder();
                    for (int i = 0; i < linhas.length; ++i) {
                        if (i == posInsercao) {
                            if (novo.length() > 0) novo.append("\n");
                            novo.append(forRotuloInicio).append(":");
                        }
                        if (i > 0 || !linhas[i].isEmpty()) {
                            if (novo.length() > 0 && !linhas[i].isEmpty()) novo.append("\n");
                            novo.append(linhas[i]);
                        }
                    }
                    codigoGerado = novo.toString();
                }

                capturandoIncrementoFor = true;
                codigoGeradoBackup = codigoGerado;
                break;
            }
            case 73: { // fim do incremento do for
                if (capturandoIncrementoFor) {
                    String incremento = codigoGerado.substring(codigoGeradoBackup.length());
                    codigoGerado = codigoGeradoBackup;
                    forCodigoIncremento.clear();
                    if (!incremento.trim().isEmpty()) forCodigoIncremento.add(incremento);
                    capturandoIncrementoFor = false;
                }
                isFor = false;
                contadorOperando = 0;
                if (contadorTemporarios != 0) { contadorTemporarios = 0; freeTemp(); }
                break;
            }
            case 74: { // fecha for
                if (!forCodigoIncremento.isEmpty() || !forRotuloInicio.isEmpty()) {
                    for (String inc : forCodigoIncremento) codigoGerado = codigoGerado + inc;
                    GeraCod("JMP", forRotuloInicio);
                    GeraCod(forRotuloFim + ":", "");
                    forCodigoIncremento.clear();
                    forRotuloInicio = "";
                    forRotuloFim = "";
                }
                break;
            }
            case 75: { // chamada de função (prepara)
                variavel.setFlagFuncao(true);
                nomeChamada = token.getLexeme();
                contadorParametro = 0;
                Integer aux = tabelaSimbolos.setUsada(variavel, pilhaEscopo);
                Integer tipoFuncao = tabelaSimbolos.getTipo(variavel.getId(), variavel.getFlagFuncao(),
                        variavel.getFlagVetor(), pilhaEscopo);
                if (tipoFuncao == -1) { // void
                    chamadaFuncaoVoid = true;
                } else {
                    chamadaFuncaoVoid = false;
                    pilhaTipo.push(tipoFuncao);
                }
                if (aux == -1) throw new SemanticError(String.format("Variavel %s não declarada\n", variavel.getId()), token.getPosition());
                if (aux == 0) warnings += String.format("Variavel %s utilizada sem ser inicializada\n", variavel.getId());
                break;
            }
            case 76: { // efetiva a chamada
                if (contadorParametro != tabelaSimbolos.getNumParametros(nomeChamada)) {
                    throw new SemanticError("Erro na passagem de parametros", token.getPosition());
                }
                GeraCod("CALL", "_" + nomeChamada);
                chamadaFuncaoVoid = false;
                break;
            }
            case 77: { // passagem de parâmetro real
                Simbolo.Parametro p = tabelaSimbolos.getParametro(nomeChamada, contadorParametro);
                if (p != null) {
                    Integer tipoParam = tipoToInt(p.getTipo());
                    if (!pilhaTipo.isEmpty()) {
                        Integer tipoArgumento = pilhaTipo.pop();
                        if (SemanticTable.atribType(tipoParam, tipoArgumento) == -1) {
                            throw new SemanticError("Tipo incompatível na passagem de parâmetro", token.getPosition());
                        }
                        GeraCod("STO", nomeChamada + "_" + p.getId());
                        contadorParametro++;
                    }
                }
                break;
            }
            case 78: { // ++/--
                String nomeVar = variavel.getId();
                if (isParametroDaFuncaoAtual(variavel.getId())) nomeVar = funcaoAtual + "_" + variavel.getId();
                if (token.getLexeme().equals("++")) {
                    GeraCod("LD", nomeVar); GeraCod("ADDI", "1"); GeraCod("STO", nomeVar);
                } else {
                    GeraCod("LD", nomeVar); GeraCod("SUBI", "1"); GeraCod("STO", nomeVar);
                }
                break;
            }
            case 79: { // guarda parte esquerda para comparação relacional
                GeraCod("STO", "temp2"); temp2Usado = true;
                GeraCod("LD", "temp1");
                GeraCod("SUB", "temp2");
                break;
            }
            case 80: { // resolve pendências aritméticas (*,/,+,-)
                if (!calculoIndice) {
                    while (!pilhaOperacao.isEmpty()) {
                        int tipo2 = pilhaTipo.pop();
                        ultimoOperador = pilhaOperacao.pop();
                        int tipo1 = pilhaTipo.pop();
                        int resultExp = SemanticTable.resultType(tipo1, tipo2, ultimoOperador);
                        if (resultExp == -1) throw new SemanticError("Operação de tipos incompativeis");
                        pilhaTipo.push(resultExp);

                        // * via somas; / via subtrações (máquina simples)
                        if (ultimoOperador == 2) {
                            GeraCod("STO", "temp1"); temp1Usado = true;
                            GeraCod("LDI", "0"); GeraCod("STO", "temp2"); temp2Usado = true;
                            String loop = newRotulo(), end = newRotulo();
                            GeraCod(loop + ":", "");
                            GeraCod("LD", "temp1"); GeraCod("BEQ", end);
                            GeraCod("LD", "temp2"); GeraCod("ADD", "temp0"); getTemp(); GeraCod("STO", "temp2");
                            GeraCod("LD", "temp1"); GeraCod("SUBI", "1"); GeraCod("STO", "temp1");
                            GeraCod("JMP", loop); GeraCod(end + ":", ""); GeraCod("LD", "temp2");
                        } else if (ultimoOperador == 3) {
                            GeraCod("STO", "temp1"); temp1Usado = true;
                            GeraCod("LDI", "0"); GeraCod("STO", "temp2"); temp2Usado = true;
                            String loop = newRotulo(), end = newRotulo();
                            GeraCod(loop + ":", "");
                            GeraCod("LD", "temp0"); getTemp(); GeraCod("SUB", "temp1"); GeraCod("BLT", end);
                            GeraCod("STO", "temp0");
                            GeraCod("LD", "temp2"); GeraCod("ADDI", "1"); GeraCod("STO", "temp2");
                            GeraCod("JMP", loop); GeraCod(end + ":", ""); GeraCod("LD", "temp2");
                        }
                    }
                }
                break;
            }
            case 81: { // LIT_INT
                pilhaTipo.push(0);
                if (calculoIndice) temporario.setTamanho(Integer.parseInt(token.getLexeme()));
                if ((!isDeclaracao || nomeVet == null || nomeVet.isEmpty()) && !processandoReturn) {
                    if (!flagOperacao) {
                        GeraCod("LDI", token.getLexeme());
                    } else {
                        if (pilhaOperacao.peek() == 0) GeraCod("ADDI", token.getLexeme());
                        else if (pilhaOperacao.peek() == 1) GeraCod("SUBI", token.getLexeme());
                        flagOperacao = false; contadorOperando++;
                    }
                }
                flagOperacao = false;
                break;
            }
            case 82: pilhaTipo.push(1); break; // float
            case 83: pilhaTipo.push(2); break; // char
            case 84: pilhaTipo.push(3); break; // string
            case 85: pilhaTipo.push(4); break; // bool
            case 86: { // uso de variável/identificador
                Integer aux = tabelaSimbolos.setUsada(variavel, pilhaEscopo);
                String nomeVar = variavel.getId();
                if (isParametroDaFuncaoAtual(variavel.getId())) nomeVar = funcaoAtual + "_" + variavel.getId();

                pilhaTipo.push(tabelaSimbolos.getTipo(variavel.getId(), variavel.getFlagFuncao(),
                        variavel.getFlagVetor(), pilhaEscopo));
                if (pilhaTipo.peek() == -1) {
                    if (variavel.getFlagFuncao()) throw new SemanticError("Atribuição de função sem retorno", token.getPosition());
                    throw new SemanticError("Variável '" + variavel.getId() + "' não encontrada no escopo atual", token.getPosition());
                }
                if (aux == -1) throw new SemanticError(String.format("Variavel %s não declarada", variavel.getId()), token.getPosition());
                if (aux == 0) warnings += String.format("Variavel %s utilizada sem ser inicializada\n", variavel.getId());

                if (!variavel.getFlagVetor()) {
                    if (!flagOperacao) {
                        GeraCod("LD", nomeVar); contadorOperando++;
                    } else {
                        if (pilhaOperacao.peek() == 0) GeraCod("ADD", nomeVar);
                        else if (pilhaOperacao.peek() == 1) GeraCod("SUB", nomeVar);
                        flagOperacao = false;
                    }
                } else {
                    if (!flagOperacao) {
                        GeraCod("STO", "$indr");
                        GeraCod("LDV", nomeVar);
                    } else {
                        GeraCod("STO", "$indr");
                        GeraCod("LDV", nomeVar);
                        flagOperacao = false;
                        temVetor = true;
                    }
                    contadorOperando++;
                }
                break;
            }
            case 87: { // operador relacional
                pilhaOperacao.push(4);
                operadorRel = token.getLexeme();
                GeraCod("STO", "temp1"); temp1Usado = true;
                break;
            }
            case 88: pilhaOperacao.push(0); flagOperacao = true; break; // +
            case 89: pilhaOperacao.push(1); flagOperacao = true; break; // -
            case 90: pilhaOperacao.push(2); flagOperacao = true; break; // *
            case 91: pilhaOperacao.push(3); flagOperacao = true; break; // /
        }
    }

    // copia campos de 'src' para 'dst'
    private void copia(Simbolo src, Simbolo dst) {
        dst.setId(src.getId());
        dst.setEscopo(src.getEscopo());
        dst.setFlagVetor(src.getFlagVetor());
        dst.setFlagFuncao(src.getFlagFuncao());
        dst.setFlagParametro(src.getFlagParametro());
        dst.setFlagInicializada(src.getFlagInicializada());
        dst.setFlagUsada(src.getFlagUsada());
        dst.setTamanho(src.getTamanho());
        dst.setTipo(src.getTipo());
    }

    private void freeTemp() {
        for (Temp t : tabelaTemp) t.setLivre(true);
    }

    private int getTemp() {
        for (int i = 0; i < tabelaTemp.size(); ++i) {
            if (tabelaTemp.get(i).livre) {
                tabelaTemp.get(i).setLivre(false);
                return i;
            }
        }
        Temp novo = new Temp();
        novo.setIndice(tabelaTemp.size());
        novo.setLivre(false);
        tabelaTemp.add(novo);
        return novo.getIndice();
    }

    private String newRotulo() {
        contadorRotulo++;
        return "R" + contadorRotulo;
    }

    public TabelaSimbolos getTabelaSimbolos() { return tabelaSimbolos; }

    private Integer tipoToInt(String tipo) {
        return switch (tipo) {
            case "int" -> 0;
            case "float" -> 1;
            case "char" -> 2;
            case "string" -> 3;
            case "bool" -> 4;
            case "void" -> -1;
            default -> -1;
        };
    }

    private boolean isParametroDaFuncaoAtual(String nomeVar) {
        if (dentroFuncao && !funcaoAtual.isEmpty()) {
            List<Simbolo.Parametro> parametros = tabelaSimbolos.getParametrosFuncao(funcaoAtual);
            if (parametros != null) {
                for (Simbolo.Parametro p : parametros) {
                    if (p.getId().equals(nomeVar)) return true;
                }
            }
        }
        return false;
    }

    public String GeraDataSeccion() {
        String dataSeccion = "";
        List<String> variaveisProcessadas = new ArrayList<>();

        for (Simbolo simb : tabelaSimbolos.getListaSimbolos()) {
            if (!simb.getFlagFuncao()) {
                String nomeVariavel = simb.getId();

                if (simb.getFlagParametro()) {
                    for (Simbolo funcao : tabelaSimbolos.getListaSimbolos()) {
                        if (funcao.getFlagFuncao()) {
                            List<Simbolo.Parametro> parametros = tabelaSimbolos.getParametrosFuncao(funcao.getId());
                            if (parametros != null) {
                                for (Simbolo.Parametro p : parametros) {
                                    if (p.getId().equals(simb.getId())) {
                                        nomeVariavel = funcao.getId() + "_" + simb.getId();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (!variaveisProcessadas.contains(nomeVariavel)) {
                    variaveisProcessadas.add(nomeVariavel);
                    if (!simb.getFlagVetor()) {
                        dataSeccion += "\t" + nomeVariavel + " : 0\n";
                    } else {
                        dataSeccion += "\t" + nomeVariavel + " : ";
                        for (int i = 0; i < simb.getTamanho(); ++i) {
                            dataSeccion += (i == 0 ? "0" : ", 0");
                        }
                        dataSeccion += "\n";
                    }
                }
            }
        }

        if (temp1Usado) dataSeccion += "\ttemp1 : 0\n";
        if (temp2Usado) dataSeccion += "\ttemp2 : 0\n";
        for (Temp t : tabelaTemp) dataSeccion += "\ttemp" + t.getIndice() + " : 0\n";

        return ".data\n" + dataSeccion;
    }

    public void GeraCod(String nome, String valor) {
        if (nome.endsWith(":")) {
            codigoGerado += "\n" + nome;
        } else if (valor.isEmpty()) {
            codigoGerado += "\n\t" + nome;
        } else {
            codigoGerado += "\n\t" + nome + " " + valor;
        }
    }

    public String getCodigoGerado() {
        String resultado = GeraDataSeccion();
        resultado += ".text\n";
        resultado += "\tJMP _" + nomeFuncaoPrincipal + "\n";
        resultado += codigoGerado;
        return resultado;
    }

    public static class Temp {
        private boolean livre = true;
        private int indice;

        public boolean getLivre() { return livre; }
        public void setLivre(boolean livre) { this.livre = livre; }
        public int getIndice() { return indice; }
        public void setIndice(int indice) { this.indice = indice; }
    }
}
