package com.br.projetointegrador.pi.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.projetointegrador.pi.model.Publicacao;

public interface PublicacaoRepository extends JpaRepository <Publicacao, Long> {
    Page<Publicacao> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @EntityGraph (attributePaths = {"idUsuario", "idLocal"})
    Page<Publicacao> findAll(Pageable pageable); // Para evitar o erro N + 1
}
