package com.br.projetointegrador.pi.dto.contato;

import jakarta.validation.constraints.NotBlank;

public record ContatoRequest(
    @NotBlank (message = "O contato não pode estar vazio!")
    String contato) {
}
