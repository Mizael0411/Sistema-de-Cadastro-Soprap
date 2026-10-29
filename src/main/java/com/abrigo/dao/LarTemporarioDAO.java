package com.abrigo.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Auditoria;
import com.abrigo.model.LarTemp;
import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;

import jakarta.persistence.EntityManager;

public class LarTemporarioDAO {

    private static final String MODULO = "Lares Temporários";

    public boolean salvar(LarTemp lar) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(lar);
            em.flush();

            registrarAuditoria(
                    em,
                    "CADASTRO",
                    lar.getId(),
                    "Lar temporário cadastrado: " + lar.getNome()
            );

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Erro ao salvar lar temporário: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            em.close();
        }
    }

    public List<LarTemp> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT l FROM LarTemp l ORDER BY l.id ASC",
                    LarTemp.class
            ).getResultList();

        } catch (Exception e) {
            System.err.println(
                    "Erro ao listar lares temporários: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();

        } finally {
            em.close();
        }
    }

    public LarTemp buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(LarTemp.class, id);

        } catch (Exception e) {
            System.err.println(
                    "Erro ao buscar lar temporário: " + e.getMessage());
            e.printStackTrace();
            return null;

        } finally {
            em.close();
        }
    }

    public boolean atualizar(LarTemp lar) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            LarTemp existente = em.find(LarTemp.class, lar.getId());

            if (existente == null) {
                em.getTransaction().rollback();

                System.err.println(
                        "Lar temporário não encontrado para edição: id="
                                + lar.getId());

                return false;
            }

            String nomeAnterior = existente.getNome();
            Long id = existente.getId();

            existente.setNome(lar.getNome());
            existente.setEndereco(lar.getEndereco());
            existente.setTelefone(lar.getTelefone());
            existente.setCapacidadeMaxima(lar.getCapacidadeMaxima());
            existente.setVagasDisponiveis(lar.getVagasDisponiveis());
            existente.setAceitaDoencasTransmissiveis(
                    lar.getAceitaDoencasTransmissiveis());
            existente.setAceitaDeficiencia(
                    lar.getAceitaDeficiencia());

            registrarAuditoria(
                    em,
                    "EDICAO",
                    id,
                    "Lar temporário editado: "
                            + nomeAnterior + " → " + existente.getNome()
            );

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Erro ao atualizar lar temporário: " + e.getMessage());
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

            LarTemp lar = em.find(LarTemp.class, id);

            if (lar == null) {
                em.getTransaction().rollback();

                System.err.println(
                        "Lar temporário não encontrado para exclusão: id="
                                + id);

                return false;
            }

            String nomeLar = lar.getNome();
            Long idLar = lar.getId();

            registrarAuditoria(
                    em,
                    "EXCLUSAO",
                    idLar,
                    "Lar temporário excluído: " + nomeLar
            );

            em.remove(lar);

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Erro ao excluir lar temporário: " + e.getMessage());
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
