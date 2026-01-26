package gui.vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dbManager.db;

import java.io.IOException;

public class GuiMain extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // 1. Inizializziamo il Database (crea le tabelle se non esistono)
        db.initializeDb();

        // 2. Carichiamo la schermata iniziale (Menu Principale)
        scene = new Scene(loadFXML("PannelloUtente"), 800, 600);

        stage.setTitle("TMS - Triage Management System");
        stage.setScene(scene);
        stage.show();
    }

    // Metodo statico per cambiare pagina da qualsiasi Controller
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Carica il file FXML dalla cartella resources/gui/vista
        FXMLLoader fxmlLoader = new FXMLLoader(GuiMain.class.getResource("/gui.vista/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}