package com.br.projetointegrador.pi.dto.contato;

import com.br.projetointegrador.pi.dto.usuario.UsuarioResponse;
import com.br.projetointegrador.pi.model.Contato;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContatoResponse(
    @NotNull (message = "O id do contato não pode ser nulo!")
    Long idContato,
    UsuarioResponse usuario, 
    @NotBlank (message = "O contato não pode estar vazio!")
    String contato
) {
    public ContatoResponse(Contato contato) {
        this(contato.getIdContato(),
        contato.getIdUsuario() != null ? new UsuarioResponse(contato.getIdUsuario()) : null,
        contato.getContato());
    }
}
