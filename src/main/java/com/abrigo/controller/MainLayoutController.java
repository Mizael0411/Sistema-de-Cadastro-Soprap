package com.abrigo.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
/**
 * Classe java que divide a tela em duas areas, uma para o menu e outra para o conteúdo. O menu é carregado na área de menu e o conteúdo é carregado na área de conteúdo.
 * Camada: Controller
 * Arquivo Relacionado: main-Layout.fxml
 * @author Mizael
 */
public class MainLayoutController implements Initializable {

    @FXML private StackPane areaMenu;
    @FXML private StackPane areaConteudo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        NavigationManager nav = NavigationManager.getInstance();
        nav.setMenuContainer(areaMenu);
        nav.setContentContainer(areaConteudo);
        nav.navegarMenu("menu-principal");
        nav.navegarConteudo("boas-vindas");
    }
}