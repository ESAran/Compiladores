# Compiladores - Analisador Léxico, Sintático e Semântico

Este projeto implementa um analisador léxico, sintático e semântico em Java, desenvolvido para a disciplina de Linguagens Formais e Autômatos (LFA).

## Como Funciona o Compilador

### 1. Análise Léxica (Tokens e Padrões)

A análise léxica é a primeira etapa do processo de compilação. É como se fosse um "scanner" que lê o código-fonte caractere por caractere e identifica os elementos básicos da linguagem, chamados de **tokens**.

**Tokens reconhecidos**:

- **Identificadores**: `[a-zA-Z_][a-zA-Z0-9_]*` - reconhece nomes de variáveis como `a`, `b`, `contador`
- **Números inteiros**: `[0-9]+` - reconhece valores como `2`, `8`, `123`
- **Números decimais**: `[0-9]+\.[0-9]+` - reconhece valores como `3.14`, `2.5`
- **Operadores aritméticos**: `+`, `-`, `*`, `/`, `%`, `**` (potência), `%%` (logaritmo)
- **Palavras reservadas**: `exhibere` (equivalente ao `print`)
- **Delimitadores**: `{`, `}`, `(`, `)`, `;`, `=`

**Exemplo**: No código `a = 2 + 8;`, o analisador léxico identifica:

- `a` → ID, `=` → REL_EQUAL, `2` → LIT_INT, `+` → OP_SUM, `8` → LIT_INT, `;` → DEL_SEMICOLON

### 2. Análise Sintática (Estrutura da Linguagem)

A análise sintática verifica se a sequência de tokens segue as regras gramaticais da linguagem. É como verificar se uma frase está bem estruturada gramaticalmente.

**Estrutura da linguagem**:

- Todo programa deve estar dentro de um bloco `{ }`
- Comandos permitidos: Atribuições e comandos de print
- Expressões aritméticas seguem precedência matemática correta

**Hierarquia das expressões**:

```
<exp> → <exp_add> → <exp_mul> → <exp_pow> → <exp_post> → <prim>
```

Isso garante que `2 + 3 * 4` seja interpretado como `2 + (3 * 4)`.

### 3. Implementação em Java

**Arquitetura**:

- **Runner.java**: Orquestrador que lê o arquivo e executa a análise completa
- **Lexico.java**: Implementa um autômato finito determinístico para reconhecer tokens
- **Sintatico.java**: Parser LR que usa pilha e tabela de parsing
- **Semantico.java**: Verifica regras de contexto (variáveis declaradas, tipos compatíveis)

**Fluxo de execução**:

1. Léxico transforma código em tokens
2. Sintático verifica se a sequência segue a gramática
3. Semântico verifica regras de contexto
4. Se tudo estiver correto, a análise é bem-sucedida

## Estrutura do Projeto

- **src/**: Contém todos os arquivos-fonte Java e o arquivo de entrada `programa.txt`.
  - `Lexico.java`, `Sintatico.java`, `Semantico.java`: Implementações dos analisadores léxico, sintático e semântico.
  - `Runner.java`: Classe principal para execução do analisador.
  - `Token.java`, `Constants.java`, `ParserConstants.java`, `ScannerConstants.java`: Definições de tokens e constantes.
  - `LexicalError.java`, `SyntacticError.java`, `SemanticError.java`, `AnalysisError.java`: Classes para tratamento de erros.
  - `programa.txt`: Exemplo de código-fonte a ser analisado.
- **WebGals/**: Documentação gerada pelas ferramentas WebGals.
  - `Analisador Léxico.txt`, `Analisador Sintático.txt`: Relatórios dos analisadores.
- `LFA.iml`: Arquivo de configuração do projeto para IDEs como IntelliJ IDEA.

## Como Executar

1. Compile todos os arquivos Java dentro da pasta `src`:
   ```powershell
   cd src
   javac *.java
   ```
2. Execute a classe principal:
   ```powershell
   java Runner
   ```

O analisador irá ler o arquivo `programa.txt` e processar as etapas léxica, sintática e semântica, exibindo os resultados e eventuais erros encontrados.

## Requisitos

- Java 8 ou superior
- (Opcional) IDE como IntelliJ IDEA ou VS Code para facilitar o desenvolvimento

## Créditos

Desenvolvido por Eduardo Aran para a disciplina de LFA.

---

Sinta-se à vontade para modificar e adaptar este projeto conforme necessário para seus estudos ou aplicações!
