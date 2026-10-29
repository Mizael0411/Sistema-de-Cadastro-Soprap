package com.abrigo.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Animal;
import com.abrigo.model.Auditoria;
import com.abrigo.model.Usuario;
import com.abrigo.model.Vacina;
import com.abrigo.service.SessaoUsuario;

import jakarta.persistence.EntityManager;

public class VacinaDAO {

    private static final String MODULO = "Vacinação";

    public boolean salvar(Vacina vacina) {
        if (vacina == null
                || vacina.getAnimal() == null
                || vacina.getAnimal().getId() == null) {
            System.err.println("Vacina ou animal não informado.");
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        Long idAnimal = vacina.getAnimal().getId();

        try {
            em.getTransaction().begin();

            Animal animal = em.find(Animal.class, idAnimal);

            if (animal == null || !Boolean.TRUE.equals(animal.getAtivo())) {
                em.getTransaction().rollback();
                System.err.println(
                        "Não é possível registrar vacina para animal inativo."
                );
                return false;
            }

            vacina.setAnimal(animal);
            em.persist(vacina);
            em.flush();

            registrarAuditoria(
                    em,
                    "CADASTRO",
                    vacina.getId(),
                    "Vacina cadastrada: "
                            + vacina.getTipoVacina()
                            + " para o animal ID " + idAnimal
            );

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println("Erro ao salvar vacina: " + e.getMessage());
            return false;

        } finally {
            em.close();
        }

        return atualizarStatusVacinacaoAnimal(idAnimal);
    }

    public boolean atualizar(Vacina vacina) {
        if (vacina == null
                || vacina.getId() == null
                || vacina.getAnimal() == null
                || vacina.getAnimal().getId() == null) {
            System.err.println("Dados insuficientes para atualizar a vacina.");
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        Long idAnimalOriginal = null;
        Long idNovoAnimal = vacina.getAnimal().getId();

        try {
            em.getTransaction().begin();

            Vacina existente = em.find(Vacina.class, vacina.getId());

            if (existente == null) {
                em.getTransaction().rollback();
                System.err.println("Vacina não encontrada para atualização.");
                return false;
            }

            Animal animalOriginal = existente.getAnimal();

            if (animalOriginal == null
                    || !Boolean.TRUE.equals(animalOriginal.getAtivo())) {
                em.getTransaction().rollback();
                System.err.println(
                        "Não é possível alterar vacina de animal desativado."
                );
                return false;
            }

            idAnimalOriginal = animalOriginal.getId();

            Animal novoAnimal = em.find(Animal.class, idNovoAnimal);

            if (novoAnimal == null
                    || !Boolean.TRUE.equals(novoAnimal.getAtivo())) {
                em.getTransaction().rollback();
                System.err.println(
                        "Não é possível associar vacina a animal inativo."
                );
                return false;
            }

            existente.setAnimal(novoAnimal);
            existente.setTipoVacina(vacina.getTipoVacina());
            existente.setDose(vacina.getDose());
            existente.setDataVacinacao(vacina.getDataVacinacao());
            existente.setStatusVacinacao(vacina.getStatusVacinacao());
            existente.setDataProximaVacinacao(
                    vacina.getDataProximaVacinacao()
            );

            registrarAuditoria(
                    em,
                    "EDICAO",
                    existente.getId(),
                    "Vacina editada: "
                            + existente.getTipoVacina()
                            + "; animal anterior ID " + idAnimalOriginal
                            + "; novo animal ID " + idNovoAnimal
            );

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println("Erro ao atualizar vacina: " + e.getMessage());
            return false;

        } finally {
            em.close();
        }

        boolean statusOriginalAtualizado =
                atualizarStatusVacinacaoAnimal(idAnimalOriginal);

        if (idAnimalOriginal.equals(idNovoAnimal)) {
            return statusOriginalAtualizado;
        }

        boolean statusNovoAtualizado =
                atualizarStatusVacinacaoAnimal(idNovoAnimal);

        return statusOriginalAtualizado && statusNovoAtualizado;
    }

    public boolean excluir(Long vacinaId) {
        if (vacinaId == null) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        Long idAnimal;

        try {
            em.getTransaction().begin();

            Vacina vacina = em.find(Vacina.class, vacinaId);

            if (vacina == null) {
                em.getTransaction().rollback();
                System.err.println("Vacina não encontrada para exclusão.");
                return false;
            }

            Animal animal = vacina.getAnimal();

            if (animal == null
                    || !Boolean.TRUE.equals(animal.getAtivo())) {
                em.getTransaction().rollback();
                System.err.println(
                        "Não é possível excluir vacina de animal desativado."
                );
                return false;
            }

            idAnimal = animal.getId();

            registrarAuditoria(
                    em,
                    "EXCLUSAO",
                    vacina.getId(),
                    "Vacina excluída: "
                            + vacina.getTipoVacina()
                            + " do animal ID " + idAnimal
            );

            em.remove(vacina);
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println("Erro ao excluir vacina: " + e.getMessage());
            return false;

        } finally {
            em.close();
        }

        return atualizarStatusVacinacaoAnimal(idAnimal);
    }

    public Vacina buscarPorId(Long vacinaId) {
        if (vacinaId == null) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Vacina.class, vacinaId);

        } catch (Exception e) {
            System.err.println(
                    "Erro ao buscar vacina por ID: " + e.getMessage()
            );
            return null;

        } finally {
            em.close();
        }
    }

