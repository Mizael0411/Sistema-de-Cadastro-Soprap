package com.abrigo.dao;

import java.util.List;

import com.abrigo.model.Auditoria;
import com.abrigo.model.Produto;
import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ProdutoDAO {

    private static EntityManagerFactory emf;

    private static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("soprap-pu");
        }
        return emf;
    }

    public void salvar(Produto produto) {
        EntityManager em = getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(produto);
            em.flush();

            registrarAuditoria(
                    em,
                    "CADASTRO",
                    produto.getId(),
                    "Produto cadastrado: " + produto.getNome()
            );

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

    public void atualizar(Produto produto) {
        EntityManager em = getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Produto anterior = em.find(Produto.class, produto.getId());

            if (anterior == null) {
                throw new IllegalArgumentException(
                        "Produto não encontrado para atualização."
                );
            }

            String descricao = "Produto atualizado: " + anterior.getNome()
                    + " -> " + produto.getNome()
                    + "; preço: " + anterior.getPrecoCompra()
                    + " -> " + produto.getPrecoCompra()
                    + "; data da compra: " + anterior.getDataCompra()
                    + " -> " + produto.getDataCompra()
                    + "; validade: " + anterior.getDataValidade()
                    + " -> " + produto.getDataValidade();

            em.merge(produto);

            registrarAuditoria(
                    em,
                    "EDICAO",
                    produto.getId(),
                    descricao
            );

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

    public void deletar(Long id) {
        EntityManager em = getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Produto produto = em.find(Produto.class, id);

            if (produto != null) {
                String descricao = "Produto excluído: " + produto.getNome()
                        + "; preço: " + produto.getPrecoCompra()
                        + "; data da compra: " + produto.getDataCompra()
                        + "; validade: " + produto.getDataValidade();

                em.remove(produto);

                registrarAuditoria(
                        em,
                        "EXCLUSAO",
                        id,
                        descricao
                );
            }

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

    public List<Produto> listarTodos() {
        EntityManager em = getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Produto p",
                    Produto.class
            ).getResultList();

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
            throw new SecurityException(
                    "É necessário estar autenticado para registrar alterações."
            );
        }

        Auditoria auditoria = new Auditoria(
                usuario.getId(),
                usuario.getNome(),
                operacao,
                "Produtos",
                registroId,
                descricao,
                java.time.LocalDateTime.now()
        );

        em.persist(auditoria);
    }
}