package controller;

import gui.vista.GuiMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import model.CalendarioService;
import model.Sessione;
import model.pojo.Visita;

import java.io.IOException;
import java.util.List;

public class MenuController {
    @FXML
    private ListView<String> listaAppuntamenti;

    private final CalendarioService calendarioService = new CalendarioService();


    @FXML
    public void initialize() {
        if (listaAppuntamenti != null) {
            //serve a popolare il calendario
            caricaDatiCalendario();
        }
    }



    @FXML
    public void ApriCalendarioGenerale(ActionEvent actionEvent) {
        // usato dal segretario per vedere gli appuntamenti di tutti
        navigaVerso("PannelloCalendario");
    }

    @FXML
    public void VisualizzaCalendario(ActionEvent actionEvent) {
        // Usato dal Dottore per vedere i propri eventi
        navigaVerso("PannelloCalendario");
    }

    @FXML
    public void CreaNuovoTicket(ActionEvent actionEvent) {
        // Porta alla schermata Triage
        navigaVerso("PannelloTriage");
    }

    @FXML
    public void AccettaTicket(ActionEvent actionEvent) {
        // Porta alla schermata di Accettazione
        navigaVerso("PannelloAccettazione");
    }

    @FXML
    public void EffettuaLogout(ActionEvent actionEvent) {
        // Resetta l'utente loggato nella sessione
        Sessione.getInstance().logout();

        System.out.println("Logout effettuato con successo.");

        // Torna alla schermata principale
        navigaVerso("PannelloUtente");
    }

    @FXML
    public void TornaAlMenu(ActionEvent actionEvent) {

        if (Sessione.getInstance().isDottore()) {
            navigaVerso("PannelloDottore");
        } else if (Sessione.getInstance().isSegretario()) {
            navigaVerso("PannelloSegretario");
        } else {

            navigaVerso("PannelloUtente");
        }
    }


    private void navigaVerso(String fxml) {
        try {
            GuiMain.setRoot(fxml);
        } catch (IOException e) {
            mostraErrore("Impossibile caricare la schermata: " + fxml);
            e.printStackTrace();
        }
    }

    private void caricaDatiCalendario() {
        listaAppuntamenti.getItems().clear();
        List<Visita> visite;

        try {
            if (Sessione.getInstance().isSegretario()) {
                visite = calendarioService.getEventiGlobali();
            }
            else if (Sessione.getInstance().isDottore()) {
                visite = calendarioService.getEventiPersonali();
            } else {
                return;
            }

            if (visite.isEmpty()) {
                listaAppuntamenti.getItems().add("Nessun appuntamento in programma.");
            } else {
                for (Visita v : visite) {
                    String riga = String.format("Visita #%d - %s (Sala: %s)",
                            v.getIdVisita(), v.getDataOraInizio(), v.getSala());
                    listaAppuntamenti.getItems().add(riga);
                }
            }

        } catch (SecurityException e) {
            mostraErrore("Permesso negato: " + e.getMessage());
        } catch (Exception e) {
            mostraErrore("Errore caricamento dati: " + e.getMessage());
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}