package com.br.projetointegrador.pi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.projetointegrador.pi.model.Contato;
import com.br.projetointegrador.pi.model.Usuario;

public interface ContatoRepository extends JpaRepository<Contato, Long> {
    
    List<Contato> findByidUsuario(Usuario idUsuario);
}
