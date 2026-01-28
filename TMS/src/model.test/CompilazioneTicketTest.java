package model.test;

import model.CompilazioneTicket;
import model.Sessione;
import model.dao.TicketDao;
import model.pojo.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *  @author DAPPIANO FRANCESCO
 */
class CompilazioneTicketTest {

    // Stub per simulare il database
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
        Sessione.getInstance().logout(); // Assicuriamoci che non ci sia nessuno loggato
        daoFinto = new TicketDaoStub();
        service = new CompilazioneTicket(daoFinto);
    }

    @Test
    void testCreaTicket_SenzaLogin_Successo() {
        int idPazienteTest = 999;
        String colore = "Giallo";
        int priorita = 2;
        String sintomi = "Sintomi di prova";

        // Chiamata con il nuovo parametro idPaziente
        service.creaTicket(colore, priorita, sintomi, idPazienteTest);

        Ticket risultato = daoFinto.ticketSalvato;

        assertNotNull(risultato, "Il ticket deve essere salvato anche senza login in sessione");
        assertEquals(idPazienteTest, risultato.getIdPaziente());
        assertEquals("IN_ATTESA", risultato.getStato());
        assertNotNull(risultato.getTimestamp());
    }
}