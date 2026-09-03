package com.abrigo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Doacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_doacao;
    private String descricao;
    private float valor;
    private String tipo_doacao;
    private Date data_doacao;
    private String nome_doador;
}

public int getId_doacao() {
        return id_doacao;
    }

    public void setId_doacao(int id_doacao) {
        this.id_doacao = id_doacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getTipo_doacao() {
        return tipo_doacao;
    }

    public void setTipo_doacao(String tipo_doacao) {
        this.tipo_doacao = tipo_doacao;
    }

    public Date getData_doacao() {
        return data_doacao;
    }

    public void setData_doacao(Date data_doacao) {
        this.data_doacao = data_doacao;
    }

    public String getNome_doador() {
        return nome_doador;
    }

    public void setNome_doador(String nome_doador) {
        this.nome_doador = nome_doador;
    }
}