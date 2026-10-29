package com.abrigo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "nome_usuario", nullable = false, length = 100)
    private String nomeUsuario;

    @Column(name = "operacao", nullable = false, length = 30)
    private String operacao;

    @Column(name = "modulo", nullable = false, length = 50)
    private String modulo;

    @Column(name = "registro_id")
    private Long registroId;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public Auditoria() {
    }

    public Auditoria(
            Long usuarioId,
            String nomeUsuario,
            String operacao,
            String modulo,
            Long registroId,
            String descricao,
            LocalDateTime dataHora) {

        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.operacao = operacao;
        this.modulo = modulo;
        this.registroId = registroId;
        this.descricao = descricao;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Long getRegistroId() {
        return registroId;
    }

    public void setRegistroId(Long registroId) {
        this.registroId = registroId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
