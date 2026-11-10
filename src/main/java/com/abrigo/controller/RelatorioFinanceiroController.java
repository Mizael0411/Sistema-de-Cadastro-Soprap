package com.abrigo.controller;

import java.time.LocalDate;

import com.abrigo.service.RelatorioService.Relatorio;

public class RelatorioFinanceiroController extends RelatorioBaseController {

    @Override
    protected String titulo() {
        return "Relatório Financeiro";
    }

    @Override
    protected String descricao() {
        return "Entradas (doações em dinheiro) e saídas (compras de produtos e insumos) no período.";
    }

    @Override
    protected String prefixoArquivo() {
        return "relatorio_financeiro";
    }

    @Override
    protected Relatorio gerarDados(LocalDate inicio, LocalDate fim, String situacao) {
        return service.financeiro(inicio, fim);
    }
}
