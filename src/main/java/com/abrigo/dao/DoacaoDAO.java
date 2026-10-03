package com.abrigo.dao;

import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Doacao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class DoacaoDAO {
    
    public void salvar(Doacao doacao) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (doacao.getId() == null) {
                em.persist(doacao);
            } else {
                em.merge(doacao);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao salvar doação: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // CORREÇÃO 1: Método listarTodos() que o HistoricoDoacaoController chama
    public List<Doacao> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT d FROM Doacao d ORDER BY d.dataDoacao DESC";
            return em.createQuery(jpql, Doacao.class).getResultList();
        } finally {
            em.close();
        }
    } 

    // Mantido para compatibilidade se usar o feminino em outro lugar
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
    
    // CORREÇÃO 2: Método deletar() que o HistoricoDoacaoController chama
    public void deletar(Long id) {
        excluir(id);
    }

    public void excluir(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Doacao doacao = em.find(Doacao.class, id);
            if (doacao != null) {
                em.remove(doacao);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao excluir doação: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}