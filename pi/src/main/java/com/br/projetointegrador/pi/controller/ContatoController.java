package com.br.projetointegrador.pi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.projetointegrador.pi.dto.contato.ContatoRequest;
import com.br.projetointegrador.pi.dto.contato.ContatoResponse;
import com.br.projetointegrador.pi.service.ContatoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/usuario/contato")
public class ContatoController {

    @Autowired 
    private ContatoService contatoService;

    @PostMapping 
    public ResponseEntity<ContatoResponse> cadastrar(@Valid @RequestBody ContatoRequest contatoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contatoService.cadastrar(contatoRequest));
    }

    @GetMapping ("/listarTodos")
    public List<ContatoResponse> listarTodosPorUsuario() {
        return contatoService.listarTodosPorUsuario();
    }

    @GetMapping ("/{id}")
    public ContatoResponse buscarPorId(@PathVariable Long id) {
        return contatoService.buscarPorId(id);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @PutMapping ("/{id}")
    public ContatoResponse editar(@PathVariable Long id, @RequestBody ContatoRequest contatoRequest) {
        return contatoService.editar(id, contatoRequest);
    }
}
