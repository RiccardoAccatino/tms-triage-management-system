package model;

/**
 * RF1 - Prenotazione visita
 *
 * @author angie albitres
 */
public class prenotazioneVisita extends Visita {

    private PazienteDao pazienteDao = new PazienteDao();
    private TicketDao ticketDao = new TicketDao();

    public PrenotazioneVisita() {
        super();
    }

    /**
     * Esegue la logica dell'RF1:
     * 1. Salva i dati anagrafici del paziente
     * 2. Crea un ticket "In Attesa" associato
     */
    public void registraPrenotazione(Paziente p) {
        // 1. Salviamo il paziente per ottenere il suo ID generato dal DB
        // Il metodo save gestisce già la transazione tra le tabelle 'utente' e 'paziente'
        pazienteDao.save(p);

        // 2. Prepariamo il Ticket basandoci sui dati di questa prenotazione
        Ticket t = new Ticket();
        t.setIdPaziente(p.getIdUtente()); // Usiamo l'ID appena generatoesto dai requisiti [cite: 167, 241]
        t.setColore("Bianco");            // Codice per prenotazioni non urgenti [cite: 31, 57]
        t.setPriorita(1);                 // Priorità standard bassa

        // Impostiamo la data e ora corrente come timestamp di creazione
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        t.setTimestamp(LocalDateTime.now().format(dtf));

        // 3. Salviamo il ticket nel database
        ticketDao.save(t);

        // Aggiorniamo i campi ereditati da Visita per riferimento futuro
        this.setIdTicket(t.getIdTicket());
        this.setIdPaziente(p.getIdUtente());

        System.out.println("RF1 - Prenotazione registrata con successo: Ticket #" + t.getIdTicket());
    }
}