package com.abrigo.view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.awt.*;

/**
 * Controller responsável pela gestão da interface gráfica.
 * 
 * Camada: Controller
 * FXML Associado: cadastroProduto.fxml
 * 
 * @author Windows 10
 * @created 29/08/2026
 */
public class CadastroProdutoController {

    @FXML private TextField txtNomeProduto;
    @FXML private TextField txtPrecoProduto;
    @FXML private TextField txtDescricaoProduto;
    @FXML private Spinner<Integer> spQuantidadeProduto;
    @FXML private ComboBox<String> cbInsumoProduto;


    public  String getNomeProduto() {
        // TODO: Implementar lógica para obter o nome do produto
        return null;
    }

    public String getPrecoProduto() {
        // TODO: Implementar lógica para obter o preço do produto
        return null;
    }

}