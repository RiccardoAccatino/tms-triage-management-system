package model;

import model.dao.TicketDao;
import model.pojo.Ticket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service per la gestione della logica di business legata a RF1.
 *
 * @author angie albitres
 */
public class PrenotazioneVisitaService {
    private final TicketDao ticketDao;

    public PrenotazioneVisitaService() {
        this.ticketDao = new TicketDao();
    }

    /**
     * Prende i dati dalla richiesta dell'utente e crea un Ticket nel database.
     *
     * @param richiesta L'oggetto DTO con i dati della prenotazione.
     * @return true se il salvataggio avviene con successo, false altrimenti.
     */
    public boolean salvaPrenotazioneComeTicket(PrenotazioneVisita richiesta) {
        try {
            // Creiamo l'oggetto POJO Ticket che rispecchia la tabella nel DB
            Ticket nuovoTicket = new Ticket();

            // 1. Colleghiamo il paziente
            nuovoTicket.setIdPaziente(richiesta.getIdPaziente());

            // 2. Impostiamo lo stato iniziale richiesto dal caso d'uso UC1
            nuovoTicket.setStato(richiesta.getStato());

            // 3. Valori predefiniti per una prenotazione esterna
            nuovoTicket.setColore("Bianco"); // Codice colore base per visite non urgenti
            nuovoTicket.setPriorita(1);      // Priorità minima

            // 4. Gestione dati extra (Dottore e Reparto)
            // Dato che la tabella 'ticket' non ha questi campi, li inseriamo nel campo 'sintomi'
            // come stringa formattata. Il Segretario li leggerà da qui per creare la Visita.
            String dettagliDallaRichiesta = String.format(
                    "PRENOTAZIONE ESTERNA - Dottore ID: %d, Reparto ID: %d. Motivo: %s",
                    richiesta.getIdDottore(),
                    richiesta.getIdReparto(),
                    richiesta.getMotivo()
            );
            nuovoTicket.setSintomi(dettagliDallaRichiesta);

            // 5. Generiamo il timestamp di creazione attuale
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            nuovoTicket.setTimestamp(dtf.format(LocalDateTime.now()));

            // 6. Salvataggio definitivo tramite il DAO
            ticketDao.save(nuovoTicket);

            System.out.println("Service: Ticket #" + nuovoTicket.getIdTicket() + " creato per il paziente ID " + richiesta.getIdPaziente());
            return true;

        } catch (Exception e) {
            System.err.println("Errore durante la creazione del ticket: " + e.getMessage());
            return false;
        }
    }
}