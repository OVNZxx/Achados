package com.br.projetointegrador.pi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.projetointegrador.pi.dto.dashboard.DashboardResponse;
import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;
import com.br.projetointegrador.pi.repository.PublicacaoRepository;
import com.br.projetointegrador.pi.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
public class DashboardService {

    @Autowired 
    private UsuarioRepository usuarioRepository;

    @Autowired 
    private PublicacaoRepository publicacaoRepository;

    public DashboardResponse retornarMetricas() {
        log.info("Retornando métricas!");
        
        Long usuariosCadastrados = usuarioRepository.count();
        Long publicacoesAtivas = publicacaoRepository.countByStatus(StatusPublicacao.ATIVA);
        Long publicacoesFinalizadas = publicacaoRepository.countByStatus(StatusPublicacao.FINALIZADA);
        Long perdidos = publicacaoRepository.countByTipo(TipoPublicacao.PERDIDO);
        Long encontrados = publicacaoRepository.countByTipo(TipoPublicacao.ENCONTRADO);

        return new DashboardResponse(usuariosCadastrados, publicacoesAtivas, publicacoesFinalizadas, perdidos, encontrados);
    }
}
