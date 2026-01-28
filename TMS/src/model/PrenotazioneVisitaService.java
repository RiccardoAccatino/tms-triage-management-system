package model;

import model.dao.TicketDao;
import model.dao.VisitaDao;
import model.pojo.Ticket;
import model.pojo.Visita;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service per la gestione della logica di business legata a RF1.
 *
 * @author angie
 */
public class PrenotazioneVisitaService {
    private final TicketDao ticketDao;
    private final VisitaDao visitaDao;

    public PrenotazioneVisitaService() {
        this.visitaDao = new VisitaDao();
        this.ticketDao = new TicketDao();
    }

    /**
     * Crea un Ticket già ACCETTATO e genera subito la VISITA corrispondente.
     */
    public boolean registraPrenotazioneDiretta(PrenotazioneVisita richiesta) {
        try {
            // --- CREAZIONE TICKET (gia accettato)---
            Ticket nuovoTicket = new Ticket();
            nuovoTicket.setIdPaziente(richiesta.getIdPaziente());
            nuovoTicket.setColore("Verde"); // Priorità standard per prenotazioni
            nuovoTicket.setPriorita(1);

            // Impostiamo direttamente lo stato su ACCETTATO
            nuovoTicket.setStato("ACCETTATO");

            String desc = String.format("Prenotazione Diretta - Motivo: %s", richiesta.getMotivo());
            nuovoTicket.setSintomi(desc);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            nuovoTicket.setTimestamp(dtf.format(LocalDateTime.now()));

            // Salviamo il ticket per ottenere l'ID generato
            ticketDao.save(nuovoTicket);
            int idTicketGenerato = nuovoTicket.getIdTicket();

            // --- CREAZIONE VISITA ---
            Visita nuovaVisita = new Visita();
            nuovaVisita.setIdTicket(idTicketGenerato);
            nuovaVisita.setIdPaziente(richiesta.getIdPaziente());
            nuovaVisita.setIdDottore(richiesta.getIdDottore());
            nuovaVisita.setIdReparto(richiesta.getIdReparto());

            // Impostiamo Data e Ora scelte dal paziente
            nuovaVisita.setDataOraInizio(richiesta.getDataOraRichiesta());

            // Calcoliamo una data fine fittizia (es. +30 minuti)
            try {
                LocalDateTime inizio = LocalDateTime.parse(richiesta.getDataOraRichiesta(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime fine = inizio.plusMinutes(30);
                nuovaVisita.setDataOraFine(fine.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } catch (Exception e) {
                // Fallback se il formato non è perfetto
                nuovaVisita.setDataOraFine(richiesta.getDataOraRichiesta());
            }

            nuovaVisita.setSala("Ambulatorio " + richiesta.getIdReparto()); // Assegnazione sala automatica o fittizia

            // Salviamo la visita
            visitaDao.save(nuovaVisita);

            System.out.println("Prenotazione completata: Ticket #" + idTicketGenerato + " -> Visita creata.");
            return true;

        } catch (Exception e) {
            System.err.println("Errore prenotazione diretta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}