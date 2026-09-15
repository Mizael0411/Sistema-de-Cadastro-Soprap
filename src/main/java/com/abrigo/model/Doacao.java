package com.abrigo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "doacao")
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doacao")
    private Long id;

    @Column(name = "nome_doador", nullable = false)
    private String nomeDoador;

    @Column(name = "tipo_doacao", nullable = false)
    private String tipoDoacao; 

    @Column(name = "valor")
    private Double valor; 

    @Column(name = "descricao")
    private String descricao; 

    @Column(name = "data_doacao", nullable = false)
    private LocalDate dataDoacao; 

    public Doacao() {
    }

    public Doacao(String nomeDoador, String tipoDoacao, Double valor, String descricao, LocalDate dataDoacao) {
        this.nomeDoador = nomeDoador;
        this.tipoDoacao = tipoDoacao;
        this.valor = valor;
        this.descricao = descricao;
        this.dataDoacao = dataDoacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeDoador() {
        return nomeDoador;
    }

    public void setNomeDoador(String nomeDoador) {
        this.nomeDoador = nomeDoador;
    }

    public String getTipoDoacao() {
        return tipoDoacao;
    }

    public void setTipoDoacao(String tipoDoacao) {
        this.tipoDoacao = tipoDoacao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(LocalDate dataDoacao) {
        this.dataDoacao = dataDoacao;
    }
}