package model.pojo;

public class Utente {
    private int idUtente;
    private String nome;
    private String cognome;
    private String dataNascita; // Formato "YYYY-MM-DD"
    private String tipoUtente;  // "PAZIENTE", "DOTTORE", "SEGRETARIO"

    // Costruttore Vuoto
    public Utente() {}

    // Costruttore Pieno
    public Utente(int idUtente, String nome, String cognome, String dataNascita, String tipoUtente) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.tipoUtente = tipoUtente;
    }

    // Getter e Setter
    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getDataNascita() { return dataNascita; }
    public void setDataNascita(String dataNascita) { this.dataNascita = dataNascita; }

    public String getTipoUtente() { return tipoUtente; }
    public void setTipoUtente(String tipoUtente) { this.tipoUtente = tipoUtente; }

}