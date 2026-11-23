package com.br.projetointegrador.pi.dto.publicacaoContato;

import jakarta.validation.constraints.NotNull;

public record PublicacaoContatoRequest(
    @NotNull(message = "idContato não pode ser nulo")
    Long idContato
) {}
