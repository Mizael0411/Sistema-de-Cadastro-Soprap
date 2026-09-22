package com.abrigo.controller;

import java.util.List;

import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Animal;
import com.abrigo.model.Vacina;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class FichaTecnicaAnimalController {

    @FXML private Label lblNomeEId;
    @FXML private Label lblBadgeStatus;

    @FXML private Label lblEspecie;
    @FXML private Label lblPorte;
    @FXML private Label lblSexo;
    @FXML private Label lblIdade;
    @FXML private Label lblGravidez;

    @FXML private Label lblHospedagem;
    @FXML private Label lblVoluntarioLT;
    @FXML private Label lblContatoLT;
    @FXML private Label lblStatusAdocao;
    @FXML private Label lblAdotante;

    @FXML private TableView<Vacina> tabelaVacinas;
    @FXML private TableColumn<Vacina, String> colVacinaNome;
    @FXML private TableColumn<Vacina, String> colVacinaData;

    @FXML private TextArea txtObservacoes;

    private Animal animalAtual;
    private final VacinaDAO vacinaDAO = new VacinaDAO();
    private final ObservableList<Vacina> listaVacinas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColunasVacina();

        this.animalAtual = NavigationManager.getInstance().getAnimalSelecionado();

        if (animalAtual != null) {
            preencherDadosAnimal();
            carregarVacinasAssincrono();
        } else {
            lblNomeEId.setText("Nenhum animal selecionado");
            lblBadgeStatus.setText("Sem Estado");
        }
    }

    private void configurarColunasVacina() {
        colVacinaNome.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getTipoVacina() != null ? c.getValue().getTipoVacina() : "--"
        ));
        
        colVacinaData.setCellValueFactory(c -> {
            if (c.getValue().getDataVacinacao() != null) { 
                return new SimpleStringProperty(c.getValue().getDataVacinacao().toString());
            }
            return new SimpleStringProperty("--");
        });
        
        tabelaVacinas.setItems(listaVacinas);
    }

    private void preencherDadosAnimal() {
        lblNomeEId.setText(animalAtual.getNome() + " (ID: #" + animalAtual.getId() + ")");
        lblBadgeStatus.setText("Cadastrado");

        lblEspecie.setText(animalAtual.getEspecie() != null ? animalAtual.getEspecie() : "--");
        lblPorte.setText(animalAtual.getPorte() != null ? animalAtual.getPorte() : "--");
        lblSexo.setText(animalAtual.getSexo() != null ? animalAtual.getSexo() : "--");
        lblIdade.setText(animalAtual.getIdade() != null ? animalAtual.getIdade() + " ano(s)" : "--");
        lblGravidez.setText(animalAtual.getStatusGravidez() != null ? animalAtual.getStatusGravidez() : "--");

        lblHospedagem.setText("Abrigo");
        lblVoluntarioLT.setText("--");
        lblContatoLT.setText("--");
        lblStatusAdocao.setText("Disponível");
        lblAdotante.setText("--");
    }

    private void carregarVacinasAssincrono() {
        Task<List<Vacina>> task = new Task<>() {
            @Override
            protected List<Vacina> call() {
                return vacinaDAO.buscarPorAnimalId(animalAtual.getId());
            }
        };

        task.setOnSucceeded(e -> listaVacinas.setAll(task.getValue()));

        Thread t = new Thread(task, "carregar-vacinas-ficha");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void btnEditarPetOnAction() {
        if (animalAtual != null) {
            CadastroAnimalController controller = (CadastroAnimalController)
                    NavigationManager.getInstance().navegarConteudoEObterController("cadastro-animal");

            if (controller != null) {
                controller.carregarDadosParaEdicao(animalAtual);
            }
        }
    }

    @FXML
    private void btnVoltarOnAction() {
        NavigationManager.getInstance().navegarConteudo("selecao-animal-ficha");
    }
}