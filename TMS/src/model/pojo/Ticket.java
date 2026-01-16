package model.pojo;

public class Ticket {
    private int idTicket;
    private String colore;    // "Bianco", "Verde", "Giallo", "Rosso"
    private int priorita;     // Es. 1 (bassa) a 4 (alta)
    private String sintomi;
    private String stato;     // "In Attesa", "Accettato", "Rifiutato"
    private String timestamp; // Data e ora creazione
    private int idPaziente;   // Chi ha creato il ticket

    public Ticket() {}

    public Ticket(int idTicket, String colore, int priorita, String sintomi, String stato, String timestamp, int idPaziente) {
        this.idTicket = idTicket;
        this.colore = colore;
        this.priorita = priorita;
        this.sintomi = sintomi;
        this.stato = stato;
        this.timestamp = timestamp;
        this.idPaziente = idPaziente;
    }

    public int getIdTicket() { return idTicket; }
    public void setIdTicket(int idTicket) { this.idTicket = idTicket; }

    public String getColore() { return colore; }
    public void setColore(String colore) { this.colore = colore; }

    public int getPriorita() { return priorita; }
    public void setPriorita(int priorita) { this.priorita = priorita; }

    public String getSintomi() { return sintomi; }
    public void setSintomi(String sintomi) { this.sintomi = sintomi; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public int getIdPaziente() { return idPaziente; }
    public void setIdPaziente(int idPaziente) { this.idPaziente = idPaziente; }
}