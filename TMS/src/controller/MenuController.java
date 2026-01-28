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
//@author Accatino Riccardo and Angie Albitres
public class MenuController {

    @FXML
    private ListView<String> listaAppuntamenti;

    private final CalendarioService calendarioService = new CalendarioService();


    @FXML
    public void initialize() {
        // Se listaAppuntamenti non è null, significa che siamo nella schermata PannelloCalendario
        if (listaAppuntamenti != null) {
            caricaDatiCalendario();
        }
    }


    @FXML
    public void ApriCalendarioGenerale(ActionEvent actionEvent) {
        navigaVerso("PannelloCalendario");
    }

    @FXML
    public void VisualizzaCalendario(ActionEvent actionEvent) {
        navigaVerso("PannelloCalendario");
    }

    @FXML
    public void CreaNuovoTicket(ActionEvent actionEvent) {
        navigaVerso("PannelloTriage");
    }

    @FXML
    public void AccettaTicket(ActionEvent actionEvent) {
        navigaVerso("PannelloAccettazione");
    }

    @FXML
    public void EffettuaLogout(ActionEvent actionEvent) {
        Sessione.getInstance().logout();

        System.out.println("Logout effettuato con successo.");

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