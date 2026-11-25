package com.br.projetointegrador.pi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.projetointegrador.pi.dto.local.LocalResponse;
import com.br.projetointegrador.pi.service.LocalServiceTest;

@RestController 
@RequestMapping ("/local")
public class LocalController {

    @Autowired 
    private LocalServiceTest localService;

    @GetMapping ("/listarTodos")
    public List<LocalResponse> listarTodos() {
        return localService.listarTodos();
    }

    @GetMapping ("/{id}")
    public LocalResponse buscarPorId(@PathVariable Short id) {
        return localService.buscarPorId(id);
    }
}
