package model.test;

import model.Sessione;
import model.pojo.Dottore;
import model.pojo.Paziente;
import model.pojo.Segretario;
import model.pojo.Utente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
//@author Accatino Riccardo and Angie Albitres
class SessioneTest {

    @AfterEach
    void tearDown() {
        Sessione.getInstance().logout();
    }

    @Test
    void testSingletonInstance() {
        Sessione s1 = Sessione.getInstance();
        Sessione s2 = Sessione.getInstance();

        assertNotNull(s1, "L'istanza non deve essere null");
        assertSame(s1, s2, "Le due variabili devono puntare allo stesso oggetto in memoria");
    }

    @Test
    void testLoginLogout() {
        Sessione sessione = Sessione.getInstance();

        assertNull(sessione.getUtenteLoggato(), "All'avvio nessun utente deve essere loggato");

        Utente u = new Utente(1, "Test", "User", "2000-01-01", "GENERICO");
        sessione.setUtenteLoggato(u);

        assertEquals(u, sessione.getUtenteLoggato(), "L'utente loggato deve corrispondere a quello settato");

        sessione.logout();
        assertNull(sessione.getUtenteLoggato(), "Dopo il logout l'utente deve essere null");
    }

    @Test
    void testSessioneDottore() {
        Sessione sessione = Sessione.getInstance();
        Dottore doc = new Dottore(10, "Mario", "Rossi", "1980-01-01", "M123", "Mattina", 1);

        sessione.setUtenteLoggato(doc);

        assertAll("Verifiche specifiche per Dottore",
                () -> assertTrue(sessione.isDottore(), "isDottore() deve ritornare true"),
                () -> assertFalse(sessione.isPaziente(), "isPaziente() deve ritornare false"),
                () -> assertFalse(sessione.isSegretario(), "isSegretario() deve ritornare false"),
                () -> assertNotNull(sessione.getDottore(), "getDottore() deve restituire l'oggetto"),
                () -> assertEquals("M123", sessione.getDottore().getMatricola(), "Devo poter accedere ai metodi specifici di Dottore")
        );
    }

    @Test
    void testSessionePaziente() {
        Sessione sessione = Sessione.getInstance();
        Paziente paz = new Paziente(20, "Luigi", "Verdi", "1990-01-01", "CFTEST01", "Via Roma");

        sessione.setUtenteLoggato(paz);

        assertAll("Verifiche specifiche per Paziente",
                () -> assertTrue(sessione.isPaziente(), "isPaziente() deve ritornare true"),
                () -> assertFalse(sessione.isDottore(), "isDottore() deve ritornare false"),
                () -> assertNotNull(sessione.getPaziente(), "getPaziente() deve restituire l'oggetto"),
                () -> assertEquals("CFTEST01", sessione.getPaziente().getCodiceFiscale())
        );
    }

    @Test
    void testSessioneSegretario() {
        Sessione sessione = Sessione.getInstance();
        Segretario seg = new Segretario(30, "Anna", "Neri", "1985-05-05", 2);

        sessione.setUtenteLoggato(seg);

        assertAll("Verifiche specifiche per Segretario",
                () -> assertTrue(sessione.isSegretario(), "isSegretario() deve ritornare true"),
                () -> assertNotNull(sessione.getSegretario(), "getSegretario() deve restituire l'oggetto"),
                () -> assertEquals(2, sessione.getSegretario().getLivelloPermessi())
        );
    }

    @Test
    void testCastingErrato() {
        Sessione sessione = Sessione.getInstance();
        Paziente paz = new Paziente(20, "Luigi", "Verdi", "1990-01-01", "CFTEST01", "Via Roma");
        sessione.setUtenteLoggato(paz);

        Dottore d = sessione.getDottore();

        assertNull(d, "Se l'utente è un Paziente, getDottore() deve ritornare null");
    }
}