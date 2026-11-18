package com.br.projetointegrador.pi.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;
import com.br.projetointegrador.pi.model.Publicacao;
import com.br.projetointegrador.pi.model.Usuario;

public interface PublicacaoRepository extends JpaRepository <Publicacao, Long> {
    Page<Publicacao> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @EntityGraph (attributePaths = {"idUsuario", "idLocal"})
    Page<Publicacao> findAll(Pageable pageable); // Para evitar o erro N + 1

    Page<Publicacao> findByIdUsuario(Usuario usuario, Pageable pageable);

    Long countByStatus(StatusPublicacao status);
    
    Long countByTipo(TipoPublicacao tipo);
}
