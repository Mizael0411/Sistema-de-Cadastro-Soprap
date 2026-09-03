package com.abrigo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(carregarFXML("login"), 600, 400);
        stage.setScene(scene);
        stage.setTitle("Sistema Abrigo");
        stage.show();
    }

    // Troca TODA a tela (usado só na transição Login -> Menu principal)
    public static void trocarCena(String nomeFxml) throws IOException {
        scene.setRoot(carregarFXML(nomeFxml));
    }

    private static Parent carregarFXML(String nome) throws IOException {
        String caminho = "/com/abrigo/sistema/fxml/" + nome + ".fxml";
        var url = App.class.getResource(caminho);
        if (url == null) {
            throw new IOException("Arquivo FXML não encontrado em: " + caminho);
        }
        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
    }
}