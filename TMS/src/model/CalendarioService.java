package model;

import model.dao.VisitaDao;
import model.pojo.Visita;
import java.util.List;

//@author Accatino Riccardo and Angie Albitres
public class CalendarioService {
    private VisitaDao visitaDao;

    public CalendarioService() {
        this.visitaDao = new VisitaDao();
    }


    public List<Visita> getEventiGlobali() {
        Sessione s = Sessione.getInstance();

        if (s.getUtenteLoggato() == null) {
            throw new SecurityException("Nessun utente loggato.");
        }

        if (!s.isSegretario()) {
            throw new SecurityException("Accesso Negato: Solo i segretari possono vedere il calendario globale.");
        }

        return visitaDao.getAll();
    }


    public List<Visita> getEventiPersonali() {
        Sessione s = Sessione.getInstance();

        if (s.getUtenteLoggato() == null) {
            throw new SecurityException("Nessun utente loggato.");
        }

        if (!s.isDottore()) {
            throw new SecurityException("Accesso Negato: Solo i dottori possono accedere al calendario personale.");
        }

        int idDottore = s.getDottore().getIdUtente();

        return visitaDao.getByDottore(idDottore);
    }
}