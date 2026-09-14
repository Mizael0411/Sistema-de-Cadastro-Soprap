package com.abrigo.view;

import java.net.URL;
import java.util.ResourceBundle;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.model.Animal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AnimaisCadastradosController implements Initializable {

    @FXML private TableView<Animal> tabelaAnimais;
    @FXML private TableColumn<Animal, Integer> colunaIdAnimal;
    @FXML private TableColumn<Animal, String> colunaNomeAnimal;
    @FXML private TableColumn<Animal, Integer> colunaIdadeAnimal;
    @FXML private TableColumn<Animal, String> colunaSexo;
    @FXML private TableColumn<Animal, String> colunaStsVacinacao;
    @FXML private TableColumn<Animal, String> colunaDataUltimaVacinacao;
    @FXML private TableColumn<Animal, String> colunaStsAdocao;
    @FXML private TableColumn<Animal, String> colunaStsGravidez;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Vincula o fx:id das colunas com os nomes dos atributos (getters) da classe Animal
        colunaIdAnimal.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNomeAnimal.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaIdadeAnimal.setCellValueFactory(new PropertyValueFactory<>("idade"));
        colunaSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        colunaStsVacinacao.setCellValueFactory(new PropertyValueFactory<>("statusVacinacao"));
        colunaDataUltimaVacinacao.setCellValueFactory(new PropertyValueFactory<>("dataUltimaVacinacao"));
        //colunaStsAdocao.setCellValueFactory(new PropertyValueFactory<>("statusAdocao"));
        colunaStsGravidez.setCellValueFactory(new PropertyValueFactory<>("statusGravidez"));

        carregarDadosTabela();
    }
    @FXML
    private void atualizarTabela() {
        carregarDadosTabela();
    }

    private void carregarDadosTabela() {
        AnimalDAO dao = new AnimalDAO();
        ObservableList<Animal> listaAnimais = FXCollections.observableArrayList(dao.listarTodos());
        tabelaAnimais.setItems(listaAnimais);
    }
}