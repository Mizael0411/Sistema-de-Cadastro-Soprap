package com.abrigo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_animal")
    private Long id;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer idade;

    @Column(nullable = false)
    private String sexo;

    @Column(name = "especie")
    private String especie;

    @Column(name = "porte")
    private String porte;

    @Column(name = "possui_deficiencia")
    private Boolean possuiDeficiencia = false;

    @Column(name = "descricao_deficiencia")
    private String descricaoDeficiencia;

    @Column(name = "status_vacinacao", nullable = true)
    private String statusVacinacao;

    @Column(name = "status_gravidez", nullable = true)
    private String statusGravidez;

    @Column(name = "data_nascimento", nullable = true)
    private LocalDate dataNascimento;

    @Column(name = "data_ultima_vacinacao", nullable = true)
    private LocalDate dataUltimaVacinacao;

    @Column(name = "data_ultima_consulta", nullable = true)
    private LocalDate dataUltimaConsulta;

     public Animal() {
        
    }

    @Override
    public String toString() {
        return (nome != null && !nome.isBlank()) ? nome : "Animal #" + id;
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

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getPorte() { 
        return porte; 
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public Boolean getPossuiDeficiencia(){
        return possuiDeficiencia;
    }

    public void setPossuiDeficiencia(Boolean possuiDeficiencia){
        this.possuiDeficiencia = possuiDeficiencia;
    }

    public String getDescricaoDeficiencia(){
        return descricaoDeficiencia;
    }

    public void setDescricaoDeficiencia(String descricaoDeficiencia) {
        this.descricaoDeficiencia = descricaoDeficiencia;
    }

    public String getStatusVacinacao() {
        return statusVacinacao;
    }

    public void setStatusVacinacao(String statusVacinacao) {
        this.statusVacinacao = statusVacinacao;
    }

    public String getStatusGravidez() {
        return statusGravidez;
    }

    public void setStatusGravidez(String statusGravidez) {
        this.statusGravidez = statusGravidez;
    }

    public LocalDate getDataUltimaVacinacao() {
        return dataUltimaVacinacao;
    }

    public void setDataUltimaVacinacao(LocalDate dataUltimaVacinacao) {
        this.dataUltimaVacinacao = dataUltimaVacinacao;
    }

    public LocalDate getDataUltimaConsulta() {
        return dataUltimaConsulta;
    }

    public void setDataUltimaConsulta(LocalDate dataUltimaConsulta) {
        this.dataUltimaConsulta = dataUltimaConsulta;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}