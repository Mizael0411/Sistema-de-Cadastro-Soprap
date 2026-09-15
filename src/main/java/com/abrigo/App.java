package com.abrigo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        var iconUrl = App.class.getResource("/com/abrigo/images/soprap-logo.png");
        if (iconUrl != null) {
            stage.getIcons().add(new Image(iconUrl.toExternalForm()));
        } else {
            System.err.println("Ícone não encontrado em /com/abrigo/images/soprap-logo.png");
        }
        
        scene = new Scene(carregarFXML("login"), 1200, 750);
        stage.setScene(scene);
        stage.setTitle("Sistema de Cadastro SOPRAP");
        
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setWidth(1200);
        stage.setHeight(750);
        
        stage.centerOnScreen();
        stage.show();
    }

    public static void trocarCena(String nomeFxml) throws IOException {
        scene.setRoot(carregarFXML(nomeFxml));
        primaryStage.centerOnScreen();
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