    public List<Vacina> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT v FROM Vacina v "
                    + "ORDER BY v.dataVacinacao DESC",
                    Vacina.class
            ).getResultList();

        } catch (Exception e) {
            System.err.println(
                    "Erro ao listar todas as vacinas: " + e.getMessage()
            );
            return new ArrayList<>();

        } finally {
            em.close();
        }
    }

    public List<Vacina> buscarPorAnimalId(Object animalId) {
        if (animalId == null) {
            return Collections.emptyList();
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT v FROM Vacina v "
                    + "WHERE v.animal.id = :animalId "
                    + "ORDER BY v.dataVacinacao DESC",
                    Vacina.class
            )
            .setParameter("animalId", animalId)
            .getResultList();

        } catch (Exception e) {
            System.err.println(
                    "Erro ao buscar histórico de vacina do animal: "
                    + e.getMessage()
            );
            return new ArrayList<>();

        } finally {
            em.close();
        }
    }

    public List<Vacina> listarPorAnimal(Long idAnimal) {
        if (idAnimal == null) {
            return Collections.emptyList();
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT v FROM Vacina v "
                    + "WHERE v.animal.id = :idAnimal "
                    + "ORDER BY v.dataVacinacao DESC",
                    Vacina.class
            )
            .setParameter("idAnimal", idAnimal)
            .getResultList();

        } catch (Exception e) {
            System.err.println(
                    "Erro ao listar vacinas por animal: " + e.getMessage()
            );
            return Collections.emptyList();

        } finally {
            em.close();
        }
    }

    public boolean atualizarStatusVacinacaoAnimal(Long idAnimal) {
        if (idAnimal == null) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Animal animal = em.find(Animal.class, idAnimal);

            if (animal == null) {
                em.getTransaction().rollback();
                return false;
            }

            List<Vacina> vacinas = em.createQuery(
                    "SELECT v FROM Vacina v "
                    + "WHERE v.animal.id = :idAnimal "
                    + "AND v.statusVacinacao = :status "
                    + "ORDER BY v.dataVacinacao DESC",
                    Vacina.class
            )
            .setParameter("idAnimal", idAnimal)
            .setParameter("status", "Concluída")
            .getResultList();

            boolean temRaiva = false;
            boolean temVirose = false;
            LocalDate ultimaData = null;

            for (Vacina vacina : vacinas) {
                String tipo = vacina.getTipoVacina();

                if (tipo == null) {
                    continue;
                }

                if (tipo.equalsIgnoreCase("Raiva")
                        || tipo.equalsIgnoreCase("Raiva e Virose")) {
                    temRaiva = true;
                }

                if (tipo.equalsIgnoreCase("Virose")
                        || tipo.equalsIgnoreCase("Raiva e Virose")) {
                    temVirose = true;
                }

                if (vacina.getDataVacinacao() != null
                        && (ultimaData == null
                        || vacina.getDataVacinacao().isAfter(ultimaData))) {
                    ultimaData = vacina.getDataVacinacao();
                }
            }

            String status;

            if (temRaiva && temVirose) {
                status = "Vacinado";
            } else if (temRaiva || temVirose) {
                status = "Parcialmente vacinado";
            } else {
                status = "Pendente de vacinação";
            }

            animal.setStatusVacinacao(status);
            animal.setDataUltimaVacinacao(ultimaData);

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Erro ao atualizar status de vacinação: "
                    + e.getMessage()
            );
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
        auditoria.setDataHora(LocalDateTime.now());

        em.persist(auditoria);
    }
}
