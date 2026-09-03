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

}
