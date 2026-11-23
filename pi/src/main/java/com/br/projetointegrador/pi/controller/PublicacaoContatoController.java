package com.br.projetointegrador.pi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.projetointegrador.pi.dto.publicacaoContato.PublicacaoContatoRequest;
import com.br.projetointegrador.pi.dto.publicacaoContato.PublicacaoContatoResponse;
import com.br.projetointegrador.pi.service.PublicacaoContatoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/publicacao/{idPublicacao}/contatos")
public class PublicacaoContatoController {

    @Autowired
    private PublicacaoContatoService publicacaoContatoService;

    @GetMapping
    public List<PublicacaoContatoResponse> listar(@PathVariable Long idPublicacao) {
        return publicacaoContatoService.listarPorPublicacao(idPublicacao);
    }

    @PostMapping
    public ResponseEntity<PublicacaoContatoResponse> disponibilizar(
            @PathVariable Long idPublicacao,
            @Valid @RequestBody PublicacaoContatoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publicacaoContatoService.disponibilizarContato(idPublicacao, request));
    }

    @DeleteMapping("/{idContato}")
    public ResponseEntity<Void> remover(@PathVariable Long idPublicacao, @PathVariable Long idContato) {
        publicacaoContatoService.removerContato(idPublicacao, idContato);
        return ResponseEntity.noContent().build();
    }
}
