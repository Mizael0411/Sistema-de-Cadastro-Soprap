package com.abrigo.view;

import com.abrigo.model.Animal;
import com.abrigo.model.Vacina;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoricoVacinasController {

    @FXML
    private ComboBox<Animal> cbAnimal;

    @FXML
    private TableView<Vacina> tabelaHistorico;

    @FXML
    private TableColumn<Vacina, String> colunaVacina;

    @FXML
    private TableColumn<Vacina, String> colunaData;
    
    @FXML
    private Button voltarButton;

    @FXML void initialize() {

        colunaVacina.setCellValueFactory(
            cellData -> new SimpleStringProperty(
                cellData.getValue().getTipo_vacina()
            )
        );

        colunaData.setCellValueFactory(
            cellData -> new SimpleStringProperty(
                cellData.getValue().getData_vacinacao() != null ? cellData.getValue().getData_vacinacao().toString() : ""
            )
        );

    }

    @FXML void voltar() {
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }

}
