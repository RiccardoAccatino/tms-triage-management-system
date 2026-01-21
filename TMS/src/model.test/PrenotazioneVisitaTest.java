package model.test;

import model.dao.PazienteDao;
import model.dao.TicketDao;
import model.pojo.Paziente;
import model.pojo.Ticket;
import model.PrenotazioneVisita;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test PrenotazioneVisite - RF1
 *
 * @author angie
 */
class PrenotazioneVisitaTest {
    @Test
    void testCreazionePrenotazione() {
        // 1. Dati di esempio per la simulazione
        int idPaziente = 1;
        int idDottore = 10;
        int idReparto = 2;
        String dataOra = "2026-05-20 10:30";
        String motivo = "Controllo periodico cardiologia";

        // 2. Creazione dell'oggetto POJO
        PrenotazioneVisita prenotazione = new PrenotazioneVisita(idPaziente, idDottore, idReparto, dataOra, motivo);

        // 3. Verifiche puntuali (si ferma alla prima che fallisce)
        assertEquals(idPaziente, prenotazione.getIdPaziente());
        assertEquals(idDottore, prenotazione.getIdDottore());
        assertEquals(idReparto, prenotazione.getIdReparto());
        assertEquals(dataOra, prenotazione.getDataOraRichiesta());
        assertEquals(motivo, prenotazione.getMotivo());

        // Verifica dello stato iniziale come definito nel Caso d'Uso UC1 [cite: 244]
        assertEquals("In Attesa", prenotazione.getStato());
    }
}