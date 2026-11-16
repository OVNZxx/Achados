package com.br.projetointegrador.pi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.br.projetointegrador.pi.dto.local.LocalResponse;
import com.br.projetointegrador.pi.dto.publicacao.PublicacaoRequest;
import com.br.projetointegrador.pi.dto.publicacao.PublicacaoResponse;
import com.br.projetointegrador.pi.dto.usuario.UsuarioResponse;
import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Local;
import com.br.projetointegrador.pi.model.Publicacao;
import com.br.projetointegrador.pi.model.Usuario;
import com.br.projetointegrador.pi.model.UsuarioAutenticado;
import com.br.projetointegrador.pi.repository.LocalRepository;
import com.br.projetointegrador.pi.repository.PublicacaoRepository;
import com.br.projetointegrador.pi.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
public class PublicacaoService {

    @Autowired 
    private PublicacaoRepository publicacaoRepository;

    @Autowired 
    private LocalRepository localRepository;

    @Autowired 
    private UsuarioRepository usuarioRepository;

    public PublicacaoResponse cadastrar(PublicacaoRequest publicacaoRequest) {
        Publicacao publicacao = new Publicacao();
        Local local = localRepository.findById(publicacaoRequest.idLocal())
        .orElseThrow(() -> new GenericException("Erro! Local não existente", "LOCAL_NOT_FOUND", HttpStatus.NOT_FOUND));
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());

        if (usuario == null) {
            throw new GenericException("O e-mail não existe!", "EMAIL_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        publicacao.setIdUsuario(usuario);
        publicacao.setIdLocal(local);
        publicacao.setNome(publicacaoRequest.nome());
        publicacao.setDataOcorrencia(publicacaoRequest.dataOcorrencia());
        publicacao.setDescricao(publicacaoRequest.descricao());
        publicacao.setStatus(StatusPublicacao.ATIVA);
        publicacao.setTipo(TipoPublicacao.PERDIDO);


        log.info("Adicionando publicação...");

        publicacaoRepository.save(publicacao);

        return new PublicacaoResponse(publicacao.getIdPublicacao(),
        new UsuarioResponse(usuario.getIdUsuario(), usuario.getNome(), usuario.getPermissao()), new LocalResponse(local.getIdLocal(), local.getNome()), publicacao.getNome(),
        publicacao.getDescricao(), publicacao.getDataCriacao(), publicacao.getDataOcorrencia(), publicacao.getStatus(), publicacao.getTipo());
    }

    public Optional<PublicacaoResponse> buscarPorId(Long id) {
        log.info("Buscando publicação por id {id}", id);
        
        if(!publicacaoRepository.existsById(id)) {
            throw new GenericException("Erro! Publicação não existe!", "PUBLI_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        Publicacao publicacao = publicacaoRepository.findById(id).orElseThrow();

        Usuario usuario = publicacao.getIdUsuario();
        Local local = publicacao.getIdLocal();

        return publicacaoRepository.findById(id).map(publi -> new PublicacaoResponse(publi.getIdPublicacao(),
        new UsuarioResponse(usuario.getIdUsuario(), usuario.getNome(), usuario.getPermissao()), 
        new LocalResponse(local.getIdLocal(), local.getNome()), publi.getNome(), publi.getDescricao(), publi.getDataCriacao(), publi.getDataOcorrencia(),
        publi.getStatus(), publi.getTipo()));
    }


    public Page<PublicacaoResponse> listarTodos(Pageable pageable) {
        log.info("Listando todas as publicações");
        
        return publicacaoRepository.findAll(pageable).map(PublicacaoResponse::new);
    }
}
