package model.pojo;

public class Dottore extends Utente {
    private String nome;

    private String matricola;
    private String turni;
    private int idReparto; // Riferimento al reparto

    public Dottore() {
        super();
        this.setTipoUtente("DOTTORE");
    }

    public Dottore(int idUtente, String nome, String cognome, String dataNascita, String matricola, String turni, int idReparto) {
        super(idUtente, nome, cognome, dataNascita, "DOTTORE");
        this.matricola = matricola;
        this.turni = turni;
        this.idReparto = idReparto;
    }

    public String getMatricola() { return matricola; }
    public void setMatricola(String matricola) { this.matricola = matricola; }

    public String getTurni() { return turni; }
    public void setTurni(String turni) { this.turni = turni; }

    public int getIdReparto() { return idReparto; }
    public void setIdReparto(int idReparto) { this.idReparto = idReparto; }
}