package com.abrigo.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "doacoes")
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_doador", nullable = false, length = 150)
    private String nomeDoador;

    @Column(name = "tipo_doacao", nullable = false, length = 50)
    private String tipoDoacao;

    @Column(name = "data_doacao", nullable = false)
    private LocalDate dataDoacao;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    public Doacao() {
    }

    public Doacao(String nomeDoador, String tipoDoacao, LocalDate dataDoacao, Double valor, String descricao) {
        this.nomeDoador = nomeDoador;
        this.tipoDoacao = tipoDoacao;
        this.dataDoacao = dataDoacao;
        this.valor = valor;
        this.descricao = descricao;
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

    public LocalDate getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(LocalDate dataDoacao) {
        this.dataDoacao = dataDoacao;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doacao doacao = (Doacao) o;
        return Objects.equals(id, doacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Doacao{" +
                "id=" + id +
                ", nomeDoador='" + nomeDoador + '\'' +
                ", tipoDoacao='" + tipoDoacao + '\'' +
                ", dataDoacao=" + dataDoacao +
                ", valor=" + valor +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}