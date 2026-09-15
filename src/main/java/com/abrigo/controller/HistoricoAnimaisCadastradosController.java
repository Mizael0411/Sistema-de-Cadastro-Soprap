package com.abrigo.controller;

import java.util.Date;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.model.Animal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoricoAnimaisCadastradosController {

    @FXML private TableView<Animal> tabelaAnimais;
    @FXML private TableColumn<Animal, Integer> colunaIdAnimal;
    @FXML private TableColumn<Animal, String> colunaNomeAnimal;
    @FXML private TableColumn<Animal, Integer> colunaIdadeAnimal;
    @FXML private TableColumn<Animal, String> colunaSexo;
    @FXML private TableColumn<Animal, String> colunaStsVacinacao;
    @FXML private TableColumn<Animal, Date> colunaDataUltimaVacinacao;
    @FXML private TableColumn<Animal, String> colunaStsGravidez;

    private AnimalDAO animalDAO = new AnimalDAO();

    @FXML
    public void initialize() {
        // Mapeamento das propriedades correspondentes à entidade Animal.java
        colunaIdAnimal.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNomeAnimal.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaIdadeAnimal.setCellValueFactory(new PropertyValueFactory<>("idade"));
        colunaSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        colunaStsVacinacao.setCellValueFactory(new PropertyValueFactory<>("statusVacinacao"));
        colunaDataUltimaVacinacao.setCellValueFactory(new PropertyValueFactory<>("dataUltimaVacinacao"));
        colunaStsGravidez.setCellValueFactory(new PropertyValueFactory<>("statusGravidez"));

        carregarTabela();
    }

    private void carregarTabela() {
        if (animalDAO != null) {
            ObservableList<Animal> lista = FXCollections.observableArrayList(animalDAO.listarTodos());
            tabelaAnimais.setItems(lista);
        }
    }

    @FXML
    private void inserir() {
        NavigationManager.getInstance().navegarConteudo("cadastro-animal");
    }

    @FXML
    private void editar() {
        Animal selecionado = tabelaAnimais.getSelectionModel().getSelectedItem();
        
        if (selecionado != null) {
            // Abre a tela de cadastro e injeta o animal selecionado para alteração
            CadastroAnimalController controller = (CadastroAnimalController) 
                NavigationManager.getInstance().navegarConteudoEObterController("cadastro-animal");
            
            if (controller != null) {
                controller.carregarDadosParaEdicao(selecionado);
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um animal na tabela para editar.");
        }
    }

    @FXML
    private void excluir() {
        Animal selecionado = tabelaAnimais.getSelectionModel().getSelectedItem();
        
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um animal na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(
            Alert.AlertType.CONFIRMATION, 
            "Deseja realmente excluir o animal \"" + selecionado.getNome() + "\"?", 
            ButtonType.YES, 
            ButtonType.NO
        );
        
        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.YES) {
                try {
                    if (animalDAO.excluir(selecionado.getId())) {
                        carregarTabela();
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Animal excluído com sucesso.");
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível excluir o animal.");
                    }
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro de Integridade", "Este animal possui vínculos com vacinas ou doações e não pode ser excluído diretamente.");
                }
            }
        });
    }

    @FXML
private void voltar() {
    // Redireciona para a rota do formulário de cadastro de animais
    NavigationManager.getInstance().navegarConteudo("cadastro-animal"); 
}

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}