package com.abrigo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lar_temporario")
public class LarTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lar")
    private Long id;

    @Column(name = "nome_tutor", nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "capacidade_maxima", nullable = false)
    private Integer capacidadeMaxima;

    @Column(name = "vagas_disponiveis", nullable = false)
    private Integer vagasDisponiveis;

    @Column(name = "aceita_doencas_transmissiveis", nullable = false)
    private Boolean aceitaDoencasTransmissiveis;

    @Column(name = "aceita_deficiencia", nullable = false)
    private Boolean aceitaDeficiencia;

    public LarTemp() {
    }

    public LarTemp(String nome, String endereco, String telefone, Integer capacidadeMaxima, Integer vagasDisponiveis, Boolean aceitaDoencasTransmissiveis, Boolean aceitaDeficiencia) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.capacidadeMaxima = capacidadeMaxima;
        this.vagasDisponiveis = vagasDisponiveis;
        this.aceitaDoencasTransmissiveis = aceitaDoencasTransmissiveis;
        this.aceitaDeficiencia = aceitaDeficiencia;
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(Integer capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(Integer vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public Boolean getAceitaDoencasTransmissiveis() {
        return aceitaDoencasTransmissiveis;
    }

    public void setAceitaDoencasTransmissiveis(Boolean aceitaDoencasTransmissiveis) {
        this.aceitaDoencasTransmissiveis = aceitaDoencasTransmissiveis;
    }

    public Boolean getAceitaDeficiencia() {
        return aceitaDeficiencia;
    }

    public void setAceitaDeficiencia(Boolean aceitaDeficiencia) {
        this.aceitaDeficiencia = aceitaDeficiencia;
    }
}