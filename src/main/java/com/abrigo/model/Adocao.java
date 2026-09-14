package com.abrigo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adocao")
public class Adocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adocao")
    private Long id;

    @Column(name = "data_adocao", nullable = false)
    private LocalDate dataAdocao;

    public Adocao() {
    }

    public Adocao(LocalDate dataAdocao) {
        this.dataAdocao = dataAdocao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAdocao() {
        return dataAdocao;
    }

    public void setDataAdocao(LocalDate dataAdocao) {
        this.dataAdocao = dataAdocao;
    }
}