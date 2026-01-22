package model.test;

import dbManager.db;
import model.PrenotazioneVisita;
import model.PrenotazioneVisitaService;
import model.dao.TicketDao;
import model.pojo.Ticket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Completo Requisito RF1 - Prenotazione Visite
 */
class PrenotazioneVisitaTest {

    @BeforeAll
    static void setup() {
        // Inizializziamo il database e le tabelle prima di eseguire i test
        db.initializeDb();
    }

    @Test
    void testCreazionePrenotazione() {
        // Verifica che l'oggetto DTO memorizzi correttamente i dati inseriti dall'utente
        PrenotazioneVisita prenotazione = new PrenotazioneVisita(1, 10, 2, "2026-05-20 10:30", "Controllo periodico");

        assertEquals(1, prenotazione.getIdPaziente());
        assertEquals("In Attesa", prenotazione.getStato()); // Stato iniziale richiesto da UC1 [cite: 244]
    }

    @Test
    void testSalvataggioPrenotazioneService() {
        // 1. Prepariamo i dati della richiesta
        PrenotazioneVisita richiesta = new PrenotazioneVisita(
                1,
                10,
                2,
                "2026-05-20 10:30",
                "Controllo periodico cardiologia"
        );

        // 2. Istanziamo il Service e proviamo a salvare
        PrenotazioneVisitaService service = new PrenotazioneVisitaService();
        boolean successo = service.salvaPrenotazioneComeTicket(richiesta);

        // 3. Verifica: Il metodo deve restituire true
        assertTrue(successo, "Il service dovrebbe restituire true dopo il salvataggio");

        // 4. Verifica profonda: Controlliamo se nel database esiste davvero un nuovo Ticket
        TicketDao ticketDao = new TicketDao();
        List<Ticket> tuttiITicket = ticketDao.getAll();

        // Verifichiamo che la lista non sia vuota e che l'ultimo ticket inserito sia "In Attesa"
        assertFalse(tuttiITicket.isEmpty(), "Il database dovrebbe contenere almeno un ticket");

        Ticket ultimoTicket = tuttiITicket.getLast();
        assertEquals("In Attesa", ultimoTicket.getStato(), "Il ticket nel DB deve avere stato 'In Attesa'");
        assertEquals(richiesta.getIdPaziente(), ultimoTicket.getIdPaziente(), "L'ID paziente nel DB deve coincidere");
    }
}