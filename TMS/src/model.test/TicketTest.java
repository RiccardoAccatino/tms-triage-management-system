package model.test;

import dbManager.db;
import model.dao.PazienteDao;
import model.dao.TicketDao;
import model.pojo.Paziente;
import model.pojo.Ticket;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite per la gestione dei Ticket.
 * Aggiornato per gestire correttamente l'univocità del Codice Fiscale.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TicketTest {

    private TicketDao ticketDao;
    private PazienteDao pazienteDao;
    private int idPazienteTest;

    @BeforeAll
    static void initDb() {
        db.initializeDb();
    }

    @BeforeEach
    void setUp() {
        ticketDao = new TicketDao();
        pazienteDao = new PazienteDao();

        // FIX: Usiamo Random invece del tempo per evitare duplicati nei test rapidi
        Random random = new Random();
        int randomNum = random.nextInt(900000) + 100000; // Numero a 6 cifre
        String cfTest = "TEST_CF_" + randomNum; // Esempio: TEST_CF_123456 (14 caratteri)

        Paziente p = new Paziente(0, "Paziente", "Test", "1990-01-01", cfTest, "Via Test");
        pazienteDao.save(p);
        this.idPazienteTest = p.getIdUtente();
    }

    // NUOVO: Pulizia dopo ogni test per non intasare il DB
    @AfterEach
    void tearDown() {
        if (idPazienteTest > 0) {
            Paziente p = new Paziente();
            p.setIdUtente(idPazienteTest);
            // Nota: PazienteDao.delete elimina sia Utente che Paziente
            pazienteDao.delete(p);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Unit Test: Verifica Getter e Setter del POJO")
    void testTicketPojo() {
        Ticket t = new Ticket();
        t.setColore("Rosso");
        t.setPriorita(4);
        t.setSintomi("Dolore toracico");
        t.setStato("IN_ATTESA");
        t.setTimestamp("2023-10-10 10:00:00");
        t.setIdPaziente(1);

        assertEquals("Rosso", t.getColore());
        assertEquals(4, t.getPriorita());
        assertEquals("Dolore toracico", t.getSintomi());
        assertEquals("IN_ATTESA", t.getStato());
        assertEquals("2023-10-10 10:00:00", t.getTimestamp());
        assertEquals(1, t.getIdPaziente());
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test: Salvataggio e Recupero dal DB")
    void testSalvataggioERecupero() {
        Ticket nuovoTicket = new Ticket(0, "Giallo", 2, "Febbre alta", "IN_ATTESA", "2023-10-27 09:00:00", idPazienteTest);
        ticketDao.save(nuovoTicket);

        assertTrue(nuovoTicket.getIdTicket() > 0, "Il ticket deve avere un ID generato dopo il salvataggio");

        Ticket recuperato = ticketDao.get(nuovoTicket.getIdTicket());
        assertNotNull(recuperato, "Il ticket salvato deve essere recuperabile");
        assertEquals("Giallo", recuperato.getColore());
        assertEquals(idPazienteTest, recuperato.getIdPaziente());
    }

    @Test
    @Order(3)
    @DisplayName("Integration Test: Aggiornamento Stato e Priorità")
    void testAggiornamento() {
        Ticket ticket = new Ticket(0, "Bianco", 1, "Tosse lieve", "IN_ATTESA", "2023-10-27 08:30:00", idPazienteTest);
        ticketDao.save(ticket);
        int idTicket = ticket.getIdTicket();

        ticket.setColore("Verde");
        ticket.setPriorita(2);
        ticket.setStato("ACCETTATO");

        ticketDao.update(ticket);

        Ticket aggiornato = ticketDao.get(idTicket);
        assertEquals("Verde", aggiornato.getColore());
        assertEquals(2, aggiornato.getPriorita());
        assertEquals("ACCETTATO", aggiornato.getStato());
    }

    @Test
    @Order(4)
    @DisplayName("Integration Test: Eliminazione Ticket")
    void testEliminazione() {
        Ticket daEliminare = new Ticket(0, "Bianco", 0, "Test Delete", "RIFIUTATO", "2023-01-01 00:00:00", idPazienteTest);
        ticketDao.save(daEliminare);
        int id = daEliminare.getIdTicket();

        ticketDao.delete(daEliminare);

        Ticket check = ticketDao.get(id);
        assertNull(check, "Il ticket eliminato non dovrebbe più esistere nel DB");
    }

    @Test
    @Order(5)
    @DisplayName("Integration Test: Recupero lista completa")
    void testGetAll() {
        // Creiamo almeno un ticket per essere sicuri che la lista non sia vuota
        Ticket t = new Ticket(0, "Test", 1, "GetAll", "IN_ATTESA", "2023-01-01", idPazienteTest);
        ticketDao.save(t);

        List<Ticket> lista = ticketDao.getAll();
        assertNotNull(lista, "La lista dei ticket non deve essere null");
        assertFalse(lista.isEmpty(), "La lista ticket dovrebbe contenere gli elementi");
    }
}