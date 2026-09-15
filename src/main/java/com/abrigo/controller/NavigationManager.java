package com.abrigo.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigationManager {

    private static NavigationManager instance;
    
    private StackPane menuContainer;
    private StackPane contentContainer;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void setMenuContainer(StackPane menuContainer) {
        this.menuContainer = menuContainer;
    }

    public void setContentContainer(StackPane contentContainer) {
        this.contentContainer = contentContainer;
    }
    
    public void navegarMenu(String nomeFxml) {
        carregarNoContainer(menuContainer, nomeFxml);
    }

    public void navegarConteudo(String nomeFxml) {
        carregarNoContainer(contentContainer, nomeFxml);
    }

    public Object navegarConteudoEObterController(String nomeFxml) {
        try {
            String caminho = "/com/abrigo/sistema/fxml/" + nomeFxml + ".fxml";
            var url = getClass().getResource(caminho);
            if (url == null) {
                System.err.println("Arquivo FXML não encontrado em: " + caminho);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();

            if (contentContainer != null) {
                contentContainer.getChildren().setAll(view);
            }

            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void voltarMenu() {
        navegarMenu("menu-principal");
        navegarConteudo("boas-vindas");
    }

    private void carregarNoContainer(StackPane container, String nomeFxml) {
        if (container == null) {
            System.err.println("Container de navegação não foi inicializado.");
            return;
        }

        try {
            String caminho = "/com/abrigo/sistema/fxml/" + nomeFxml + ".fxml";
            var url = getClass().getResource(caminho);
            if (url == null) {
                System.err.println("Arquivo FXML não encontrado em: " + caminho);
                return;
            }

            Parent view = FXMLLoader.load(url);

            container.getChildren().setAll(view);
        
        } catch (IOException e) {
             e.printStackTrace();
            }
        }
}