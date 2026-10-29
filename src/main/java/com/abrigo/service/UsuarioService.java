package com.abrigo.service;

import java.util.List;
import java.util.Optional;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Usuario;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.EntityManager;

public class UsuarioService {

    private void verificarPermissaoAdministrativa() {
        if (!SessaoUsuario.isAdministradora()) {
            throw new SecurityException(
                "Somente a administradora pode executar esta operação."
            );
        }
    }

    public List<Usuario> listarUsuariosAtivos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                "SELECT u FROM Usuario u WHERE u.ativo = true ORDER BY u.nome",
                Usuario.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Usuario> autenticar(
            String nomeUsuario, String senha) {

        if (nomeUsuario == null || nomeUsuario.isBlank()
                || senha == null || senha.isEmpty()) {
            return Optional.empty();
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            List<Usuario> resultados = em.createQuery(
                "SELECT u FROM Usuario u "
                + "WHERE u.nomeUsuario = :nomeUsuario AND u.ativo = true",
                Usuario.class
            )
            .setParameter("nomeUsuario", nomeUsuario.trim())
            .setMaxResults(1)
            .getResultList();

            if (resultados.isEmpty()) {
                return Optional.empty();
            }

            Usuario usuario = resultados.get(0);

            BCrypt.Result resultado = BCrypt.verifyer().verify(
                senha.toCharArray(),
                usuario.getSenhaHash()
            );

            return resultado.verified
                ? Optional.of(usuario)
                : Optional.empty();

        } finally {
            em.close();
        }
    }

    public List<Usuario> listarTodos() {
        verificarPermissaoAdministrativa();

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                "SELECT u FROM Usuario u ORDER BY u.nome",
                Usuario.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void cadastrarFuncionario(
            String nome, String nomeUsuario, String senha) {

        verificarPermissaoAdministrativa();

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome.");
        }

        if (nomeUsuario == null || nomeUsuario.isBlank()) {
            throw new IllegalArgumentException(
                "Informe o nome de usuário."
            );
        }

        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException(
                "A senha deve ter pelo menos 8 caracteres."
            );
        }

        String nomeLimpo = nome.trim();
        String usuarioLimpo = nomeUsuario.trim();

        EntityManager em = JPAUtil.getEntityManager();

        try {
            Long quantidade = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u "
                + "WHERE u.nomeUsuario = :nomeUsuario",
                Long.class
            )
            .setParameter("nomeUsuario", usuarioLimpo)
            .getSingleResult();

            if (quantidade > 0) {
                throw new IllegalArgumentException(
                    "Esse nome de usuário já está cadastrado."
                );
            }

            String hash = BCrypt.withDefaults().hashToString(
                12, senha.toCharArray()
            );

            Usuario novo = new Usuario();
            novo.setNome(nomeLimpo);
            novo.setNomeUsuario(usuarioLimpo);
            novo.setSenhaHash(hash);
            novo.setPerfil("FUNCIONARIO");
            novo.setAtivo(true);

            em.getTransaction().begin();
            em.persist(novo);
            em.getTransaction().commit();

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void desativarUsuario(Long id) {
        verificarPermissaoAdministrativa();

        if (id == null) {
            throw new IllegalArgumentException(
                "Usuário inválido."
            );
        }

        if (SessaoUsuario.getUsuarioAtual() != null
                && id.equals(SessaoUsuario.getUsuarioAtual().getId())) {
            throw new IllegalArgumentException(
                "Você não pode desativar a própria conta."
            );
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Usuario usuario = em.find(Usuario.class, id);

            if (usuario == null) {
                throw new IllegalArgumentException(
                    "Usuário não encontrado."
                );
            }

            if ("ADMINISTRADORA".equals(usuario.getPerfil())) {
                throw new IllegalArgumentException(
                    "Esta operação não permite desativar uma conta administradora."
                );
            }

            usuario.setAtivo(false);

            em.getTransaction().commit();

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
