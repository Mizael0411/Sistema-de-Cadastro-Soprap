package com.abrigo.controller;

import java.time.LocalDate;
import java.util.List;

import com.abrigo.service.RelatorioService;
import com.abrigo.service.RelatorioService.Relatorio;

public class RelatorioAnimaisController extends RelatorioBaseController {

    @Override
    protected String titulo() {
        return "Relatório de Animais";
    }

    @Override
    protected String descricao() {
        return "Situação atual dos animais (lar temporário, adotado ou disponível) e ocupação dos lares.";
    }

    @Override
    protected String prefixoArquivo() {
        return "relatorio_animais";
    }

    @Override
    protected boolean usaPeriodo() {
        return false;
    }

    @Override
    protected List<String> opcoesSituacao() {
        return List.of(RelatorioService.SITUACAO_TODOS,
                RelatorioService.SITUACAO_LAR,
                RelatorioService.SITUACAO_ADOTADO,
                RelatorioService.SITUACAO_DISPONIVEL);
    }

    @Override
    protected Relatorio gerarDados(LocalDate inicio, LocalDate fim, String situacao) {
        return service.animais(situacao);
    }
}