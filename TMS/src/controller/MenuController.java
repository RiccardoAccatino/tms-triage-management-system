package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuController {

    @FXML
    private void VaiAPrenotazione(ActionEvent event) throws IOException {
        cambiaScena(event, "/gui.vista/PannelloPrenotazioneVisita.xfml");
    }

    @FXML
    private void VaiATriage(ActionEvent event) throws IOException {
        cambiaScena(event, "/gui.vista/PannellloTriage.xfml");
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}