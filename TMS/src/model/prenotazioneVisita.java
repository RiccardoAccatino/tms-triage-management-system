package model;

import model.dao.PazienteDao;
import model.dao.TicketDao;
import model.pojo.Paziente;
import model.pojo.Ticket;
import model.pojo.Visita;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RF1 - Prenotazione visita
 * L’utente esterno deve poter prenotare le visite con i dottori
 *
 * @author angie albitres
 */
public class prenotazioneVisita extends Visita {

    private final PazienteDao pazienteDao;
    private final TicketDao ticketDao;

    // Costruttore per i test, senza utilizzo del DB
    public prenotazioneVisita(PazienteDao pDao, TicketDao tDao) {
        this.pazienteDao = pDao;
        this.ticketDao = tDao;
    }

    // Registrazione della prenotazione
    public void registraPrenotazione(Paziente p) {
        // salviamo il paziente per ottenere il suo ID generato dal DB
        pazienteDao.save(p);

        // prepariamo il Ticket basandoci sui dati di questa prenotazione
        Ticket t = new Ticket();
        t.setIdPaziente(p.getIdUtente());
        t.setColore("Bianco");            // non urgente
        t.setPriorita(1);                 // priorità standard

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        t.setTimestamp(LocalDateTime.now().format(dtf));

        // Salviamo il ticket nel database
        ticketDao.save(t);

        // Aggiorniamo i campi ereditati
        this.setIdTicket(t.getIdTicket());
        this.setIdPaziente(p.getIdUtente());

        System.out.println("Prenotazione registrata con successo: Ticket #" + t.getIdTicket());
    }
}