package com.abrigo.dao;

import java.util.ArrayList;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Animal;
import com.abrigo.model.Auditoria;
import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;

import jakarta.persistence.EntityManager;

public class AnimalDAO {

    private static final String MODULO = "Animais";

    public boolean salvar(Animal animal) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(animal);
            em.flush();

            registrarAuditoria(
                    em,
                    "CADASTRO",
                    animal.getId(),
                    "Animal cadastrado: " + animal.getNome()
            );

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Erro ao salvar animal no banco: " + e.getMessage()
            );
            e.printStackTrace();
            return false;

        } finally {
            em.close();
        }
    }

    public List<Animal> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT a FROM Animal a "
                    + "WHERE a.ativo = true ORDER BY a.id ASC",
                    Animal.class
            ).getResultList();

        } catch (Exception e) {
            System.err.println(
                    "Erro ao listar animais do banco: " + e.getMessage()
            );
            return new ArrayList<>();

        } finally {
            em.close();
        }
    }

    public Animal buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Animal.class, id);

        } catch (Exception e) {
            System.err.println(
                    "Erro ao buscar animal por ID: " + e.getMessage()
            );
            return null;

        } finally {
            em.close();
        }
    }

    public boolean atualizar(Animal animal) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Animal existente = em.find(Animal.class, animal.getId());

            if (existente == null) {
                em.getTransaction().rollback();
                return false;
            }

            String nomeAnterior = existente.getNome();

            existente.setNome(animal.getNome());
            existente.setIdade(animal.getIdade());
            existente.setSexo(animal.getSexo());
            existente.setEspecie(animal.getEspecie());
            existente.setPorte(animal.getPorte());
            existente.setPossuiDeficiencia(
                    animal.getPossuiDeficiencia()
            );
            existente.setDescricaoDeficiencia(
                    animal.getDescricaoDeficiencia()
            );
            existente.setStatusVacinacao(
                    animal.getStatusVacinacao()
            );
            existente.setStatusGravidez(
                    animal.getStatusGravidez()
            );
            existente.setDataNascimento(
                    animal.getDataNascimento()
            );
            existente.setDataUltimaVacinacao(
                    animal.getDataUltimaVacinacao()
            );
            existente.setDataUltimaConsulta(
                    animal.getDataUltimaConsulta()
            );

            registrarAuditoria(
                    em,
                    "EDICAO",
                    existente.getId(),
                    "Animal editado: " + nomeAnterior
                    + " → " + existente.getNome()
            );

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Erro ao atualizar animal: " + e.getMessage()
            );
            e.printStackTrace();
            return false;

        } finally {
            em.close();
        }
    }

    public boolean excluir(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Animal animal = em.find(Animal.class, id);

            if (animal == null || Boolean.FALSE.equals(animal.getAtivo())) {
                em.getTransaction().rollback();
                return false;
            }

            String nomeAnimal = animal.getNome();

            animal.setAtivo(false);

            registrarAuditoria(
                    em,
                    "EXCLUSAO",
                    animal.getId(),
                    "Animal desativado: " + nomeAnimal
            );

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Erro ao desativar animal: " + e.getMessage()
            );
            e.printStackTrace();
            return false;

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
                    "Não há usuário autenticado para registrar a auditoria."
            );
        }

        Auditoria auditoria = new Auditoria();

        auditoria.setUsuarioId(usuario.getId());
        auditoria.setNomeUsuario(usuario.getNome());
        auditoria.setOperacao(operacao);
        auditoria.setModulo(MODULO);
        auditoria.setRegistroId(registroId);
        auditoria.setDescricao(descricao);

        auditoria.setDataHora(java.time.LocalDateTime.now());

        em.persist(auditoria);
    }
}
