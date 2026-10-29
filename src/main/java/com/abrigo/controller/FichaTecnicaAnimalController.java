package com.abrigo.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.abrigo.dao.AdocaoDAO;
import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Adocao;
import com.abrigo.model.Animal;
import com.abrigo.model.LarTemp;
import com.abrigo.model.NavigationManager;
import com.abrigo.model.Vacina;
import com.abrigo.util.FormatadorTelefone;

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
    @FXML private Label lblPossuiDeficiencia;
    @FXML private Label lblDescricaoDeficiencia;

    @FXML private Label lblHospedagem;
    @FXML private Label lblRotuloResponsavel;
    @FXML private Label lblVoluntarioLT;
    @FXML private Label lblContatoLT;
    @FXML private Label lblStatusAdocao;
    @FXML private Label lblAdotante;
    @FXML private Label lblDataAdocao;

    @FXML private TableView<Vacina> tabelaVacinas;
    @FXML private TableColumn<Vacina, String> colVacinaNome;
    @FXML private TableColumn<Vacina, String> colVacinaData;
    @FXML private TableColumn<Vacina, String> colVacinaDose;
    @FXML private TableColumn<Vacina, String> colProximaVacina;

    @FXML private TextArea txtObservacoes;

    private Animal animalAtual;

    private final VacinaDAO vacinaDAO = new VacinaDAO();
    private final AdocaoDAO adocaoDAO = new AdocaoDAO();

    private final ObservableList<Vacina> listaVacinas =
            FXCollections.observableArrayList();

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurarColunasVacina();

        animalAtual = NavigationManager.getInstance()
                .getAnimalSelecionado();

        if (animalAtual != null) {
            preencherDadosAnimal();
            preencherDadosAdocao();
            carregarVacinasAssincrono();
        } else {
            lblNomeEId.setText("Nenhum animal selecionado");
            lblBadgeStatus.setText("Sem Estado");

            lblHospedagem.setText("--");
            lblRotuloResponsavel.setText("Responsável:");
            lblVoluntarioLT.setText("--");
            lblContatoLT.setText("--");

            lblStatusAdocao.setText("Sem adoção registrada");
            lblAdotante.setText("--");
            lblDataAdocao.setText("--");
        }
    }

    private void configurarColunasVacina() {
        colVacinaNome.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getTipoVacina() != null
                                ? c.getValue().getTipoVacina()
                                : "--"
                )
        );

        colVacinaDose.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDose() != null
                                ? c.getValue().getDose()
                                : "--"
                )
        );

        colVacinaData.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDataVacinacao() != null
                                ? c.getValue().getDataVacinacao()
                                        .format(FORMATO_DATA)
                                : "--"
                )
        );

        colProximaVacina.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDataProximaVacinacao() != null
                                ? c.getValue().getDataProximaVacinacao()
                                        .format(FORMATO_DATA)
                                : "--"
                )
        );

        tabelaVacinas.setItems(listaVacinas);
    }

    private void preencherDadosAnimal() {
        lblNomeEId.setText(
                animalAtual.getNome()
                        + " (ID: #" + animalAtual.getId() + ")"
        );

        lblEspecie.setText(
                animalAtual.getEspecie() != null
                        ? animalAtual.getEspecie() : "--"
        );

        lblPorte.setText(
                animalAtual.getPorte() != null
                        ? animalAtual.getPorte() : "--"
        );

        lblSexo.setText(
                animalAtual.getSexo() != null
                        ? animalAtual.getSexo() : "--"
        );

        lblIdade.setText(
                animalAtual.getIdade() != null
                        ? animalAtual.getIdade() + " ano(s)" : "--"
        );

        lblGravidez.setText(
                animalAtual.getStatusGravidez() != null
                        ? animalAtual.getStatusGravidez() : "--"
        );

        boolean possuiDeficiencia =
                Boolean.TRUE.equals(animalAtual.getPossuiDeficiencia());

        lblPossuiDeficiencia.setText(
                possuiDeficiencia ? "Sim" : "Não"
        );

        String descricao = animalAtual.getDescricaoDeficiencia();

        lblDescricaoDeficiencia.setText(
                possuiDeficiencia
                        && descricao != null
                        && !descricao.isBlank()
                                ? descricao
                                : "--"
        );
    }

    private void preencherDadosAdocao() {
        Adocao adocao =
                adocaoDAO.buscarPorAnimalId(animalAtual.getId());

        if (adocao == null) {
            lblBadgeStatus.setText("Disponível");

            lblStatusAdocao.setText("Sem adoção registrada");
            lblAdotante.setText("--");
            lblDataAdocao.setText("--");

            lblHospedagem.setText("Abrigo");
            lblRotuloResponsavel.setText("Responsável:");
            lblVoluntarioLT.setText("--");
            lblContatoLT.setText("--");

            return;
        }

        String status = adocao.getStatusAdocao();

        if (status == null || status.isBlank()) {
            lblBadgeStatus.setText("Cadastrado");
            lblStatusAdocao.setText("Status não informado");
        } else {
            lblStatusAdocao.setText(status);

            if (status.equalsIgnoreCase("Adotado Definitivo")) {
                lblBadgeStatus.setText("Adotado");
            } else if (status.equalsIgnoreCase(
                    "Adotado Lar Temporario")) {
                lblBadgeStatus.setText("Lar temporário");
            } else {
                lblBadgeStatus.setText("Cadastrado");
            }
        }

        String adotante = adocao.getNomeTutor();

        lblAdotante.setText(
                textoOuTraco(adotante)
        );

        lblDataAdocao.setText(
                adocao.getDataAdocao() != null
                        ? adocao.getDataAdocao().format(FORMATO_DATA)
                        : "--"
        );

        LarTemp larTemp = adocao.getLarTemp();

        if (status != null
                && status.equalsIgnoreCase("Adotado Lar Temporario")) {

            lblHospedagem.setText("Lar temporário");
            lblRotuloResponsavel.setText(
                    "Voluntário lar temporário:"
            );

            if (larTemp != null) {
                lblVoluntarioLT.setText(
                        textoOuTraco(larTemp.getNome())
                );

                lblContatoLT.setText(
                        formatarTelefoneOuTraco(larTemp.getTelefone())
                );
            } else {
                lblVoluntarioLT.setText("--");
                lblContatoLT.setText("--");
            }

        } else if (status != null
                && status.equalsIgnoreCase("Adotado Definitivo")) {

            lblHospedagem.setText("Adotado definitivamente");
            lblRotuloResponsavel.setText("Nome do dono:");

            lblVoluntarioLT.setText(
                    textoOuTraco(adocao.getNomeTutor())
            );

            lblContatoLT.setText(
                    formatarTelefoneOuTraco(adocao.getTelefoneTutor())
            );

        } else {
            lblHospedagem.setText(
                    larTemp != null ? "Lar temporário" : "Abrigo"
            );

            lblRotuloResponsavel.setText(
                    larTemp != null
                            ? "Voluntário lar temporário:"
                            : "Responsável:"
            );

            lblVoluntarioLT.setText(
                    larTemp != null
                            ? textoOuTraco(larTemp.getNome())
                            : "--"
            );

            lblContatoLT.setText(
                    larTemp != null
                            ? formatarTelefoneOuTraco(larTemp.getTelefone())
                            : "--"
            );
        }
    }

    private String textoOuTraco(String texto) {
        return texto != null && !texto.isBlank() ? texto : "--";
    }

    private String formatarTelefoneOuTraco(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return "--";
        }

        return FormatadorTelefone.formatar(telefone);
    }

    private void carregarVacinasAssincrono() {
        Task<List<Vacina>> task = new Task<>() {
            @Override
            protected List<Vacina> call() {
                return vacinaDAO.buscarPorAnimalId(animalAtual.getId());
            }
        };

        task.setOnSucceeded(e ->
                listaVacinas.setAll(task.getValue())
        );

        task.setOnFailed(e ->
                System.err.println(
                        "Erro ao carregar vacinas: "
                                + task.getException().getMessage()
                )
        );

        Thread t = new Thread(task, "carregar-vacinas-ficha");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void btnEditarPetOnAction() {
        if (animalAtual != null) {
            CadastroAnimalController controller =
                    (CadastroAnimalController)
                            NavigationManager.getInstance()
                                    .navegarConteudoEObterController(
                                            "cadastro-animal"
                                    );

            if (controller != null) {
                controller.carregarDadosParaEdicao(animalAtual);
            }
        }
    }

    @FXML
    private void btnVoltarOnAction() {
        NavigationManager.getInstance()
                .navegarConteudo("selecao-animal-ficha");
    }
}
