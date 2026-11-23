package com.br.projetointegrador.pi.dto.publicacao;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import com.br.projetointegrador.pi.dto.local.LocalResponse;
import com.br.projetointegrador.pi.dto.publicacaoContato.PublicacaoContatoResponse;
import com.br.projetointegrador.pi.dto.usuario.UsuarioResponse;
import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;
import com.br.projetointegrador.pi.model.Publicacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicacaoResponse(
    @NotNull (message = "idPublicacao não pode ser nulo!")
    Long idPublicacao,

    @NotNull (message = "idUsuario não pode ser nulo!")
    UsuarioResponse usuario,

    @NotNull (message = "idLocal não pode ser nulo!")
    LocalResponse local,

    @NotBlank (message = "Nome não pode ser vazio")
    String nome,

    @NotBlank (message = "Descrição não pode ser vazio")
    String descricao,

    @NotNull (message = "dataCriacao não pode ser nulo!")
    LocalDateTime dataCriacao,

    @NotNull (message = "dataOcorrencia não pode ser nulo!")
    Date dataOcorrencia,

    @NotBlank (message = "Status não pode ser vazio")
    StatusPublicacao status,
    
    @NotBlank (message = "Nome não pode ser vazio")
    TipoPublicacao tipo,

    List<PublicacaoContatoResponse> contatos
) {

    public PublicacaoResponse(Publicacao publicacao) {
        this(publicacao.getIdPublicacao(),
        publicacao.getIdUsuario() != null ? new UsuarioResponse(publicacao.getIdUsuario()) : null,
        publicacao.getIdLocal() != null ? new LocalResponse(publicacao.getIdLocal()) : null, publicacao.getNome(),
        publicacao.getDescricao(), publicacao.getDataCriacao(), publicacao.getDataOcorrencia(), publicacao.getStatus(),
        publicacao.getTipo(), publicacao.getContatosDisponibilizados().stream().map(PublicacaoContatoResponse::new).toList());
    }
}
