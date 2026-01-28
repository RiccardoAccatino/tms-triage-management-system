package model.pojo;
/**
 * @author Accatino Riccardo
 */
public class Paziente extends Utente {
    private String codiceFiscale;
    private String indirizzo;

    public Paziente() {
        super();
        this.setTipoUtente("PAZIENTE");
    }

    public Paziente(int idUtente, String nome, String cognome, String dataNascita, String codiceFiscale, String indirizzo) {
        super(idUtente, nome, cognome, dataNascita, "PAZIENTE");
        this.codiceFiscale = codiceFiscale;
        this.indirizzo = indirizzo;
    }

    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
}