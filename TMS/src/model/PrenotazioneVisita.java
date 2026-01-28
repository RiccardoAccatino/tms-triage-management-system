package model;

/**
 * RF1 - Prenotazione visita
 * L’utente esterno deve poter prenotare le visite con i dottori
 *
 * @author angie albitres
 */
public class PrenotazioneVisita {
    private int idPaziente;
    private int idDottore;
    private int idReparto;
    private String dataOraRichiesta;
    private String motivo;
    private String stato;

    // Costruttore
    public PrenotazioneVisita(int idPaziente, int idDottore, int idReparto, String dataOraRichiesta, String motivo) {
        this.idPaziente = idPaziente;
        this.idDottore = idDottore;
        this.idReparto = idReparto;
        this.dataOraRichiesta = dataOraRichiesta;
        this.motivo = motivo;
        this.stato = "In Attesa";
    }

    // Getters e Setters
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