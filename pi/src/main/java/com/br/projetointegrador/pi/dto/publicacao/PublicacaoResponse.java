package com.br.projetointegrador.pi.dto.publicacao;

import java.sql.Date;
import java.time.LocalDateTime;

import com.br.projetointegrador.pi.dto.usuario.UsuarioResponse;
import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;
import com.br.projetointegrador.pi.model.Local;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PublicacaoResponse(
    @NotNull (message = "idPublicacao não pode ser nulo!")
    @NotEmpty (message = "idPublicacao não pode ser vazio!")
    Long idPublicacao,

    @NotNull (message = "idUsuario não pode ser nulo!")
    @NotEmpty (message = "idUsuario não pode ser vazio!")
    UsuarioResponse Usuario,

    @NotNull (message = "idLocal não pode ser nulo!")
    @NotEmpty (message = "idLocal não pode ser vazio!")
    Local local,

    @NotBlank (message = "Nome não pode ser vazio")
    String nome,

    @NotBlank (message = "Descrição não pode ser vazio")
    String descricao,

    @NotNull (message = "dataCriacao não pode ser nulo!")
    @NotEmpty (message = "dataCriacao não pode ser vazio!")
    LocalDateTime dataCriacao,

    @NotNull (message = "dataOcorrencia não pode ser nulo!")
    @NotEmpty (message = "dataOcorrencia não pode ser vazio!")
    Date dataOcorrencia,

    @NotBlank (message = "Status não pode ser vazio")
    StatusPublicacao status,
    
    @NotBlank (message = "Nome não pode ser vazio")
    TipoPublicacao tipo
) {}
