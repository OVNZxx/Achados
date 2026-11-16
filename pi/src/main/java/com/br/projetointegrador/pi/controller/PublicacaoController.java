package com.br.projetointegrador.pi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
