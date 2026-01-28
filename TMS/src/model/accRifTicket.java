package model;

import model.dao.DottoreDao;
import model.dao.TicketDao;
import model.pojo.Ticket;
import model.exception.TicketException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  @author DAPPIANO FRANCESCO
 */
public class accRifTicket {
    private final TicketDao ticketDao;
    private DottoreDao visitaDao;

    public accRifTicket() {
        this.ticketDao = new TicketDao();
    }

    /**
     * Restituisce la lista dei ticket in attesa.
     * Accessibile solo a Dottori e Segretari.
     */
    public List<Ticket> getTicketsInAttesa() {
        // 1. CONTROLLO SICUREZZA
        checkPermessiPersonale();

        try {
            List<Ticket> allTickets = ticketDao.getAll();

            return allTickets.stream()
                    .filter(t -> "IN_ATTESA".equalsIgnoreCase(t.getStato()))
                    .collect(Collectors.toList());

        } catch (TicketException e) {
            throw new RuntimeException("Errore nel recupero dei ticket in attesa", e);
        }
    }

    /**
     * Permette di accettare o rifiutare un ticket.
     * Accessibile solo a Dottori e Segretari.
     */
    public Ticket valutaTicket(int ticketId, boolean isAccepted) {
        // 1. CONTROLLO SICUREZZA
        checkPermessiPersonale();

        Ticket ticket = ticketDao.get(ticketId);

        if (ticket == null) {
            throw new IllegalArgumentException("Nessun ticket trovato con ID: " + ticketId);
        }

        if (!"IN_ATTESA".equalsIgnoreCase(ticket.getStato())) {
            throw new IllegalStateException("Il ticket non è in attesa, stato attuale: " + ticket.getStato());
        }

        if (isAccepted) {
            ticket.setStato("ACCETTATO");

        } else {
            ticket.setStato("RIFIUTATO");

        }

        try {
            ticketDao.update(ticket);
        } catch (TicketException e) {
            throw new RuntimeException("Impossibile aggiornare lo stato del ticket", e);
        }

        return ticket;
    }

    /**
     * Metodo privato per verificare che l'utente sia loggato
     * e sia un Dottore o un Segretario.
     */
    private void checkPermessiPersonale() {
        Sessione s = Sessione.getInstance();
        if (s.getUtenteLoggato() == null) {
            throw new SecurityException("Operazione non consentita: Nessun utente loggato.");
        }
        if (!s.isDottore() && !s.isSegretario()) {
            throw new SecurityException("Accesso Negato: Operazione riservata al personale medico/amministrativo.");
        }
    }
}