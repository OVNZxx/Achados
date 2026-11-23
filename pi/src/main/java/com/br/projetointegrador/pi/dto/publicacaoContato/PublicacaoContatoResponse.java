package com.br.projetointegrador.pi.dto.publicacaoContato;

import com.br.projetointegrador.pi.model.PublicacaoContato;

public record PublicacaoContatoResponse(
    Long idPublicacao,
    Long idContato,
    String contato
) {
    public PublicacaoContatoResponse(PublicacaoContato publicacaoContato) {
        this(
            publicacaoContato.getPublicacao().getIdPublicacao(),
            publicacaoContato.getContato().getIdContato(),
            publicacaoContato.getContato().getContato()
        );
    }
}
