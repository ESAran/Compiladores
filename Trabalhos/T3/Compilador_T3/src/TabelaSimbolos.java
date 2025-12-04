
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class TabelaSimbolos {
    public List<Simbolo> listaSimbolos = new ArrayList<Simbolo>();

    public List<Simbolo> getListaSimbolos() {
        return listaSimbolos;
    }

    public void setListaSimbolos(List<Simbolo> listaSimbolos) {
        this.listaSimbolos = listaSimbolos;
    }

    public Boolean add(Simbolo simbolo) {
        return listaSimbolos.add(new Simbolo(
                simbolo.getTipo(),
                simbolo.getId(),
                simbolo.getEscopo(),
                simbolo.getFlagVetor(),
                simbolo.getFlagFuncao(),
                simbolo.getFlagParametro(),
                simbolo.getFlagInicializada(),
                simbolo.getFlagUsada(),
                simbolo.getTamanho(),
                simbolo.getValor()
        ));
    }

    public Boolean isVetorById(String id) {
        System.out.println("id = " + id);
        return listaSimbolos.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .map(Simbolo::getFlagVetor)
                .orElse(null); // ou .orElse(false)
    }


    public Boolean declarar(Simbolo simbolo, Stack<Integer> pilhaEscopo) {
        for (Integer escopo : pilhaEscopo) {
            for (Simbolo s : listaSimbolos) {
                if (s.getId().equals(simbolo.getId()) && s.getEscopo().equals(escopo)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Simbolo.Parametro> getParametrosFuncao(String nomeFuncao) {
        for (Simbolo s : listaSimbolos) {
            if (s.getId().equals(nomeFuncao) && s.getFlagFuncao()) {
                return s.getParametros();
            }
        }
        return null;
    }


    public Boolean declarada(Simbolo simbolo, Stack<Integer> pilhaEscopo) {
        for (Simbolo s : listaSimbolos) {
            for (Integer escopo : pilhaEscopo) {
                if (s.getId().equals(simbolo.getId()) && s.getEscopo().equals(escopo)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addParametro(String idFunc, String idParametro, String tipoParametro, Boolean isVet) {
        for (Simbolo s : listaSimbolos) {
            if (s.getId().equals(idFunc) && s.getFlagFuncao()) {
                s.addParemetro(idParametro, tipoParametro, isVet);
            }
        }
    }

    public  Simbolo.Parametro getParametro(String idFunc, Integer pos) {
        for (Simbolo s : listaSimbolos) {
            if (s.getId().equals(idFunc) && s.getFlagFuncao()) {
                return s.getParametros().get(pos);
            }
        }
        return null;
    }

    public Integer getNumParametros(String idFunc) {
        for (Simbolo s : listaSimbolos) {
            if (s.getId().equals(idFunc) && s.getFlagFuncao()) {
                return s.getParametros().size();
            }
        }
        return 0;
    }

    public Integer setUsada(Simbolo simbolo, Stack<Integer> pilhaEscopo) {
        if (!simbolo.getFlagFuncao()) {
            List<Simbolo> simbolos;

            if (!simbolo.getFlagVetor()) {
                simbolos = listaSimbolos.stream().filter(
                        s -> (s.getId().equals(simbolo.getId())) &&
                                (pilhaEscopo.search(s.getEscopo()) != -1) &&
                                (!s.getFlagFuncao()) &&
                                (!s.getFlagVetor())
                ).collect(Collectors.toList());
            } else {
                simbolos = listaSimbolos.stream().filter(
                        s -> (s.getId().equals(simbolo.getId())) &&
                                (pilhaEscopo.search(s.getEscopo()) != -1) &&
                                (!s.getFlagFuncao()) &&
                                (s.getFlagVetor())
                ).collect(Collectors.toList());
            }

            if (!simbolos.isEmpty()) {
                Simbolo ultimo = simbolos.get(simbolos.size() - 1);
                ultimo.setFlagUsada(true);
                if (!ultimo.getFlagInicializada())
                    return 0;
                return 1;
            }

            return -1;
        }

        List<Simbolo> simboloFromList = listaSimbolos.stream().filter(
                s -> (s.getId().equals(simbolo.getId())) && (s.getFlagFuncao())
        ).collect(Collectors.toList());

        if (!simboloFromList.isEmpty()) {
            simboloFromList.get(0).setFlagUsada(true);
            return 1;
        }

        return -1;
    }

    public Boolean setInicializada(String id, Boolean isVet, Integer valor) {
        List<Simbolo> simbolos;
        if (!isVet) {
            simbolos = listaSimbolos.stream().filter(
                    s -> (s.getId().equals(id)) && (!s.getFlagVetor())
            ).collect(Collectors.toList());
        } else {
            simbolos = listaSimbolos.stream().filter(
                    s -> (s.getId().equals(id)) && (s.getFlagVetor())
            ).collect(Collectors.toList());
        }

        if (!simbolos.isEmpty()) {
            Simbolo ultimo = simbolos.get(simbolos.size() - 1);
            ultimo.setFlagInicializada(true);
            if (!isVet) {
                ultimo.setValor(valor);
            }
            return true;
        }

        return false;
    }

    public int getTipo(String id, Boolean isFuncao, Boolean isVet, Stack<Integer> pilhaEscopo) {
        String tipo;

        if (!isFuncao) {
            if (!isVet) {
                List<Simbolo> simbolos = listaSimbolos.stream().filter(
                        s -> (s.getId().equals(id)) &&
                                (pilhaEscopo.search(s.getEscopo()) != -1) &&
                                (!s.getFlagFuncao()) &&
                                (!s.getFlagVetor())
                ).collect(Collectors.toList());

                if (simbolos.isEmpty()) return -1;

                tipo = simbolos.get(simbolos.size() - 1).getTipo();
            } else {
                List<Simbolo> simbolos = listaSimbolos.stream().filter(
                        s -> (s.getId().equals(id)) &&
                                (pilhaEscopo.search(s.getEscopo()) != -1) &&
                                (!s.getFlagFuncao()) &&
                                (s.getFlagVetor())
                ).collect(Collectors.toList());

                if (simbolos.isEmpty()) return -1;

                tipo = simbolos.get(simbolos.size() - 1).getTipo();
            }

        } else {
            List<Simbolo> simbolos = listaSimbolos.stream().filter(
                    s -> (s.getId().equals(id)) && (s.getFlagFuncao())
            ).collect(Collectors.toList());

            if (simbolos.isEmpty()) return -1;

            tipo = simbolos.get(0).getTipo();
        }

        switch (tipo) {
            case "int": return 0;
            case "float": return 1;
            case "char": return 2;
            case "string": return 3;
            case "bool": return 4;
            case "void": return -1;
            default: return -1;
        }
    }

    public boolean existsInCurrentScope(String id, Integer currentScope) {
        for (Simbolo s : listaSimbolos) {
            if (s.getId().equals(id) && s.getEscopo().equals(currentScope)) {
                return true;
            }
        }
        return false;
    }
}
