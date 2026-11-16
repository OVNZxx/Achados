package com.br.projetointegrador.pi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.br.projetointegrador.pi.dto.local.LocalResponse;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Local;
import com.br.projetointegrador.pi.repository.LocalRepository;

import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
public class LocalService {

    @Autowired 
    private LocalRepository localRepository;

    public List<LocalResponse> listarTodos() {
        List<Local> locais = localRepository.findAll();
        List<LocalResponse> localResponses = new ArrayList<>();

        log.info("Listando todos os locais!");

        for(Local local : locais) {
            localResponses.add(new LocalResponse(local));
        }

        return localResponses;
    }

    public LocalResponse buscarPorId(Short id) {
        return localRepository.findById(id).map(local -> new LocalResponse(local)).orElseThrow(() ->
        new GenericException("Erro! Local não encontrado", "LOCAL_NOT_FOUND", HttpStatus.NOT_FOUND));
    }
}
