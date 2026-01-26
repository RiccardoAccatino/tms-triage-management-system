package model.test;

import model.accRifTicket;
import model.Sessione;
import model.dao.TicketDao;
import dbManager.db;
import model.pojo.Dottore;
import model.pojo.Paziente;
import model.pojo.Ticket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class AccRifTicketTest {

    private accRifTicket service;
    private TicketDao dao;
    private Ticket ticketDiProva;

    @BeforeEach
    public void setUp() {
        db.initializeDb();
        Sessione.getInstance().logout();

        service = new accRifTicket();
        dao = new TicketDao();

        ticketDiProva = new Ticket(0, "Bianco", 1, "Test JUnit", "IN_ATTESA", "2023-10-01 10:00", 1);
        dao.save(ticketDiProva);

        Dottore doc = new Dottore(100, "Gregory", "House", "1960-01-01", "DOC1", "H24", 1);
        Sessione.getInstance().setUtenteLoggato(doc);
    }

    @AfterEach
    public void tearDown() {
        if (ticketDiProva != null && ticketDiProva.getIdTicket() > 0) {
            dao.delete(ticketDiProva);
        }
        Sessione.getInstance().logout();
    }

    @Test
    public void testGetTicketsInAttesa_ConDottoreLoggato() {
        List<Ticket> lista = service.getTicketsInAttesa();

        assertNotNull(lista, "La lista non dovrebbe essere null");

        boolean trovato = false;
        for (Ticket t : lista) {
            if (t.getIdTicket() == ticketDiProva.getIdTicket()) {
                trovato = true;
                break;
            }
        }
        assertTrue(trovato, "Il ticket inserito (IN_ATTESA) dovrebbe essere visibile al Dottore");
    }

    @Test
    public void testValutaTicket_Accettazione() {
        Ticket ticketAggiornato = service.valutaTicket(ticketDiProva.getIdTicket(), true);

        assertEquals("ACCETTATO", ticketAggiornato.getStato());

        Ticket ticketDalDb = dao.get(ticketDiProva.getIdTicket());
        assertEquals("ACCETTATO", ticketDalDb.getStato());
    }

    @Test
    public void testAccessoNegato_SeUtentePaziente() {
        Paziente paz = new Paziente(200, "Mario", "Rossi", "1990-01-01", "CFTEST", "Via Roma");
        Sessione.getInstance().setUtenteLoggato(paz);

        Exception exception = assertThrows(SecurityException.class, () -> {
            service.valutaTicket(ticketDiProva.getIdTicket(), true);
        });

        assertTrue(exception.getMessage().contains("Accesso Negato"), "Dovrebbe lanciare eccezione di accesso negato");
        System.out.println("Test Sicurezza Superato: Il paziente è stato bloccato.");
    }
}