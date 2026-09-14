package com.abrigo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(carregarFXML("login"), 700, 520);
        stage.setScene(scene);
        stage.setTitle("Sistema de Cadastro SOPRAP");
        stage.setMinWidth(700);
        stage.setMinHeight(520);
        stage.show();
    }

    // Troca TODA a tela (usado só na transição Login -> Menu principal)
    public static void trocarCena(String nomeFxml) throws IOException {
        scene.setRoot(carregarFXML(nomeFxml));

        if(!nomeFxml.equalsIgnoreCase("login")){
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(550);
            primaryStage.setWidth(980);
            primaryStage.setHeight(620);
            primaryStage.centerOnScreen();
        } else {
            primaryStage.setMinWidth(700);
            primaryStage.setMinHeight(520);
            primaryStage.setWidth(700);
            primaryStage.setHeight(520);
            primaryStage.centerOnScreen();
        }
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