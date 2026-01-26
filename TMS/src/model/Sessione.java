package model;

import model.pojo.Dottore;
import model.pojo.Paziente;
import model.pojo.Segretario;
import model.pojo.Utente;

public class Sessione {

    // 1. Istanza statica unica
    private static Sessione instance;

    // 2. Variabile per salvare l'utente loggato
    private Utente utenteLoggato;

    // Costruttore privato: nessuno può fare "new Sessione()" dall'esterno
    private Sessione() {}

    // 3. Metodo per accedere all'istanza
    public static Sessione getInstance() {
        if (instance == null) {
            instance = new Sessione();
        }
        return instance;
    }

    // ==========================================
    // GESTIONE UTENTE LOGGATO
    // ==========================================

    /**
     * Imposta l'utente loggato (chiamato al Login)
     */
    public void setUtenteLoggato(Utente utente) {
        this.utenteLoggato = utente;
    }

    /**
     * Restituisce l'utente generico
     */
    public Utente getUtenteLoggato() {
        return utenteLoggato;
    }

    /**
     * Esegue il logout (pulisce la sessione)
     */
    public void logout() {
        this.utenteLoggato = null;
    }

    // ==========================================
    // METODI DI UTILITÀ PER IL TIPO DI UTENTE
    // ==========================================

    // Controlli rapidi
    public boolean isDottore() {
        return utenteLoggato instanceof Dottore;
    }

    public boolean isPaziente() {
        return utenteLoggato instanceof Paziente;
    }

    public boolean isSegretario() {
        return utenteLoggato instanceof Segretario;
    }

    // Getter specifici con cast (utili nel Controller)
    public Dottore getDottore() {
        if (isDottore()) {
            return (Dottore) utenteLoggato;
        }
        return null; // O lancia eccezione
    }

    public Paziente getPaziente() {
        if (isPaziente()) {
            return (Paziente) utenteLoggato;
        }
        return null;
    }

    public Segretario getSegretario() {
        if (isSegretario()) {
            return (Segretario) utenteLoggato;
        }
        return null;
    }
}