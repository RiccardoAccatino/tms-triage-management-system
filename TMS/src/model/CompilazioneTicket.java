package model;

import model.dao.TicketDao;
import model.pojo.Ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CompilazioneTicket {

    private TicketDao ticketDao;

    // Costruttore predefinito
    public CompilazioneTicket() {
        this.ticketDao = new TicketDao();
    }

    // Costruttore per test (Dependency Injection)
    public CompilazioneTicket(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }
    public Ticket creaTicket(String colore, int priorita, String sintomi) {

        // 1. CONTROLLO SESSIONE E SICUREZZA
        Sessione sessione = Sessione.getInstance();

        // Verifica che l'utente sia effettivamente un Paziente
        if (!sessione.isPaziente()) {
            throw new SecurityException("Operazione non consentita: Nessun paziente loggato.");
        }

        // 2. RECUPERO ID DALLA SESSIONE
        // Dato che isPaziente() è true, possiamo usare getPaziente() in sicurezza
        int idPaziente = sessione.getPaziente().getIdUtente();

        // 3. CREAZIONE DEL TICKET
        Ticket nuovoTicket = new Ticket();

        nuovoTicket.setIdPaziente(idPaziente); // Usiamo l'ID recuperato dalla sessione
        nuovoTicket.setColore(colore);
        nuovoTicket.setPriorita(priorita);
        nuovoTicket.setSintomi(sintomi);

        nuovoTicket.setStato("IN_ATTESA");

        // Generazione Timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        nuovoTicket.setTimestamp(now.format(formatter));

        // 4. SALVATAGGIO
        ticketDao.save(nuovoTicket);

        return nuovoTicket;
    }
}