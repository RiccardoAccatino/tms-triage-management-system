package model;

import model.pojo.Dottore;
import model.pojo.Paziente;
import model.pojo.Segretario;
import model.pojo.Utente;
//@author Accatino Riccardo and Angie Albitres
public class Sessione {


    private static Sessione instance;

    private Utente utenteLoggato;

    private Sessione() {}

    public static Sessione getInstance() {
        if (instance == null) {
            instance = new Sessione();
        }
        return instance;
    }

    public void setUtenteLoggato(Utente utente) {
        this.utenteLoggato = utente;
    }

    public Utente getUtenteLoggato() {
        return utenteLoggato;
    }

    public void logout() {
        this.utenteLoggato = null;
    }


    public boolean isDottore() {
        return utenteLoggato instanceof Dottore;
    }

    public boolean isPaziente() {
        return utenteLoggato instanceof Paziente;
    }

    public boolean isSegretario() {
        return utenteLoggato instanceof Segretario;
    }

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