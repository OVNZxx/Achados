package com.br.projetointegrador.pi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.projetointegrador.pi.dto.dashboard.DashboardResponse;
import com.br.projetointegrador.pi.service.DashboardService;

@RestController 
@RequestMapping ("/dashboard")
public class DashboardController {

    @Autowired 
    private DashboardService dashboardService;

    @GetMapping 
    public DashboardResponse retornarMetricas() {
        return dashboardService.retornarMetricas();
    }
}
