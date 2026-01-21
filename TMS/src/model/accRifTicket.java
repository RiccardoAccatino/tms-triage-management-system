package model;
import model.dao.TicketDao;
import model.pojo.Ticket;
import model.exception.TicketException;
import java.util.List;
import java.util.stream.Collectors;

// RF5 (20055270)
public class accRifTicket {
        private final TicketDao ticketDao;

        public accRifTicket() {
            this.ticketDao = new TicketDao();
        }

        public List<Ticket> getTicketsInAttesa() {
            try {
                List<Ticket> allTickets = ticketDao.getAll();

                return allTickets.stream()
                        .filter(t -> "IN_ATTESA".equalsIgnoreCase(t.getStato()))
                        .collect(Collectors.toList());

            } catch (TicketException e) {
                throw new RuntimeException("Errore nel recupero dei ticket in attesa", e);
            }
        }


        public Ticket valutaTicket(int ticketId, boolean isAccepted) {
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
    }

