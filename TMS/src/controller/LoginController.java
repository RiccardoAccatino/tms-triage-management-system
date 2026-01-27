package controller;

import gui.vista.GuiMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.LoginService;
import model.Sessione;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField txtIdStaff;

    private LoginService loginService = new LoginService();

    @FXML
    public void EffettuaLogin(ActionEvent actionEvent) {
        try {
            String idText = txtIdStaff.getText();
            if (idText.isEmpty()) {
                mostraErrore("Inserisci un ID.");
                return;
            }

            int id = Integer.parseInt(idText);

            if (loginService.login(id)) {
                // Reindirizzamento in base al ruolo
                if (Sessione.getInstance().isDottore()) {
                    GuiMain.setRoot("PannelloDottore");
                } else if (Sessione.getInstance().isSegretario()) {
                    GuiMain.setRoot("PannelloSegretario");
                }
            } else {
                mostraErrore("ID non trovato o utente non valido.");
            }
        } catch (NumberFormatException e) {
            mostraErrore("L'ID deve essere un numero.");
        } catch (IOException e) {
            e.printStackTrace();
            mostraErrore("Errore nel caricamento della pagina: " + e.getMessage());
        }
    }

    @FXML
    private void VaiAPrenotazione(ActionEvent event) throws IOException {
        GuiMain.setRoot("PannelloPrenotazioneVisita");
    }

    @FXML
    private void VaiATriage(ActionEvent event) throws IOException {
        GuiMain.setRoot("PannelloTriage");
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}