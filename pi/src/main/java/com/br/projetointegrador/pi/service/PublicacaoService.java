package com.br.projetointegrador.pi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.br.projetointegrador.pi.dto.publicacao.PublicacaoRequest;
import com.br.projetointegrador.pi.dto.publicacao.PublicacaoResponse;
import com.br.projetointegrador.pi.enumerators.Permissao;
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

        return new PublicacaoResponse(publicacao);
    }

    public PublicacaoResponse buscarPorId(Long id) {
        log.info("Buscando publicação por id {}", id);

        Publicacao publi = publicacaoRepository.findById(id).orElseThrow(() -> new GenericException("Erro! Publicação não existe!",
        "PUBLI_NOT_FOUND", HttpStatus.NOT_FOUND));

        return new PublicacaoResponse(publi);
    }


    public Page<PublicacaoResponse> listarTodos(Pageable pageable) {
        log.info("Listando todas as publicações");
        
        return publicacaoRepository.findAll(pageable).map(PublicacaoResponse::new);
    }

    public void deletarPublicacaoPorId(Long id) {
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());
        Publicacao publi = publicacaoRepository.findById(id).orElseThrow(() -> new GenericException("Erro! Publicação não existe!",
        "PUBLI_NOT_FOUND", HttpStatus.NOT_FOUND));

        boolean proprietario = publi.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario());

        boolean administrador = usuario.getPermissao() == Permissao.ADMIN;

        if(!proprietario && !administrador) {
            throw new GenericException("Você não tem permissão para alterar essa publicação", "ACESSO_NEGADO", HttpStatus.FORBIDDEN);
        }

        log.info("Deletando publicação por id: {}", id);
        
        publicacaoRepository.deleteById(id);
    }

    public PublicacaoResponse atualizarPublicacaoPorId(Long id, PublicacaoRequest publicacaoRequest) {
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());
        Publicacao publi = publicacaoRepository.findById(id).orElseThrow(() -> new GenericException("Erro! Publicação não existe!",
        "PUBLI_NOT_FOUND", HttpStatus.NOT_FOUND));
        Local local = localRepository.findById(publi.getIdLocal().getIdLocal())
        .orElseThrow(() -> new GenericException("Erro! Local não existente", "LOCAL_NOT_FOUND", HttpStatus.NOT_FOUND));

        boolean proprietario = publi.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario());

        boolean administrador = usuario.getPermissao() == Permissao.ADMIN;

        if(!proprietario && !administrador) {
            throw new GenericException("Você não tem permissão para alterar essa publicação", "ACESSO_NEGADO", HttpStatus.FORBIDDEN);
        }

        log.info("Atualizando publicação do ID: {}", id);

        publi.setIdLocal(local);
        publi.setNome(publicacaoRequest.nome());
        publi.setDataOcorrencia(publicacaoRequest.dataOcorrencia());
        publi.setDescricao(publicacaoRequest.descricao());
        publi.setStatus(publicacaoRequest.status());
        publi.setTipo(publicacaoRequest.tipo());

        publicacaoRepository.save(publi);

        return new PublicacaoResponse(publi);
    }

    public Page<PublicacaoResponse> buscaPorNome(String nome, Pageable pageable) {
        log.info("Listando todas as publicações com nome parecido com: {}", nome);

        Page<Publicacao> publicacoes;

        if(nome != null && !nome.isEmpty()) {
            publicacoes = publicacaoRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            throw new GenericException("Nenhuma publicação encontrada com esse nome", "PUBLI_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        return publicacoes.map(PublicacaoResponse::new);
    }
}
