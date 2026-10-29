package com.abrigo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Auditoria;
import com.abrigo.model.Usuario;

import jakarta.persistence.EntityManager;

public class AuditoriaService {

    public void registrar(
            String operacao,
            String modulo,
            Long registroId,
            String descricao) {

        Usuario usuario = SessaoUsuario.getUsuarioAtual();

        if (usuario == null) {
            throw new SecurityException(
                    "Não há usuário autenticado para registrar a auditoria."
            );
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(criarRegistro(
                    usuario,
                    operacao,
                    modulo,
                    registroId,
                    descricao
            ));

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public List<Auditoria> listarTodas() {

        if (!SessaoUsuario.isAdministradora()) {
            throw new SecurityException(
                    "Somente a administradora pode consultar a auditoria."
            );
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT a FROM Auditoria a ORDER BY a.dataHora DESC",
                    Auditoria.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    private Auditoria criarRegistro(
            Usuario usuario,
            String operacao,
            String modulo,
            Long registroId,
            String descricao) {

        return new Auditoria(
                usuario.getId(),
                usuario.getNome(),
                operacao,
                modulo,
                registroId,
                descricao,
                LocalDateTime.now()
        );
    }
}