package com.abrigo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Produtos_Comp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_produto;
    private Date data_compra;    
    private float valor_produto;

    public int getId_produto() {
        return id_produto;
    }

    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public Date getData_compra() {
        return data_compra;
    }

    public void setData_compra(Date data_compra) {
        this.data_compra = data_compra;
    }

    public float getValor_produto() {
        return valor_produto;
    }

    public void setValor_produto(float valor_produto) {
        this.valor_produto = valor_produto;
    }
}
