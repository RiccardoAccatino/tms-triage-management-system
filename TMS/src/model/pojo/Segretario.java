package model.pojo;

public class Segretario extends Utente {
    private int livelloPermessi;

    public Segretario() {
        super();
        this.setTipoUtente("SEGRETARIO");
    }

    public Segretario(int idUtente, String nome, String cognome, String dataNascita, int livelloPermessi) {
        super(idUtente, nome, cognome, dataNascita, "SEGRETARIO");
        this.livelloPermessi = livelloPermessi;
    }

    public int getLivelloPermessi() { return livelloPermessi; }
    public void setLivelloPermessi(int livelloPermessi) { this.livelloPermessi = livelloPermessi; }
}