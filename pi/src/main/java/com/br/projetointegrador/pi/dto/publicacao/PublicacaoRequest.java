package com.br.projetointegrador.pi.dto.publicacao;

import java.sql.Date;
import java.util.List;

import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PublicacaoRequest(
    @NotNull (message = "idLocal não pode ser nulo!")
    Short idLocal,

    @NotBlank (message = "Nome não pode ser vazio")
    String nome,

    @NotBlank (message = "Descrição não pode ser vazio")
    String descricao,

    @NotNull(message = "Data da ocorrência não pode ser nula!")
    Date dataOcorrencia,

    StatusPublicacao status,
    TipoPublicacao tipo,

    @NotEmpty(message = "Informe ao menos um contato para a publicação")
    List<@NotNull Long> idsContatos
) {}
