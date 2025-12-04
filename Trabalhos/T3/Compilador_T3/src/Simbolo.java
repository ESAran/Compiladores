import java.util.ArrayList;
import java.util.List;

public class Simbolo {
    private String tipo;
    private String id;
    private Integer escopo;
    private Boolean flagVetor;
    private Boolean flagFuncao;
    private Boolean flagParametro;
    private Boolean flagInicializada;
    private Boolean flagUsada;
    private Integer tamanho;
    private Integer valor;
    private List<Parametro> paremetros = new ArrayList<Parametro>();

    public Simbolo(){}

    public Simbolo(String tipo, String id, Integer escopo, Boolean flagVetor, Boolean flagFuncao, Boolean flagParametro, Boolean flagInicializada, Boolean flagUsada, Integer tamanho, Integer valor){
        this.tipo = tipo;
        this.id = id;
        this.escopo = escopo;
        this.flagVetor = flagVetor;
        this.flagFuncao = flagFuncao;
        this.flagParametro = flagParametro;
        this.flagInicializada = flagInicializada;
        this.flagUsada = flagUsada;
        this.tamanho = tamanho;
        this.valor = valor;
    }

    public void setTipo(String tipo){

        this.tipo = tipo;

    }

    public String getTipo(){

        return this.tipo;

    }

    public void setId(String id){

        this.id = id;

    }

    public String getId(){

        return this.id;

    }

    public void setEscopo(Integer escopo){

        this.escopo = escopo;

    }

    public Integer getEscopo(){

        return this.escopo;

    }

    public void setFlagVetor(Boolean flagVetor){

        this.flagVetor = flagVetor;

    }

    public Boolean getFlagVetor(){

        return this.flagVetor;

    }

    public void setFlagFuncao(Boolean flagFuncao){

        this.flagFuncao = flagFuncao;

    }

    public Boolean getFlagFuncao(){

        return this.flagFuncao;

    }

    public void setFlagParametro(Boolean flagParametro){

        this.flagParametro = flagParametro;

    }

    public Boolean getFlagParametro(){

        return this.flagParametro;

    }

    public void setFlagInicializada(Boolean flagInicializada){

        this.flagInicializada = flagInicializada;

    }

    public Boolean getFlagInicializada(){

        return this.flagInicializada;

    }

    public void setFlagUsada(Boolean flagUsada){

        this.flagUsada = flagUsada;

    }

    public Boolean getFlagUsada(){

        return this.flagUsada;

    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public List<Parametro> getParametros() {
        return paremetros;
    }

    public void setParemetros(List<Parametro> paremetros) {
        this.paremetros = paremetros;
    }

    public void addParemetro(String id, String tipo, Boolean isVet) {
        paremetros.add(new Parametro(id, tipo, isVet));
    }

    public class Parametro
    {
        private String id;
        private String tipo;
        private Boolean isVet;

        public Parametro(String id, String tipo, Boolean isVet) {
            this.id = id;
            this.tipo = tipo;
            this.isVet = isVet;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public Boolean getIsVet() {
            return isVet;
        }

        public void setIsVet(Boolean isVet) {
            this.isVet = isVet;
        }

    }

}
