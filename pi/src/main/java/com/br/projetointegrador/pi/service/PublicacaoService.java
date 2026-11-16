package com.br.projetointegrador.pi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.br.projetointegrador.pi.dto.publicacao.PublicacaoRequest;
import com.br.projetointegrador.pi.dto.publicacao.PublicacaoResponse;
import com.br.projetointegrador.pi.dto.usuario.UsuarioResponse;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Local;
import com.br.projetointegrador.pi.model.Publicacao;
import com.br.projetointegrador.pi.model.Usuario;
import com.br.projetointegrador.pi.model.UsuarioAutenticado;
import com.br.projetointegrador.pi.repository.LocalRepository;
import com.br.projetointegrador.pi.repository.PublicacaoRepository;
import com.br.projetointegrador.pi.repository.UsuarioRepository;

@Service 
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
        publicacao.setStatus(publicacaoRequest.status());
        publicacao.setTipo(publicacaoRequest.tipo());

        publicacaoRepository.save(publicacao);

        return new PublicacaoResponse(publicacao.getIdPublicacao(),
        new UsuarioResponse(usuario.getIdUsuario(), usuario.getNome(), usuario.getPermissao()), local, publicacao.getNome(),
        publicacao.getDescricao(), publicacao.getDataCriacao(), publicacao.getDataOcorrencia(), publicacao.getStatus(), publicacao.getTipo());
    }

}
