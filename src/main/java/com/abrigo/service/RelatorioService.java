package com.abrigo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Adocao;
import com.abrigo.model.Animal;
import com.abrigo.model.Doacao;
import com.abrigo.model.ItemDoacao;
import com.abrigo.model.LarTemp;
import com.abrigo.model.Produto;

import jakarta.persistence.EntityManager;

public class RelatorioService {

    public static final String SITUACAO_TODOS = "Todos";
    public static final String SITUACAO_LAR = "Em lar temporário";
    public static final String SITUACAO_ADOTADO = "Adotados";
    public static final String SITUACAO_DISPONIVEL = "Disponíveis";

    private static final String ADOTADO_DEFINITIVO = "Adotado Definitivo";
    private static final String ADOTADO_LAR_TEMPORARIO = "Adotado Lar Temporario";
    private static final String ATIVA = "ATIVA";

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public enum Tipo { TEXTO, INTEIRO, DECIMAL, MOEDA, DATA }

    public record Coluna(String titulo, Tipo tipo) { }

    public record Secao(String nome, List<Coluna> colunas, List<List<Object>> linhas, boolean totais) { }

    public record Indicador(String rotulo, Object valor, Tipo tipo) { }

    public record Relatorio(String titulo, String subtitulo,
                            List<Indicador> indicadores, List<Secao> secoes) { }

