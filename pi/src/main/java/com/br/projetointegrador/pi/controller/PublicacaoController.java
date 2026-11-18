package com.br.projetointegrador.pi.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.br.projetointegrador.pi.dto.publicacao.PublicacaoRequest;
import com.br.projetointegrador.pi.dto.publicacao.PublicacaoResponse;
import com.br.projetointegrador.pi.service.PublicacaoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/publicacao")
public class PublicacaoController {

    @Autowired 
    private PublicacaoService publicacaoService;

    @PostMapping 
    public ResponseEntity<PublicacaoResponse> cadastrar(@Valid @RequestBody PublicacaoRequest publicacaoRequest) {
        PublicacaoResponse publicacaoResponse = publicacaoService.cadastrar(publicacaoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicacaoResponse);
    }

    @GetMapping ("/{id}")
    public PublicacaoResponse buscarPorId(@PathVariable Long id) {
        return publicacaoService.buscarPorId(id);
    }

    @GetMapping ("/listarTodos")
    public ResponseEntity<Page<PublicacaoResponse>> listarTodos(@PageableDefault (page = 0, size = 10, sort = "idPublicacao") Pageable pageable) {
        Page<PublicacaoResponse> paginaDePublicacoes = publicacaoService.listarTodos(pageable);

        return ResponseEntity.ok(paginaDePublicacoes);
    }

    @GetMapping ("/buscarPorNome")
    public ResponseEntity<Page<PublicacaoResponse>> buscarPorNomeContendo (@RequestParam String nome, @PageableDefault (page = 0, size = 10, sort = "nome") Pageable pageable) {
        Page<PublicacaoResponse> paginaDePublicacoes = publicacaoService.listarTodos(pageable);

        return ResponseEntity.ok(paginaDePublicacoes);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deletarPublicacaoPorId(@PathVariable Long id) {
        publicacaoService.deletarPublicacaoPorId(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping ("/{id}")
    public PublicacaoResponse atualizarPublicacaoPorId(@PathVariable Long id, @Valid @RequestBody PublicacaoRequest publicacaoRequest) {
        PublicacaoResponse publicacaoResponse = publicacaoService.atualizarPublicacaoPorId(id, publicacaoRequest);

        return publicacaoResponse;
    }

    @GetMapping ("/minhas")
    public ResponseEntity<Page<PublicacaoResponse>> listarMinhas(@PageableDefault 
    (page = 0, size = 5, sort = "idPublicacao") Pageable pageable) {
        return ResponseEntity.ok(publicacaoService.listarMinhas(pageable));
    }
}
