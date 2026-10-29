package com.abrigo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.dto.AdocaoDefinitivaDTO;
import com.abrigo.dto.AdocaoLarTemporarioDTO;
import com.abrigo.model.Adocao;
import com.abrigo.model.Animal;
import com.abrigo.model.Auditoria;
import com.abrigo.model.LarTemp;
import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

public class AdocaoRepository {

    private static final String ADOTADO_DEFINITIVO =
            "Adotado Definitivo";

    private static final String ADOTADO_LAR_TEMPORARIO =
            "Adotado Lar Temporario";

    private static final String ATIVA = "ATIVA";

    private static final String MODULO = "Adoções";

    public List<AdocaoDefinitivaDTO> findAdocoesDefinitivas() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<AdocaoDefinitivaDTO> query = em.createQuery("""
                    select new com.abrigo.dto.AdocaoDefinitivaDTO(
                        an.id, an.nome, a.statusAdocao,
                        a.nomeTutor, a.id
                    )
                    from Adocao a
                    join a.animal an
                    where a.statusAdocao = :status
                      and a.statusRegistro = :registro
                    order by a.dataAdocao desc
                    """, AdocaoDefinitivaDTO.class);

            query.setParameter("status", ADOTADO_DEFINITIVO);
            query.setParameter("registro", ATIVA);

