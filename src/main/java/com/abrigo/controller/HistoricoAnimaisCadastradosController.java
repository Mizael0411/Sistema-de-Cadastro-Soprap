package com.abrigo.controller;

import java.time.LocalDate;
import java.util.List;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.database.JPAUtil;
import com.abrigo.model.Animal;

import jakarta.persistence.EntityManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class HistoricoAnimaisCadastradosController {

    @FXML private TableView<Animal> tabelaAnimais;
    @FXML private TableColumn<Animal, Long> colunaIdAnimal;
    @FXML private TableColumn<Animal, String> colunaNomeAnimal;
    @FXML private TableColumn<Animal, String> colunaEspecie;
    @FXML private TableColumn<Animal, String> colunaPorte;
    @FXML private TableColumn<Animal, Integer> colunaIdadeAnimal;
    @FXML private TableColumn<Animal, String> colunaSexo;
    @FXML private TableColumn<Animal, String> colunaStsVacinacao;
    @FXML private TableColumn<Animal, LocalDate> colunaDataUltimaVacinacao;
    @FXML private TableColumn<Animal, String> colunaStsGravidez;
    @FXML private TextField txtBusca;

    private final AnimalDAO animalDAO = new AnimalDAO();

    /** Lista completa (backing) e lista filtrada (o que a tabela vê). */
    private final ObservableList<Animal> todosAnimais = FXCollections.observableArrayList();
    private FilteredList<Animal> animaisFiltrados;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarBusca();
        carregarTabelaAssincrono();
    }

    // ==========================================================
    // 1) COLUNAS — usando lambdas em vez de PropertyValueFactory
    //    (evita reflexão, deixa a tabela bem mais rápida)
    // ==========================================================
    private void configurarColunas() {
        colunaIdAnimal.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getId()));
        colunaNomeAnimal.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNome()));
        colunaEspecie.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEspecie()));
        colunaPorte.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPorte()));
        colunaIdadeAnimal.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getIdade()));
        colunaSexo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSexo()));
        colunaStsVacinacao.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatusVacinacao()));
        colunaDataUltimaVacinacao.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getDataUltimaVacinacao()));
        colunaStsGravidez.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatusGravidez()));
    }

    // ==========================================================
    // 2) BUSCA — filtra por nome OU id em tempo real
    // ==========================================================
    private void configurarBusca() {
        animaisFiltrados = new FilteredList<>(todosAnimais, p -> true);
        tabelaAnimais.setItems(animaisFiltrados);

        if (txtBusca != null) {
            txtBusca.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro(newV));
        }
    }

    private void aplicarFiltro(String termo) {
        if (termo == null || termo.isBlank()) {
            animaisFiltrados.setPredicate(a -> true);
            return;
        }
        String t = termo.trim().toLowerCase();
        animaisFiltrados.setPredicate(a -> {
            // ID bate?
            if (a.getId() != null && String.valueOf(a.getId()).equals(t)) return true;
            // Nome contém?
            if (a.getNome() != null && a.getNome().toLowerCase().contains(t)) return true;
            // ID como substring (ex: digitar "1" e aparecer 1, 10, 11...)
            if (a.getId() != null && String.valueOf(a.getId()).contains(t)) return true;
            return false;
        });
    }

    // ==========================================================
    // 3) CARREGAMENTO ASSÍNCRONO — não trava a UI
    // ==========================================================
    private void carregarTabelaAssincrono() {
        Task<List<Animal>> task = new Task<>() {
            @Override
            protected List<Animal> call() {
                return animalDAO.listarTodos();
            }
        };

        task.setOnSucceeded(e -> {
            todosAnimais.setAll(task.getValue());
            aplicarFiltro(txtBusca != null ? txtBusca.getText() : null);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro",
                    "Não foi possível carregar a lista de animais.\n" +
                            (ex != null ? ex.getMessage() : ""));
        });

        Thread t = new Thread(task, "carregar-animais");
        t.setDaemon(true);
        t.start();
    }

    // Atalho para recarregar após operações
    private void carregarTabela() {
        carregarTabelaAssincrono();
    }

    // ==========================================================
    // AÇÕES
    // ==========================================================
    @FXML
    private void inserir() {
        NavigationManager.getInstance().navegarConteudo("cadastro-animal");
    }

    @FXML
    private void editar() {
        Animal selecionado = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Necessária",
                    "Selecione um animal na tabela para editar.");
            return;
        }

        CadastroAnimalController controller = (CadastroAnimalController)
                NavigationManager.getInstance().navegarConteudoEObterController("cadastro-animal");

        if (controller != null) {
            controller.carregarDadosParaEdicao(selecionado);
        }
    }

    @FXML
    private void excluir() {
        Animal selecionado = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Necessária",
                    "Selecione um animal na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deseja realmente excluir o animal \"" + selecionado.getNome() + "\"?\n" +
                        "Os registros de vacinas vinculados também serão removidos.",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText(null);
        confirmacao.setTitle("Confirmar Exclusão");

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.YES) {
                boolean ok = excluirAnimalComDependencias(selecionado.getId());
                if (ok) {
                    carregarTabela();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                            "Animal excluído com sucesso.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro",
                            "Não foi possível excluir o animal. Verifique o console para mais detalhes.");
                }
            }
        });
    }

    /**
     * Exclusão em cascata: remove primeiro os registros de Vacina
     * vinculados (FK) e depois o próprio Animal, tudo numa única transação.
     */
    private boolean excluirAnimalComDependencias(Long animalId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();


            em.createQuery("DELETE FROM Vacina v WHERE v.animal.id = :id")
                    .setParameter("id", animalId)
                    .executeUpdate();

            // 2) Remove o animal
            Animal animal = em.find(Animal.class, animalId);
            if (animal != null) {
                em.remove(animal);
            }

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao excluir animal id=" + animalId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @FXML
    private void voltar() {
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