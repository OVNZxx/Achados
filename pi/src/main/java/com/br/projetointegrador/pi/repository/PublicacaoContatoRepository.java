package com.br.projetointegrador.pi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.projetointegrador.pi.model.PublicacaoContato;
import com.br.projetointegrador.pi.model.PublicacaoContatoId;

public interface PublicacaoContatoRepository extends JpaRepository<PublicacaoContato, PublicacaoContatoId> {

    @EntityGraph(attributePaths = "contato")
    List<PublicacaoContato> findByPublicacao_IdPublicacaoOrderByContato_IdContato(Long idPublicacao);

    Optional<PublicacaoContato> findByPublicacao_IdPublicacaoAndContato_IdContato(Long idPublicacao, Long idContato);

    List<PublicacaoContato> findByContato_IdContato(Long idContato);

    long countByPublicacao_IdPublicacao(Long idPublicacao);

    void deleteAllByContato_IdContato(Long idContato);
}
