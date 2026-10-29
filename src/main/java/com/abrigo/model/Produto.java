package com.abrigo.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double precoCompra;
    private LocalDate dataCompra;
    private LocalDate dataValidade;

    public Produto() {}

    public Produto(String nome, Double precoCompra, LocalDate dataCompra) {
        this.nome = nome;
        this.precoCompra = precoCompra;
        this.dataCompra = dataCompra;
    }

    public Produto(String nome, Double precoCompra,
                   LocalDate dataCompra, LocalDate dataValidade) {
        this.nome = nome;
        this.precoCompra = precoCompra;
        this.dataCompra = dataCompra;
        this.dataValidade = dataValidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(Double precoCompra) {
        this.precoCompra = precoCompra;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public boolean isVencido() {
        return dataValidade != null
                && dataValidade.isBefore(LocalDate.now());
    }

    public boolean isProximoDoVencimento() {
        return dataValidade != null
                && !isVencido()
                && !dataValidade.isAfter(LocalDate.now().plusDays(30));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}