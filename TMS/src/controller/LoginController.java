package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.LoginService;
import model.Sessione;

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
}