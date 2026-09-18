package com.abrigo.dto;

// Tela 3: Animais adotados em lar temporário
// Campos: id_animal, nome, statusAdocao ("Adotado Lar Temporario"), idLarTemp, nomeTutor, idAdocao
public record AdocaoLarTemporarioDTO(
        Long idAnimal,
        String nomeAnimal,
        String statusAdocao,
        Long idLarTemp,
        String nomeTutor,
        Long idAdocao
) {}
