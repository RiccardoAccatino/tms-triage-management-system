package model.test;

import dbManager.db;
import model.PrenotazioneVisita;
import model.PrenotazioneVisitaService;
import model.dao.TicketDao;
import model.dao.VisitaDao;
import model.pojo.Ticket;
import model.pojo.Visita;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Aggiornato per RF1 - Prenotazione Visite Diretta
 * Verifica che la prenotazione crei un Ticket ACCETTATO e una VISITA.
 *
 * @author angie
 */
class PrenotazioneVisitaTest {

    @BeforeAll
    static void setup() {
        // Inizializziamo il database e le tabelle prima di eseguire i test
        db.initializeDb();
    }

    @Test
    void testCreazioneOggettoPrenotazione() {
        // Verifica che l'oggetto DTO memorizzi correttamente i dati inseriti dall'utente
        PrenotazioneVisita prenotazione = new PrenotazioneVisita(1, 10, 2, "2026-05-20 10:30", "Controllo periodico");

        assertEquals(1, prenotazione.getIdPaziente());
        // Nel costruttore lo stato è "In Attesa" di default, ma il Segretario lo cambierà.
        assertEquals("In Attesa", prenotazione.getStato());
    }

    @Test
    void testPrenotazioneDirettaService() {
        // Prepariamo i dati della richiesta
        // Usiamo ID Paziente 1, Dottore 1, Reparto 1 (assicurati che esistano o che il DB accetti FK permissive nei test)
        PrenotazioneVisita richiesta = new PrenotazioneVisita(
                1,
                1,
                1,
                "2026-12-25 10:00",
                "Controllo urgente cardiologia"
        );

        // Istanziamo il Service e proviamo a salvare con la nuova logica
        PrenotazioneVisitaService service = new PrenotazioneVisitaService();
        boolean successo = service.registraPrenotazioneDiretta(richiesta);

        // Verifica: Il metodo deve restituire true
        assertTrue(successo, "Il service dovrebbe restituire true dopo la prenotazione diretta");

        // VERIFICA TICKET (Deve essere ACCETTATO)
        TicketDao ticketDao = new TicketDao();
        List<Ticket> tuttiITicket = ticketDao.getAll();
        assertFalse(tuttiITicket.isEmpty(), "Il database dovrebbe contenere il ticket appena creato");

        Ticket ultimoTicket = tuttiITicket.get(tuttiITicket.size() - 1);
        assertEquals("ACCETTATO", ultimoTicket.getStato(), "Il ticket creato deve essere subito in stato 'ACCETTATO'");
        assertEquals(richiesta.getIdPaziente(), ultimoTicket.getIdPaziente(), "L'ID paziente nel Ticket deve coincidere");

        // VERIFICA VISITA (Deve esistere e collegarsi al Ticket)
        VisitaDao visitaDao = new VisitaDao();
        List<Visita> tutteLeVisite = visitaDao.getAll();
        assertFalse(tutteLeVisite.isEmpty(), "Il database dovrebbe contenere la visita appena creata");

        // Cerchiamo la visita collegata all'ultimo ticket
        Visita visitaGenerata = null;
        for (Visita v : tutteLeVisite) {
            if (v.getIdTicket() == ultimoTicket.getIdTicket()) {
                visitaGenerata = v;
                break;
            }
        }

        assertNotNull(visitaGenerata, "Deve esistere una visita collegata all'ID del Ticket generato");
        assertEquals(richiesta.getIdDottore(), visitaGenerata.getIdDottore(), "L'ID Dottore nella Visita deve coincidere");
        assertEquals(richiesta.getIdReparto(), visitaGenerata.getIdReparto(), "L'ID Reparto nella Visita deve coincidere");
        assertEquals("2026-12-25 10:00", visitaGenerata.getDataOraInizio(), "La data inizio deve coincidere");
    }
}