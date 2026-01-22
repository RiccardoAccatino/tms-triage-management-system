package model;

/**
 * RF1 - Prenotazione visita
 * L’utente esterno deve poter prenotare le visite con i dottori
 *
 * @author angie albitres
 */
public class PrenotazioneVisita {
    private int idPaziente; // l'utente inserisce i dati e sulla base di quelli viene creato new Paziente()(lo fa il controller: controllare che non esista già il paziente)
    private int idDottore;  // l'utente cerca tramite il nome e cognome il dottore, trovando l'id (lo fa il controller)
    private int idReparto;
    private String dataOraRichiesta;
    private String motivo; // diventaerà sintomi del Ticket
    private String stato;

    // Costruttore per i test, senza utilizzo del DB
    public PrenotazioneVisita(int idPaziente, int idDottore, int idReparto, String dataOraRichiesta, String motivo) {
        this.idPaziente = idPaziente;
        this.idDottore = idDottore;
        this.idReparto = idReparto;
        this.dataOraRichiesta = dataOraRichiesta;
        this.motivo = motivo;
        this.stato = "In Attesa";
    }

    public String getDataOraRichiesta() {
        return dataOraRichiesta;
    }

    public int getIdReparto() {
        return idReparto;
    }

    public int getIdDottore() {
        return idDottore;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getStato() {
        return stato;
    }
}