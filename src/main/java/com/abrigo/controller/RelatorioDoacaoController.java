package com.abrigo.controller;

import java.time.LocalDate;

import com.abrigo.service.RelatorioService.Relatorio;

public class RelatorioDoacaoController extends RelatorioBaseController {

    @Override
    protected String titulo() {
        return "Relatório de Doações";
    }

    @Override
    protected String descricao() {
        return "Todas as doações recebidas no período (dinheiro e itens), com os itens e validades.";
    }

    @Override
    protected String prefixoArquivo() {
        return "relatorio_doacoes";
    }

    @Override
    protected Relatorio gerarDados(LocalDate inicio, LocalDate fim, String situacao) {
        return service.doacoes(inicio, fim);
    }
}