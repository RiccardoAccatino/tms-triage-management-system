package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.LoginService;
import model.Sessione;

import java.io.IOException;

public class LoginController {
    @FXML private TextField idUtenteField;
    private LoginService loginService = new LoginService();

    @FXML
    private void eseguiLogin() {
        int id = Integer.parseInt(idUtenteField.getText());
        if (loginService.login(id)) {
            // Reindirizza l'utente in base al tipo salvato in Sessione
            if (Sessione.getInstance().isDottore()) {
                // Vai a PannelloDottore.xfml
            } else if (Sessione.getInstance().isSegretario()) {
                // Vai a PannelloSegretario.xfml
            }
        }
    }

    public void EffettuaLogin(ActionEvent actionEvent) {
    }

    @FXML
    private void VaiAPrenotazione(ActionEvent event) throws IOException {
        cambiaScena(event, "/gui.vista/PannelloPrenotazioneVisita.fxml");
    }

    @FXML
    private void VaiATriage(ActionEvent event) throws IOException {
        cambiaScena(event, "/gui.vista/PannelloTriage.fxml");
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}