package model.test;

import model.CompilazioneTicket;
import model.Sessione;
import model.dao.TicketDao;
import model.pojo.Paziente;
import model.pojo.Ticket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompilazioneTicketTest {

    class TicketDaoStub extends TicketDao {
        public Ticket ticketSalvato;

        @Override
        public void save(Ticket ticket) {
            this.ticketSalvato = ticket;
        }
    }

    private CompilazioneTicket service;
    private TicketDaoStub daoFinto;

    @BeforeEach
    void setUp() {
        Sessione.getInstance().logout();

        daoFinto = new TicketDaoStub();
        service = new CompilazioneTicket(daoFinto);
    }

    @AfterEach
    void tearDown() {
        Sessione.getInstance().logout();
    }

    @Test
    void testCreaTicket_ConPazienteLoggato_Successo() {
        int idPazienteAtteso = 101;
        Paziente p = new Paziente(idPazienteAtteso, "Mario", "Rossi", "1990-01-01", "RSSMRA90A01H501W", "Via Roma 1");

        Sessione.getInstance().setUtenteLoggato(p);

        String colore = "Rosso";
        int priorita = 4;
        String sintomi = "Dolore toracico acuto";

        service.creaTicket(colore, priorita, sintomi);

        Ticket risultato = daoFinto.ticketSalvato;

        assertNotNull(risultato, "Il ticket dovrebbe essere stato passato al DAO");

        assertEquals(idPazienteAtteso, risultato.getIdPaziente(), "L'ID paziente nel ticket deve corrispondere all'utente loggato");

        assertEquals(colore, risultato.getColore());
        assertEquals(priorita, risultato.getPriorita());
        assertEquals(sintomi, risultato.getSintomi());
        assertEquals("IN_ATTESA", risultato.getStato());
        assertNotNull(risultato.getTimestamp());

        assertEquals("IN_ATTESA", risultato.getStato(), "Lo stato iniziale deve essere 'IN_ATTESA'");
        assertNotNull(risultato.getTimestamp(), "Il timestamp non deve essere nullo");

    @Test
    void testCreaTicket_SenzaLogin_DeveLanciareEccezione() {
        Exception exception = assertThrows(SecurityException.class, () -> {
            service.creaTicket("Verde", 1, "Mal di testa");
        });

        assertEquals("Operazione non consentita: Nessun paziente loggato.", exception.getMessage());

        System.out.println("Test Sicurezza Superato: Bloccato tentativo senza login.");
    }
}