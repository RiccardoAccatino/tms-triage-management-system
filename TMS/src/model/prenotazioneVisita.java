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
 *
 * @author angie albitres
 */
public class prenotazioneVisita extends Visita {

    private final PazienteDao pazienteDao;
    private final TicketDao ticketDao;

    // Costruttore per i TEST (ci permette di inserire i DAO finti)
    public prenotazioneVisita(PazienteDao pDao, TicketDao tDao) {
        this.pazienteDao = pDao;
        this.ticketDao = tDao;
    }

    public void registraPrenotazione(Paziente p) {
        // 1. Salviamo il paziente per ottenere il suo ID generato dal DB
        // Il metodo save gestisce già la transazione tra le tabelle 'utente' e 'paziente'
        pazienteDao.save(p);

        // 2. Prepariamo il Ticket basandoci sui dati di questa prenotazione
        Ticket t = new Ticket();
        t.setIdPaziente(p.getIdUtente()); // Usiamo l'ID appena genera testo dai requisiti
        t.setColore("Bianco");            // Codice per prenotazioni non urgenti
        t.setPriorita(1);                 // Priorità standard bassa

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