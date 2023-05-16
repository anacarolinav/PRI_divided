class Documento {
    private int id;
    private String conteudo;
    private String nome;
    
    public Documento(int id, String conteudo, String nome) {
        this.id = id;
        this.conteudo = conteudo;
        this.nome = nome;
    }
    
    public int getId() {
        return id;
    }
    
    public String getConteudo() {
        return conteudo;
    }
    
    public String getNome() {
        return nome;
    }
}
