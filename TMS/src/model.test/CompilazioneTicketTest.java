package model.test;

import model.CompilazioneTicket;
import model.dao.TicketDao;
import model.pojo.Ticket;
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

    @Test
    void testCreaTicket_VerificaLogicaEPrecondizioni() {
        TicketDaoStub daoFinto = new TicketDaoStub();
        CompilazioneTicket service = new CompilazioneTicket(daoFinto);

        int idPaziente = 101;
        String colore = "Rosso";
        int priorita = 4;
        String sintomi = "Dolore toracico acuto";

        service.creaTicket(idPaziente, colore, priorita, sintomi);

        Ticket risultato = daoFinto.ticketSalvato;

        assertNotNull(risultato, "Il ticket dovrebbe essere stato passato al DAO");

        assertEquals(idPaziente, risultato.getIdPaziente());
        assertEquals(colore, risultato.getColore());
        assertEquals(priorita, risultato.getPriorita());
        assertEquals(sintomi, risultato.getSintomi());

        assertEquals("IN_ATTESA", risultato.getStato(), "Lo stato iniziale deve essere 'IN_ATTESA'");
        assertNotNull(risultato.getTimestamp(), "Il timestamp non deve essere nullo");

        System.out.println("Test passato! Ticket generato con timestamp: " + risultato.getTimestamp());
    }
}