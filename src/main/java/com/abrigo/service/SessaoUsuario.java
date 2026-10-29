package com.abrigo.service;

import com.abrigo.model.Usuario;

public final class SessaoUsuario {
    
    private static Usuario usuarioAtual;

    private SessaoUsuario() {

    }

    public static void iniciar(Usuario usuario) {
        usuarioAtual = usuario;
    }

    public static Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    public static boolean isAdministradora() {
        return usuarioAtual != null && "ADMINISTRADORA".equals(usuarioAtual.getPerfil());
    }

    public static void encerrar() {
        usuarioAtual = null;
    }
}
