package model.pojo;

/**
 * @author angie
 */
public class Visita {
    private int idVisita;
    private String dataOraInizio;
    private String dataOraFine;
    private String sala;
    private int idTicket;
    private int idDottore;
    private int idPaziente;
    private int idReparto;

    // Costruttore
    public Visita(int idVisita, String dataOraInizio, String dataOraFine, String sala, int idTicket, int idDottore, int idPaziente, int idReparto) {
        this.idVisita = idVisita;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
        this.sala = sala;
        this.idTicket = idTicket;
        this.idDottore = idDottore;
        this.idPaziente = idPaziente;
        this.idReparto = idReparto;
    }


    public Visita(){
        this(0, "", "", "", 0, 0, 0, 0);
    }

    //Getters e Setters
    public int getIdVisita() { return idVisita; }
    public void setIdVisita(int idVisita) { this.idVisita = idVisita; }

    public String getDataOraInizio() { return dataOraInizio; }
    public void setDataOraInizio(String dataOraInizio) { this.dataOraInizio = dataOraInizio; }

    public String getDataOraFine() { return dataOraFine; }
    public void setDataOraFine(String dataOraFine) { this.dataOraFine = dataOraFine; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public int getIdTicket() { return idTicket; }
    public void setIdTicket(int idTicket) { this.idTicket = idTicket; }

    public int getIdDottore() { return idDottore; }
    public void setIdDottore(int idDottore) { this.idDottore = idDottore; }

    public int getIdPaziente() { return idPaziente; }
    public void setIdPaziente(int idPaziente) { this.idPaziente = idPaziente; }

    public int getIdReparto() { return idReparto; }
    public void setIdReparto(int idReparto) { this.idReparto = idReparto; }
}