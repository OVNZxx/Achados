package com.br.projetointegrador.pi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.projetointegrador.pi.dto.usuario.UsuarioResponse;
import com.br.projetointegrador.pi.service.UsuarioService;

@RestController 
@RequestMapping ("/usuario")
public class UsuarioController {
    
    @Autowired 
    private UsuarioService usuarioService;
    
    @GetMapping ("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @GetMapping ("/me")
    public UsuarioResponse eu() {
        return usuarioService.eu();
    }
}
