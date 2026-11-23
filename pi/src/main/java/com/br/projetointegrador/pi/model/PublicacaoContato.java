package com.br.projetointegrador.pi.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "publicacao_contato")
@Getter
@Setter
@NoArgsConstructor
public class PublicacaoContato {

    @EmbeddedId
    private PublicacaoContatoId id;

    @MapsId("idPublicacao")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_publicacao", nullable = false)
    private Publicacao publicacao;

    @MapsId("idContato")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_contato", nullable = false)
    private Contato contato;

    public PublicacaoContato(Publicacao publicacao, Contato contato) {
        this.id = new PublicacaoContatoId(publicacao.getIdPublicacao(), contato.getIdContato());
        this.publicacao = publicacao;
        this.contato = contato;
    }
}
