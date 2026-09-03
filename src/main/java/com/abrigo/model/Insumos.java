package com.abrigo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Insumos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_insumo;
    private Date data_insumos;
    private float valor;
    private String descricao;
}

public int getId_insumo() {
        return id_insumo;
    }

    public void setId_insumo(int id_insumo) {
        this.id_insumo = id_insumo;
    }

    public Date getData_insumos() {
        return data_insumos;
    }

    public void setData_insumos(Date data_insumos) {
        this.data_insumos = data_insumos;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}