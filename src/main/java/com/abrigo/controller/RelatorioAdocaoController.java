package com.abrigo.controller;

import java.time.LocalDate;

import com.abrigo.service.RelatorioService.Relatorio;

public class RelatorioAdocaoController extends RelatorioBaseController {

    @Override
    protected String titulo() {
        return "Relatório de Adoções";
    }

    @Override
    protected String descricao() {
        return "Adoções definitivas e em lar temporário realizadas no período.";
    }

    @Override
    protected String prefixoArquivo() {
        return "relatorio_adocoes";
    }

    @Override
    protected Relatorio gerarDados(LocalDate inicio, LocalDate fim, String situacao) {
        return service.adocoes(inicio, fim);
    }
}