package model.test;

import model.dao.PazienteDao;
import model.dao.TicketDao;
import model.pojo.Paziente;
import model.pojo.Ticket;
import model.prenotazioneVisita;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class prenotazioneVisitaTest {
    @Test
    void testRegistraPrenotazioneManuale() {
        // DAO Paziente che assegna ID 500
        PazienteDao paziente = new PazienteDao() {
            @Override
            public void save(Paziente p) {
                p.setIdUtente(500);
            }
        };

        // DAO Ticket che assegna ID 999
        TicketDao ticket = new TicketDao() {
            @Override
            public void save(Ticket t) {
                t.setIdTicket(999);
            }
        };

        // Creazione prenotazione
        prenotazioneVisita prenotazione = new prenotazioneVisita(paziente, ticket);
        Paziente p = new Paziente();
        p.setNome("Angie");

        prenotazione.registraPrenotazione(p);

        assertEquals(500, p.getIdUtente());
        assertEquals(999, prenotazione.getIdTicket());
        assertEquals(500, prenotazione.getIdPaziente());
    }
}