            return query.getResultList();

        } finally {
            em.close();
        }
    }

    public List<AdocaoLarTemporarioDTO> findAdocoesLarTemporario() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<AdocaoLarTemporarioDTO> query = em.createQuery("""
                    select new com.abrigo.dto.AdocaoLarTemporarioDTO(
                        an.id, an.nome, a.statusAdocao,
                        lt.id, lt.nome, a.id
                    )
                    from Adocao a
                    join a.animal an
                    join a.larTemp lt
                    where a.statusAdocao = :status
                      and a.statusRegistro = :registro
                    order by a.dataAdocao desc
                    """, AdocaoLarTemporarioDTO.class);

            query.setParameter("status", ADOTADO_LAR_TEMPORARIO);
            query.setParameter("registro", ATIVA);

            return query.getResultList();

        } finally {
            em.close();
        }
    }

    public boolean possuiLarTemporarioAtivo(Long idAnimal) {
        return possuiRegistroAtivo(idAnimal, ADOTADO_LAR_TEMPORARIO);
    }

    public boolean possuiAdocaoDefinitivaAtiva(Long idAnimal) {
        return possuiRegistroAtivo(idAnimal, ADOTADO_DEFINITIVO);
    }

    public boolean possuiAdocaoAtiva(Long idAnimal) {
        return possuiRegistroAtivo(idAnimal, null);
    }

    private boolean possuiRegistroAtivo(Long idAnimal, String status) {
        if (idAnimal == null) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            String jpql = """
                    select count(a)
                    from Adocao a
                    where a.animal.id = :idAnimal
                      and a.statusRegistro = :registro
                    """;

            if (status != null) {
                jpql += " and a.statusAdocao = :status";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                    .setParameter("idAnimal", idAnimal)
                    .setParameter("registro", ATIVA);

            if (status != null) {
                query.setParameter("status", status);
            }

            return query.getSingleResult() > 0;

        } finally {
            em.close();
        }
    }

    public void registrarAdocaoDefinitiva(
            Long idAnimal,
            String nomeTutor,
            String telefoneTutor) {

        if (idAnimal == null) {
            throw new IllegalArgumentException(
                    "Selecione um animal.");
        }

        if (nomeTutor == null || nomeTutor.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o nome do adotante.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Animal animal = em.find(
                    Animal.class,
                    idAnimal,
                    LockModeType.PESSIMISTIC_WRITE);

            if (animal == null || !Boolean.TRUE.equals(animal.getAtivo())) {
                throw new IllegalStateException(
                        "Animal não encontrado ou inativo.");
            }

            List<Adocao> registros = buscarRegistrosAtivos(
                    em, idAnimal);

            for (Adocao registro : registros) {
                if (ADOTADO_DEFINITIVO.equals(
                        registro.getStatusAdocao())) {
                    throw new IllegalStateException(
                            "Este animal já foi adotado definitivamente.");
                }

                if (ADOTADO_LAR_TEMPORARIO.equals(
                        registro.getStatusAdocao())) {
                    throw new IllegalStateException(
                            "Este animal está em um lar temporário e não "
                            + "pode ser adotado definitivamente. "
                            + "Cancele o lar temporário antes de continuar.");
                }

                throw new IllegalStateException(
                        "O animal possui outro registro de adoção "
                        + "ativo. Verifique sua situação antes de continuar.");
            }

            Adocao definitiva = new Adocao(
                    LocalDate.now(),
                    nomeTutor.trim(),
                    false,
                    animal,
                    ADOTADO_DEFINITIVO,
                    null);

            definitiva.setTelefoneTutor(telefoneTutor);

            em.persist(definitiva);
            em.flush();

            registrarAuditoria(
                    em,
                    "CADASTRO",
                    definitiva.getId(),
                    "Adoção definitiva registrada para o animal "
                    + animal.getNome()
                    + ". Adotante: " + nomeTutor.trim() + ".");

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public void registrarAdocaoLarTemporario(
            Long idAnimal,
            Long idLarTemp,
            String nomeTutor) {

        if (idAnimal == null || idLarTemp == null) {
            throw new IllegalArgumentException(
                    "Selecione o animal e o lar temporário.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Animal animal = em.find(
                    Animal.class,
                    idAnimal,
                    LockModeType.PESSIMISTIC_WRITE);

            if (animal == null || !Boolean.TRUE.equals(animal.getAtivo())) {
                throw new IllegalStateException(
                        "Animal não encontrado ou inativo.");
            }

            if (!buscarRegistrosAtivos(em, idAnimal).isEmpty()) {
                throw new IllegalStateException(
                        "Este animal já possui um registro de adoção ativo.");
            }

            LarTemp larTemp = em.find(
                    LarTemp.class,
                    idLarTemp,
                    LockModeType.PESSIMISTIC_WRITE);

            if (larTemp == null) {
                throw new IllegalStateException(
                        "Lar temporário não encontrado.");
            }

            Integer capacidade = larTemp.getCapacidadeMaxima();
            Integer vagas = larTemp.getVagasDisponiveis();

            if (capacidade == null || capacidade <= 0) {
                throw new IllegalStateException(
                        "A capacidade máxima do lar é inválida.");
            }

            if (vagas == null || vagas <= 0) {
                throw new IllegalStateException(
                        "Este lar temporário não tem vagas disponíveis.");
            }

            if (vagas > capacidade) {
                throw new IllegalStateException(
                        "As vagas cadastradas excedem a capacidade máxima. "
                        + "Corrija o cadastro do lar.");
            }

            if (Boolean.TRUE.equals(animal.getPossuiDeficiencia())
                    && !Boolean.TRUE.equals(
                            larTemp.getAceitaDeficiencia())) {
                throw new IllegalStateException(
                        "Este lar temporário não aceita animais "
                        + "com deficiência.");
            }

            larTemp.setVagasDisponiveis(vagas - 1);

            String nomeResponsavel = nomeTutor;

            if (nomeResponsavel == null || nomeResponsavel.isBlank()) {
                nomeResponsavel = larTemp.getNome();
            }

            Adocao adocao = new Adocao(
                    LocalDate.now(),
                    nomeResponsavel.trim(),
                    false,
                    animal,
                    ADOTADO_LAR_TEMPORARIO,
                    larTemp);

            em.persist(adocao);
            em.flush();

            registrarAuditoria(
                    em,
                    "CADASTRO",
                    adocao.getId(),
                    "Animal " + animal.getNome()
                    + " encaminhado para o lar temporário "
                    + larTemp.getNome() + ".");

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    private List<Adocao> buscarRegistrosAtivos(
            EntityManager em,
            Long idAnimal) {

        return em.createQuery("""
                select ad
                from Adocao ad
                where ad.animal.id = :idAnimal
                  and ad.statusRegistro = :registro
                order by ad.dataAdocao desc
                """, Adocao.class)
                .setParameter("idAnimal", idAnimal)
                .setParameter("registro", ATIVA)
                .getResultList();
    }

    private void liberarVaga(EntityManager em, LarTemp larTemp) {
        if (larTemp == null) {
            return;
        }

        LarTemp larBloqueado = em.find(
                LarTemp.class,
                larTemp.getId(),
                LockModeType.PESSIMISTIC_WRITE);

        if (larBloqueado == null) {
            return;
        }

        Integer vagas = larBloqueado.getVagasDisponiveis();
        Integer capacidade = larBloqueado.getCapacidadeMaxima();

        if (capacidade == null || capacidade < 0) {
            throw new IllegalStateException(
                    "A capacidade do lar temporário é inválida.");
        }

        int vagasAtuais = vagas == null ? 0 : vagas;

        if (vagasAtuais < capacidade) {
            larBloqueado.setVagasDisponiveis(vagasAtuais + 1);
        }
    }

    public void cancelarAdocao(Long idAdocao) {
        if (idAdocao == null) {
            throw new IllegalArgumentException(
                    "Identificador da adoção inválido.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Adocao adocao = em.find(
                    Adocao.class,
                    idAdocao,
                    LockModeType.PESSIMISTIC_WRITE);

            if (adocao == null) {
                throw new IllegalStateException(
                        "Registro de adoção não encontrado.");
            }

            String operacao;
            String descricao;
            String nomeAnimal = adocao.getAnimal() != null
                    ? adocao.getAnimal().getNome()
                    : "Animal não identificado";

            if (ADOTADO_LAR_TEMPORARIO.equals(
                    adocao.getStatusAdocao())) {

                liberarVaga(em, adocao.getLarTemp());

                operacao = "ENCERRAMENTO";
                String nomeLar = adocao.getLarTemp() != null
                        ? adocao.getLarTemp().getNome()
                        : "Lar temporário não identificado";

                descricao = "Encerrado o lar temporário do animal "
                        + nomeAnimal + ". Lar: " + nomeLar + ".";

            } else if (ADOTADO_DEFINITIVO.equals(
                    adocao.getStatusAdocao())) {

                operacao = "CANCELAMENTO";
                descricao = "Cancelada a adoção definitiva do animal "
                        + nomeAnimal
                        + ". Adotante: " + adocao.getNomeTutor() + ".";

            } else {
                throw new IllegalStateException(
                        "Tipo de registro de adoção não reconhecido.");
            }

            em.remove(adocao);

            registrarAuditoria(
                    em,
                    operacao,
                    idAdocao,
                    descricao);

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

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
