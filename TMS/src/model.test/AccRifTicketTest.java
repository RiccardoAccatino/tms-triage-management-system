package model.test;

import model.accRifTicket;
import model.dao.TicketDao;
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
        service = new accRifTicket();
        dao = new TicketDao();

        ticketDiProva = new Ticket(0, "Bianco", 1, "Test JUnit", "IN_ATTESA", "2023-10-01 10:00", 1);

        dao.save(ticketDiProva);
    }


    @AfterEach
    public void tearDown() {

        if (ticketDiProva != null && ticketDiProva.getIdTicket() > 0) {
            dao.delete(ticketDiProva);
        }
    }

    @Test
    public void testGetTicketsInAttesa() {
        List<Ticket> lista = service.getTicketsInAttesa();

        assertNotNull(lista, "La lista non dovrebbe essere null");

        boolean trovato = false;
        for (Ticket t : lista) {
            if (t.getIdTicket() == ticketDiProva.getIdTicket()) {
                trovato = true;
                break;
            }
        }
        assertTrue(trovato, "Il ticket inserito (IN_ATTESA) dovrebbe essere presente nella lista recuperata");
    }

    @Test
    public void testValutaTicket_Accettazione() {
        Ticket ticketAggiornato = service.valutaTicket(ticketDiProva.getIdTicket(), true);

        assertEquals("ACCETTATO", ticketAggiornato.getStato(), "L'oggetto ritornato deve avere stato ACCETTATO");

        Ticket ticketDalDb = dao.get(ticketDiProva.getIdTicket());
        assertEquals("ACCETTATO", ticketDalDb.getStato(), "Nel database lo stato deve essere ACCETTATO");
    }

    @Test
    public void testValutaTicket_Rifiuto() {
        Ticket ticketAggiornato = service.valutaTicket(ticketDiProva.getIdTicket(), false);

        assertEquals("RIFIUTATO", ticketAggiornato.getStato(), "L'oggetto ritornato deve avere stato RIFIUTATO");

        Ticket ticketDalDb = dao.get(ticketDiProva.getIdTicket());
        assertEquals("RIFIUTATO", ticketDalDb.getStato(), "Nel database lo stato deve essere RIFIUTATO");
    }
}