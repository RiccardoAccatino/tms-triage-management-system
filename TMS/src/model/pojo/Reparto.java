package model.pojo;

public class Reparto {
    private int idReparto;
    private String nome;
    private String codice; // Es. "CARDIO"
    private int sale;      // Numero di sale disponibili

    public Reparto() {}

    public Reparto(int idReparto, String nome, String codice, int sale) {
        this.idReparto = idReparto;
        this.nome = nome;
        this.codice = codice;
        this.sale = sale;
    }

    public int getIdReparto() { return idReparto; }
    public void setIdReparto(int idReparto) { this.idReparto = idReparto; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodice() { return codice; }
    public void setCodice(String codice) { this.codice = codice; }

    public int getSale() { return sale; }
    public void setSale(int sale) { this.sale = sale; }
}