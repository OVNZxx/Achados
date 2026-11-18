package com.br.projetointegrador.pi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.projetointegrador.pi.dto.dashboard.DashboardResponse;
import com.br.projetointegrador.pi.enumerators.Permissao;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Usuario;
import com.br.projetointegrador.pi.model.UsuarioAutenticado;
import com.br.projetointegrador.pi.repository.UsuarioRepository;
import com.br.projetointegrador.pi.service.DashboardService;

@RestController 
@RequestMapping ("/dashboard")
public class DashboardController {

    @Autowired 
    private DashboardService dashboardService;

    @Autowired 
    UsuarioRepository usuarioRepository;

    @GetMapping 
    public DashboardResponse retornarMetricas() {
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());

        if(usuario.getPermissao() != Permissao.ADMIN) {
            throw new GenericException("Erro! Você não pode acessar este serviço", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }

        return dashboardService.retornarMetricas();
    }
}
