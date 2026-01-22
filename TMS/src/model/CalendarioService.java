package model;

import model.dao.VisitaDao;
import model.pojo.Visita;
import java.util.List;

/**
 * Gestisce la logica per la visione del calendario - RF4
 *
 * @autor Angie Riccardo
 */
public class CalendarioService {
    private VisitaDao visitaDao;

    public CalendarioService() {
        this.visitaDao = new VisitaDao();
    }

    /**
     * Ritorna tutte le visite (Vista per il Segretario).
     */
    public List<Visita> getEventiGlobali() {
        return visitaDao.getAll();
    }

    /**
     * Ritorna solo le visite di un medico (Vista per il Dottore).
     */
    public List<Visita> getEventiMedico(int idDottore) {
        return visitaDao.getByDottore(idDottore);
    }
}