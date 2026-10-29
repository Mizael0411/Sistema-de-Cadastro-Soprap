package com.abrigo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Animal;
import com.abrigo.model.NavigationManager;
import com.abrigo.model.Vacina;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class HistoricoVacinasController {

    @FXML
    private TableView<Animal> tabelaAnimais;

    @FXML
    private TableColumn<Animal, Long> colunaIdAnimal;

    @FXML
    private TableColumn<Animal, String> colunaNomeAnimal;

    @FXML
    private TableColumn<Animal, String> colunaStatusAnimal;

    @FXML
    private TableView<Vacina> tabelaHistorico;

    @FXML
    private TableColumn<Vacina, Long> colunaId;

    @FXML
    private TableColumn<Vacina, String> colunaVacina;

    @FXML
    private TableColumn<Vacina, String> colunaDose;

    @FXML
    private TableColumn<Vacina, LocalDate> colunaData;

    @FXML
    private TableColumn<Vacina, LocalDate> colunaProximaData;

    @FXML
    private TextField txtBusca;

    private final VacinaDAO vacinaDAO = new VacinaDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();

    private final ObservableList<Animal> todosAnimais =
            FXCollections.observableArrayList();

    private final ObservableList<Vacina> vacinasDoAnimal =
            FXCollections.observableArrayList();

    private FilteredList<Animal> animaisFiltrados;

    private final DateTimeFormatter formatador =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurarColunas();
        configurarBusca();
        configurarSelecaoAnimal();
        carregarAnimais();
    }

    private void configurarColunas() {
        colunaIdAnimal.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getId()));

        colunaNomeAnimal.setCellValueFactory(c -> {
            Animal animal = c.getValue();

            return new SimpleStringProperty(
                    animal.getNome() != null
                            ? animal.getNome()
                            : "Sem nome"
            );
        });

        colunaStatusAnimal.setCellValueFactory(c -> {
            Animal animal = c.getValue();

            return new SimpleStringProperty(
                    animal.getStatusVacinacao() != null
                            ? animal.getStatusVacinacao()
                            : "Sem informação"
            );
        });

        colunaId.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getId()));

        colunaVacina.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getTipoVacina() != null
                                ? c.getValue().getTipoVacina()
                                : "Não informado"
                ));

        colunaDose.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDose() != null
                                ? c.getValue().getDose()
                                : "Não informada"
                ));

        colunaData.setCellValueFactory(c ->
                new SimpleObjectProperty<>(
                        c.getValue().getDataVacinacao()
                ));

        colunaProximaData.setCellValueFactory(c ->
                new SimpleObjectProperty<>(
                        c.getValue().getDataProximaVacinacao()
                ));

        configurarFormatoData(colunaData);
        configurarFormatoData(colunaProximaData);
    }

    private void configurarFormatoData(
            TableColumn<Vacina, LocalDate> coluna) {

        coluna.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate data, boolean vazio) {
                super.updateItem(data, vazio);

                setText(
                        vazio || data == null
                                ? null
                                : formatador.format(data)
                );
            }
        });
    }

    private void configurarBusca() {
        animaisFiltrados = new FilteredList<>(
                todosAnimais,
                animal -> true
        );

        tabelaAnimais.setItems(animaisFiltrados);

        if (txtBusca != null) {
            txtBusca.textProperty().addListener(
                    (obs, anterior, atual) -> aplicarFiltro(atual)
            );
        }
    }

    private void configurarSelecaoAnimal() {
        tabelaAnimais.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, selecionado) -> {
                    if (selecionado == null) {
                        vacinasDoAnimal.clear();
                        return;
                    }

                    carregarVacinasDoAnimal(selecionado);
                });

        tabelaHistorico.setItems(vacinasDoAnimal);
    }

    private void carregarAnimais() {
        try {
            List<Animal> animais = animalDAO.listarTodos();

            todosAnimais.setAll(animais);

            if (!todosAnimais.isEmpty()) {
                tabelaAnimais.getSelectionModel().selectFirst();
            } else {
                vacinasDoAnimal.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();

            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro ao carregar animais",
                    "Não foi possível carregar os animais cadastrados.\n"
                            + e.getMessage()
            );
        }
    }

    private void carregarVacinasDoAnimal(Animal animal) {
        if (animal == null || animal.getId() == null) {
            vacinasDoAnimal.clear();
            return;
        }

        try {
            List<Vacina> vacinas =
                    vacinaDAO.listarPorAnimal(animal.getId());

            vacinasDoAnimal.setAll(vacinas);

        } catch (Exception e) {
            e.printStackTrace();
            vacinasDoAnimal.clear();

            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro ao carregar histórico",
                    "Não foi possível carregar as vacinações deste animal.\n"
                            + e.getMessage()
            );
        }
    }

    private void aplicarFiltro(String termo) {
        if (termo == null || termo.isBlank()) {
            animaisFiltrados.setPredicate(animal -> true);
            return;
        }

        String busca = termo.trim().toLowerCase();

        animaisFiltrados.setPredicate(animal -> {
            boolean nomeCorresponde =
                    animal.getNome() != null
                            && animal.getNome()
                                    .toLowerCase()
                                    .contains(busca);

            boolean idCorresponde =
                    animal.getId() != null
                            && String.valueOf(animal.getId())
                                    .contains(busca);

            boolean statusCorresponde =
                    animal.getStatusVacinacao() != null
                            && animal.getStatusVacinacao()
                                    .toLowerCase()
                                    .contains(busca);

            return nomeCorresponde
                    || idCorresponde
                    || statusCorresponde;
        });
    }

    @FXML
    public void abrirNovoRegistro() {
        Animal selecionado = tabelaAnimais
                .getSelectionModel()
                .getSelectedItem();

        NavigationManager navigation =
                NavigationManager.getInstance();

        navigation.setVacinaParaEdicao(null);

        navigation.setAnimalParaVacinar(selecionado);

        navigation.navegarConteudo("registrar-vacinas");
    }

    @FXML
    public void editarVacina() {
        Vacina selecionada = tabelaHistorico
                .getSelectionModel()
                .getSelectedItem();

        if (selecionada == null) {
            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Seleção pendente",
                    "Selecione uma vacinação na tabela de detalhes."
            );
            return;
        }

        Animal animal = selecionada.getAnimal();

        if (animal == null) {
            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Animal não encontrado",
                    "Não foi possível identificar o animal desta vacinação."
            );
            return;
        }

        if (!Boolean.TRUE.equals(animal.getAtivo())) {
            exibirAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro bloqueado",
                    "O animal está desativado. A vacinação permanece "
                            + "no histórico, mas não pode ser editada."
            );
            return;
        }

        NavigationManager navigation =
                NavigationManager.getInstance();

        navigation.setVacinaParaEdicao(selecionada);
        navigation.navegarConteudo("registrar-vacinas");
    }

    @FXML
    public void excluirVacina() {
        Vacina selecionada = tabelaHistorico
                .getSelectionModel()
                .getSelectedItem();

        if (selecionada == null) {
            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Seleção pendente",
                    "Selecione uma vacinação para excluir."
            );
            return;
        }

        Alert confirmacao =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(
                "Deseja realmente excluir o registro da vacina "
                        + selecionada.getTipoVacina()
                        + "?"
        );

        Optional<ButtonType> resultado =
                confirmacao.showAndWait();

        if (resultado.isEmpty()
                || resultado.get() != ButtonType.OK) {
            return;
        }

        Long idAnimal = selecionada.getAnimal() != null
                ? selecionada.getAnimal().getId()
                : null;

        boolean sucesso = vacinaDAO.excluir(selecionada.getId());

        if (sucesso) {
            if (idAnimal != null) {
                Animal animalSelecionado = tabelaAnimais
                        .getSelectionModel()
                        .getSelectedItem();

                if (animalSelecionado != null
                        && idAnimal.equals(animalSelecionado.getId())) {
                    carregarVacinasDoAnimal(animalSelecionado);
                }
            }

            exibirAlerta(
                    Alert.AlertType.INFORMATION,
                    "Sucesso",
                    "Registro de vacinação excluído."
            );
        } else {
            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível excluir a vacinação."
            );
        }
    }

    @FXML
    public void voltar() {
        NavigationManager.getInstance()
                .navegarConteudo("registrar-vacinas");
    }

    private void exibirAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensagem) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
