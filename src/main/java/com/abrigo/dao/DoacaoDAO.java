package com.abrigo.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Auditoria;
import com.abrigo.model.Doacao;
import com.abrigo.model.ItemDoacao;
import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class DoacaoDAO {

    private static final String MODULO = "Doações";

    public void salvar(Doacao doacao) {
        executarSalvamento(doacao, null);
    }

    public void salvar(Doacao doacao, List<ItemDoacao> itens) {
        List<ItemDoacao> copiaItens = itens == null
                ? new ArrayList<>()
                : new ArrayList<>(itens);

        executarSalvamento(doacao, copiaItens);
    }

    private void executarSalvamento(
            Doacao doacao,
            List<ItemDoacao> itens) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            boolean novaDoacao = doacao.getId() == null;
            Doacao doacaoSalva;

            if (novaDoacao) {
                em.persist(doacao);
                em.flush();
                doacaoSalva = doacao;
            } else {
                doacaoSalva = em.find(Doacao.class, doacao.getId());

                if (doacaoSalva == null) {
                    throw new IllegalArgumentException(
                            "Doação não encontrada para edição: "
                                    + doacao.getId());
                }

                doacaoSalva = em.merge(doacao);
                em.flush();
            }

            if (itens != null) {

                Long doacaoId = doacaoSalva.getId();

                em.createQuery(
                    "DELETE FROM ItemDoacao i "
                    + "WHERE i.doacao.id = :doacaoId"
                )
                .setParameter("doacaoId", doacaoId)
                .executeUpdate();

                if ("MATERIAL".equalsIgnoreCase(
                        doacaoSalva.getTipoDoacao())) {

                    for (ItemDoacao item : itens) {
                        item.setId(null);
                        item.setDoacao(doacaoSalva);
                        em.persist(item);
                    }
                }
            }

            String operacao = novaDoacao ? "CADASTRO" : "EDICAO";
            String descricao = novaDoacao
                    ? "Doação cadastrada. Tipo: "
                            + doacaoSalva.getTipoDoacao()
                    : "Doação editada. Tipo: "
                            + doacaoSalva.getTipoDoacao();

            registrarAuditoria(
                    em,
                    operacao,
                    doacaoSalva.getId(),
                    descricao
            );

            em.flush();
            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException(
                    "Erro ao salvar doação: " + e.getMessage(),
                    e
            );

        } finally {
            em.close();
        }
    }

    public List<Doacao> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            String jpql =
                    "SELECT d FROM Doacao d "
                    + "ORDER BY d.dataDoacao DESC";

            return em.createQuery(jpql, Doacao.class)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    public List<Doacao> listarTodas() {
        return listarTodos();
    }

    public Doacao buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Doacao.class, id);

        } finally {
            em.close();
        }
    }

    public void deletar(Long id) {
        excluir(id);
    }

    public void excluir(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Doacao doacao = em.find(Doacao.class, id);

            if (doacao == null) {
                throw new IllegalArgumentException(
                        "Doação não encontrada para exclusão: " + id);
            }

            Long idDoacao = doacao.getId();
            String tipoDoacao = doacao.getTipoDoacao();

            registrarAuditoria(
                    em,
                    "EXCLUSAO",
                    idDoacao,
                    "Doação excluída. Tipo: " + tipoDoacao
            );

            em.remove(doacao);
            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException(
                    "Erro ao excluir doação: " + e.getMessage(),
                    e
            );

        } finally {
            em.close();
        }
    }

    public List<ItemDoacao> listarItensPorDoacao(Long doacaoId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            String jpql =
                    "SELECT i FROM ItemDoacao i "
                    + "WHERE i.doacao.id = :doacaoId "
                    + "ORDER BY i.id";

            return em.createQuery(jpql, ItemDoacao.class)
                    .setParameter("doacaoId", doacaoId)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    private void registrarAuditoria(
            EntityManager em,
            String operacao,
            Long registroId,
            String descricao) {

        Usuario usuario = SessaoUsuario.getUsuarioAtual();

        if (usuario == null) {
            throw new IllegalStateException(
                    "Não há usuário autenticado para registrar a auditoria.");
        }

        Auditoria auditoria = new Auditoria();
        auditoria.setUsuarioId(usuario.getId());
        auditoria.setNomeUsuario(usuario.getNome());
        auditoria.setOperacao(operacao);
        auditoria.setModulo(MODULO);
        auditoria.setRegistroId(registroId);
        auditoria.setDescricao(descricao);
        auditoria.setDataHora(LocalDateTime.now());

        em.persist(auditoria);
    }
}