    public Relatorio financeiro(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Doacao> doacoes = em.createQuery(
                    "select d from Doacao d "
                            + "where upper(d.tipoDoacao) = 'FINANCEIRA' "
                            + "and d.valor is not null "
                            + "and d.dataDoacao between :ini and :fim "
                            + "order by d.dataDoacao",
                    Doacao.class)
                    .setParameter("ini", inicio)
                    .setParameter("fim", fim)
                    .getResultList();

            List<Produto> produtos = em.createQuery(
                    "select p from Produto p "
                            + "where p.dataCompra between :ini and :fim "
                            + "order by p.dataCompra",
                    Produto.class)
                    .setParameter("ini", inicio)
                    .setParameter("fim", fim)
                    .getResultList();

            double entradas = 0;
            double saidas = 0;
            List<List<Object>> linhas = new ArrayList<>();

            for (Doacao d : doacoes) {
                double v = valor(d.getValor());
                entradas += v;
                linhas.add(linha(d.getDataDoacao(), "Doação", n(d.getNomeDoador()),
                        n(d.getDescricao()), v, null));
            }
            for (Produto p : produtos) {
                double v = valor(p.getPrecoCompra());
                saidas += v;
                linhas.add(linha(p.getDataCompra(), "Gasto", n(p.getNome()),
                        "Compra de produto/insumo", null, v));
            }
            linhas.sort(Comparator.comparing(l -> (LocalDate) l.get(0)));

            double saldo = entradas - saidas;
            List<Indicador> kpis = List.of(
                    new Indicador("Total de doações", entradas, Tipo.MOEDA),
                    new Indicador("Total de gastos", saidas, Tipo.MOEDA),
                    new Indicador("Saldo", saldo, Tipo.MOEDA),
                    new Indicador("Situação",
                            saldo > 0 ? "Positivo" : saldo < 0 ? "Negativo" : "Zerado", Tipo.TEXTO));

            Secao secao = new Secao("Movimentação",
                    List.of(new Coluna("Data", Tipo.DATA),
                            new Coluna("Tipo", Tipo.TEXTO),
                            new Coluna("Origem / Produto", Tipo.TEXTO),
                            new Coluna("Descrição", Tipo.TEXTO),
                            new Coluna("Entrada (R$)", Tipo.MOEDA),
                            new Coluna("Saída (R$)", Tipo.MOEDA)),
                    linhas, true);

            return new Relatorio("Relatório Financeiro", periodo(inicio, fim), kpis, List.of(secao));
        } finally {
            em.close();
        }
    }

    public Relatorio doacoes(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Doacao> doacoes = em.createQuery(
                    "select d from Doacao d "
                            + "where d.dataDoacao between :ini and :fim "
                            + "order by d.dataDoacao desc, d.id desc",
                    Doacao.class)
                    .setParameter("ini", inicio)
                    .setParameter("fim", fim)
                    .getResultList();

            List<ItemDoacao> itens = em.createQuery(
                    "select i from ItemDoacao i join fetch i.doacao d "
                            + "where d.dataDoacao between :ini and :fim "
                            + "order by d.dataDoacao desc, i.nomeItem",
                    ItemDoacao.class)
                    .setParameter("ini", inicio)
                    .setParameter("fim", fim)
                    .getResultList();

            long financeiras = 0;
            long materiais = 0;
            double arrecadado = 0;
            Set<String> doadores = new HashSet<>();
            List<List<Object>> linhas = new ArrayList<>();

            for (Doacao d : doacoes) {
                boolean financeira = "FINANCEIRA".equalsIgnoreCase(d.getTipoDoacao());
                if (financeira) {
                    financeiras++;
                    arrecadado += valor(d.getValor());
                } else {
                    materiais++;
                }
                doadores.add(n(d.getNomeDoador()).trim().toLowerCase());
                linhas.add(linha(d.getDataDoacao(), n(d.getNomeDoador()),
                        financeira ? "Financeira" : "Material",
                        n(d.getDescricao()),
                        financeira ? d.getValor() : null));
            }

            List<List<Object>> linhasItens = new ArrayList<>();
            for (ItemDoacao i : itens) {
                linhasItens.add(linha(i.getDoacao().getDataDoacao(),
                        n(i.getDoacao().getNomeDoador()),
                        n(i.getNomeItem()),
                        i.getQuantidade() == null ? null : i.getQuantidade().doubleValue(),
                        i.getValidade()));
            }

            List<Indicador> kpis = List.of(
                    new Indicador("Doações no período", (long) doacoes.size(), Tipo.INTEIRO),
                    new Indicador("Financeiras", financeiras, Tipo.INTEIRO),
                    new Indicador("Materiais", materiais, Tipo.INTEIRO),
                    new Indicador("Valor arrecadado", arrecadado, Tipo.MOEDA),
                    new Indicador("Doadores", (long) doadores.size(), Tipo.INTEIRO));

            Secao principal = new Secao("Doações",
                    List.of(new Coluna("Data", Tipo.DATA),
                            new Coluna("Doador", Tipo.TEXTO),
                            new Coluna("Tipo", Tipo.TEXTO),
                            new Coluna("Descrição", Tipo.TEXTO),
                            new Coluna("Valor (R$)", Tipo.MOEDA)),
                    linhas, true);

            Secao itensRecebidos = new Secao("Itens recebidos",
                    List.of(new Coluna("Data", Tipo.DATA),
                            new Coluna("Doador", Tipo.TEXTO),
                            new Coluna("Item", Tipo.TEXTO),
                            new Coluna("Quantidade", Tipo.DECIMAL),
                            new Coluna("Validade", Tipo.DATA)),
                    linhasItens, false);

            return new Relatorio("Relatório de Doações", periodo(inicio, fim), kpis,
                    List.of(principal, itensRecebidos));
        } finally {
            em.close();
        }
    }

    public Relatorio adocoes(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Adocao> adocoes = em.createQuery(
                    "select a from Adocao a "
                            + "join fetch a.animal "
                            + "left join fetch a.larTemp "
                            + "where a.statusRegistro = :ativa "
                            + "and a.statusAdocao in (:definitivo, :lar) "
                            + "and a.dataAdocao between :ini and :fim "
                            + "order by a.dataAdocao desc, a.id desc",
                    Adocao.class)
                    .setParameter("ativa", ATIVA)
                    .setParameter("definitivo", ADOTADO_DEFINITIVO)
                    .setParameter("lar", ADOTADO_LAR_TEMPORARIO)
                    .setParameter("ini", inicio)
                    .setParameter("fim", fim)
                    .getResultList();

            long definitivas = 0;
            long lares = 0;
            long especiais = 0;
            List<List<Object>> linhas = new ArrayList<>();

            for (Adocao a : adocoes) {
                boolean lar = ADOTADO_LAR_TEMPORARIO.equals(a.getStatusAdocao());
                if (lar) {
                    lares++;
                } else {
                    definitivas++;
                }
                if (Boolean.TRUE.equals(a.getAdocaoEspecial())) {
                    especiais++;
                }

                Animal animal = a.getAnimal();
                LarTemp larTemp = a.getLarTemp();
                String responsavel = lar && larTemp != null ? n(larTemp.getNome()) : n(a.getNomeTutor());
                String telefone = lar && larTemp != null ? n(larTemp.getTelefone()) : n(a.getTelefoneTutor());

                linhas.add(linha(a.getDataAdocao(),
                        n(animal.getNome()),
                        animal.getId(),
                        n(animal.getEspecie()),
                        lar ? "Lar temporário" : "Definitiva",
                        responsavel,
                        telefone,
                        Boolean.TRUE.equals(a.getAdocaoEspecial()) ? "Sim" : "Não"));
            }

            List<Indicador> kpis = List.of(
                    new Indicador("Adoções no período", (long) adocoes.size(), Tipo.INTEIRO),
                    new Indicador("Definitivas", definitivas, Tipo.INTEIRO),
                    new Indicador("Lares temporários", lares, Tipo.INTEIRO),
                    new Indicador("Adoções especiais", especiais, Tipo.INTEIRO));

            Secao secao = new Secao("Adoções",
                    List.of(new Coluna("Data", Tipo.DATA),
                            new Coluna("Animal", Tipo.TEXTO),
                            new Coluna("ID", Tipo.INTEIRO),
                            new Coluna("Espécie", Tipo.TEXTO),
                            new Coluna("Tipo", Tipo.TEXTO),
                            new Coluna("Tutor / Lar", Tipo.TEXTO),
                            new Coluna("Telefone", Tipo.TEXTO),
                            new Coluna("Especial", Tipo.TEXTO)),
                    linhas, false);

            return new Relatorio("Relatório de Adoções", periodo(inicio, fim), kpis, List.of(secao));
        } finally {
            em.close();
        }
    }

    public Relatorio animais(String situacaoFiltro) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Animal> animais = em.createQuery(
                    "select a from Animal a where a.ativo = true order by a.nome",
                    Animal.class)
                    .getResultList();

            List<Adocao> ativas = em.createQuery(
                    "select a from Adocao a "
                            + "join fetch a.animal "
                            + "left join fetch a.larTemp "
                            + "where a.statusRegistro = :ativa "
                            + "and a.statusAdocao in (:definitivo, :lar) "
                            + "order by a.dataAdocao asc, a.id asc",
                    Adocao.class)
                    .setParameter("ativa", ATIVA)
                    .setParameter("definitivo", ADOTADO_DEFINITIVO)
                    .setParameter("lar", ADOTADO_LAR_TEMPORARIO)
                    .getResultList();

            List<LarTemp> lares = em.createQuery(
                    "select l from LarTemp l order by l.nome",
                    LarTemp.class)
                    .getResultList();

            Map<Long, Adocao> adocaoPorAnimal = new HashMap<>();
            for (Adocao a : ativas) {
                adocaoPorAnimal.put(a.getAnimal().getId(), a);
            }

            long adotados = 0;
            long emLar = 0;
            long disponiveis = 0;
            List<List<Object>> linhas = new ArrayList<>();
            Map<Long, List<String>> animaisPorLar = new HashMap<>();

            for (Animal animal : animais) {
                Adocao adocao = adocaoPorAnimal.get(animal.getId());
                String situacao;
                String responsavel = "";
                LocalDate desde = null;

                if (adocao == null) {
                    situacao = "Disponível";
                    disponiveis++;
                } else if (ADOTADO_LAR_TEMPORARIO.equals(adocao.getStatusAdocao())) {
                    situacao = "Lar temporário";
                    emLar++;
                    LarTemp lar = adocao.getLarTemp();
                    responsavel = lar != null ? n(lar.getNome()) : n(adocao.getNomeTutor());
                    desde = adocao.getDataAdocao();
                    if (lar != null) {
                        animaisPorLar.computeIfAbsent(lar.getId(), k -> new ArrayList<>())
                                .add(n(animal.getNome()));
                    }
                } else {
                    situacao = "Adotado";
                    adotados++;
                    responsavel = n(adocao.getNomeTutor());
                    desde = adocao.getDataAdocao();
                }

                if (!combinaComFiltro(situacao, situacaoFiltro)) {
                    continue;
                }
                linhas.add(linha(animal.getId(), n(animal.getNome()), n(animal.getEspecie()),
                        n(animal.getSexo()), n(animal.getPorte()), situacao, responsavel, desde));
            }

            long vagasLivres = 0;
            List<List<Object>> linhasLares = new ArrayList<>();
            for (LarTemp l : lares) {
                int capacidade = l.getCapacidadeMaxima() == null ? 0 : l.getCapacidadeMaxima();
                List<String> alocados = animaisPorLar.getOrDefault(l.getId(), List.of());
                int vagas = Math.max(0, capacidade - alocados.size());
                vagasLivres += vagas;
                linhasLares.add(linha(n(l.getNome()), n(l.getTelefone()),
                        (long) capacidade, (long) alocados.size(), (long) vagas,
                        String.join(", ", alocados)));
            }

            List<Indicador> kpis = List.of(
                    new Indicador("Animais ativos", (long) animais.size(), Tipo.INTEIRO),
                    new Indicador("Adotados", adotados, Tipo.INTEIRO),
                    new Indicador("Em lar temporário", emLar, Tipo.INTEIRO),
                    new Indicador("Disponíveis", disponiveis, Tipo.INTEIRO),
                    new Indicador("Vagas livres nos lares", vagasLivres, Tipo.INTEIRO));

            Secao secaoAnimais = new Secao("Animais",
                    List.of(new Coluna("ID", Tipo.INTEIRO),
                            new Coluna("Nome", Tipo.TEXTO),
                            new Coluna("Espécie", Tipo.TEXTO),
                            new Coluna("Sexo", Tipo.TEXTO),
                            new Coluna("Porte", Tipo.TEXTO),
                            new Coluna("Situação", Tipo.TEXTO),
                            new Coluna("Tutor / Lar", Tipo.TEXTO),
                            new Coluna("Desde", Tipo.DATA)),
                    linhas, false);

            Secao secaoLares = new Secao("Lares temporários",
                    List.of(new Coluna("Lar (tutor)", Tipo.TEXTO),
                            new Coluna("Telefone", Tipo.TEXTO),
                            new Coluna("Capacidade", Tipo.INTEIRO),
                            new Coluna("Ocupadas", Tipo.INTEIRO),
                            new Coluna("Vagas livres", Tipo.INTEIRO),
                            new Coluna("Animais acolhidos", Tipo.TEXTO)),
                    linhasLares, false);

            String sub = "Situação atual em " + LocalDate.now().format(DATA_BR)
                    + (situacaoFiltro == null || SITUACAO_TODOS.equals(situacaoFiltro)
                            ? "" : "  •  Filtro: " + situacaoFiltro);

            return new Relatorio("Relatório de Animais", sub, kpis, List.of(secaoAnimais, secaoLares));
        } finally {
            em.close();
        }
    }

    private static boolean combinaComFiltro(String situacao, String filtro) {
        if (filtro == null || SITUACAO_TODOS.equals(filtro)) {
            return true;
        }
        return switch (filtro) {
            case SITUACAO_LAR -> "Lar temporário".equals(situacao);
            case SITUACAO_ADOTADO -> "Adotado".equals(situacao);
            case SITUACAO_DISPONIVEL -> "Disponível".equals(situacao);
            default -> true;
        };
    }

    private static List<Object> linha(Object... valores) {
        return new ArrayList<>(Arrays.asList(valores));
    }

    private static void validarPeriodo(LocalDate i, LocalDate f) {
        if (i == null || f == null) {
            throw new IllegalArgumentException("Informe a data inicial e final.");
        }
        if (i.isAfter(f)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }
    }

    private static String periodo(LocalDate i, LocalDate f) {
        return "Período: " + i.format(DATA_BR) + " a " + f.format(DATA_BR);
    }

    private static double valor(Double v) {
        return v == null ? 0 : v;
    }

    private static String n(String s) {
        return s == null ? "" : s;
    }
}
