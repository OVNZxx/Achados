package com.br.projetointegrador.pi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.projetointegrador.pi.dto.publicacaoContato.PublicacaoContatoRequest;
import com.br.projetointegrador.pi.dto.publicacaoContato.PublicacaoContatoResponse;
import com.br.projetointegrador.pi.enumerators.Permissao;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Contato;
import com.br.projetointegrador.pi.model.Publicacao;
import com.br.projetointegrador.pi.model.PublicacaoContato;
import com.br.projetointegrador.pi.model.Usuario;
import com.br.projetointegrador.pi.model.UsuarioAutenticado;
import com.br.projetointegrador.pi.repository.ContatoRepository;
import com.br.projetointegrador.pi.repository.PublicacaoContatoRepository;
import com.br.projetointegrador.pi.repository.PublicacaoRepository;
import com.br.projetointegrador.pi.repository.UsuarioRepository;

@Service
public class PublicacaoContatoService {

    @Autowired
    private PublicacaoContatoRepository publicacaoContatoRepository;

    @Autowired
    private PublicacaoRepository publicacaoRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<PublicacaoContatoResponse> listarPorPublicacao(Long idPublicacao) {
        obterPublicacao(idPublicacao);
        return publicacaoContatoRepository.findByPublicacao_IdPublicacaoOrderByContato_IdContato(idPublicacao)
                .stream()
                .map(PublicacaoContatoResponse::new)
                .toList();
    }

    @Transactional
    public PublicacaoContatoResponse disponibilizarContato(Long idPublicacao,
            PublicacaoContatoRequest request) {
        Publicacao publicacao = obterPublicacao(idPublicacao);
        Usuario usuarioAtual = obterUsuarioAtual();
        validarPermissao(publicacao, usuarioAtual);

        Contato contato = contatoRepository.findByIdContatoInAndIdUsuario(
                List.of(request.idContato()), publicacao.getIdUsuario())
                .stream()
                .findFirst()
                .orElseThrow(() -> new GenericException(
                        "O contato não pertence ao usuário da publicação",
                        "CONTATO_INVALIDO", HttpStatus.BAD_REQUEST));

        if (publicacaoContatoRepository
                .findByPublicacao_IdPublicacaoAndContato_IdContato(idPublicacao, contato.getIdContato())
                .isPresent()) {
            throw new GenericException("O contato já está disponibilizado nesta publicação",
                    "CONTATO_JA_DISPONIBILIZADO", HttpStatus.CONFLICT);
        }

        PublicacaoContato associacao = new PublicacaoContato(publicacao, contato);
        publicacaoContatoRepository.save(associacao);
        return new PublicacaoContatoResponse(associacao);
    }

    @Transactional
    public void removerContato(Long idPublicacao, Long idContato) {
        Publicacao publicacao = obterPublicacao(idPublicacao);
        Usuario usuarioAtual = obterUsuarioAtual();
        validarPermissao(publicacao, usuarioAtual);

        PublicacaoContato associacao = publicacaoContatoRepository
                .findByPublicacao_IdPublicacaoAndContato_IdContato(idPublicacao, idContato)
                .orElseThrow(() -> new GenericException("Contato não disponibilizado nesta publicação",
                        "PUBLICACAO_CONTATO_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (publicacaoContatoRepository.countByPublicacao_IdPublicacao(idPublicacao) <= 1) {
            throw new GenericException("A publicação deve manter ao menos um contato disponibilizado",
                    "CONTATO_OBRIGATORIO", HttpStatus.CONFLICT);
        }

        publicacaoContatoRepository.delete(associacao);
    }

    private Publicacao obterPublicacao(Long idPublicacao) {
        return publicacaoRepository.findById(idPublicacao)
                .orElseThrow(() -> new GenericException("Erro! Publicação não existe!",
                        "PUBLI_NOT_FOUND", HttpStatus.NOT_FOUND));
    }

    private Usuario obterUsuarioAtual() {
        UsuarioAutenticado autenticado = (UsuarioAutenticado) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(autenticado.getUsername());
        if (usuario == null) {
            throw new GenericException("O e-mail não existe!", "EMAIL_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        return usuario;
    }

    private void validarPermissao(Publicacao publicacao, Usuario usuario) {
        boolean proprietario = publicacao.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario());
        boolean administrador = usuario.getPermissao() == Permissao.ADMIN;
        if (!proprietario && !administrador) {
            throw new GenericException("Você não tem permissão para alterar essa publicação",
                    "ACESSO_NEGADO", HttpStatus.FORBIDDEN);
        }
    }
}
