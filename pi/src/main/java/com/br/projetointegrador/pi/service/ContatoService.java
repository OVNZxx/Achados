package com.br.projetointegrador.pi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.projetointegrador.pi.dto.contato.ContatoRequest;
import com.br.projetointegrador.pi.dto.contato.ContatoResponse;
import com.br.projetointegrador.pi.enumerators.Permissao;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Contato;
import com.br.projetointegrador.pi.model.PublicacaoContato;
import com.br.projetointegrador.pi.model.Usuario;
import com.br.projetointegrador.pi.model.UsuarioAutenticado;
import com.br.projetointegrador.pi.repository.ContatoRepository;
import com.br.projetointegrador.pi.repository.PublicacaoContatoRepository;
import com.br.projetointegrador.pi.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
public class ContatoService {

    @Autowired 
    private ContatoRepository contatoRepository;

    @Autowired 
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PublicacaoContatoRepository publicacaoContatoRepository;

    public ContatoResponse cadastrar(ContatoRequest contatoRequest) {
        Contato contato = new Contato();
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());
        List<Contato> contatos = contatoRepository.findByidUsuario(usuario);

        if(contatos.size() >= 3) {
            throw new GenericException("ERRO! Não são permitidos mais de 3 contatos por usuário", "CONTATOS_SIZE", HttpStatus.CONFLICT);
        }

        contato.setIdUsuario(usuario);
        contato.setContato(contatoRequest.contato());
        
        log.info("Criando um contato para o usuário: {}", usuario.getNome());

        contatoRepository.save(contato);

        return new ContatoResponse(contato);
    }

    public List<ContatoResponse> listarTodosPorUsuario() {
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());

        List<Contato> contatos = contatoRepository.findByidUsuario(usuario);

        List<ContatoResponse> listaContatos = new ArrayList<>();

        for(Contato contato : contatos) {
            listaContatos.add(new ContatoResponse(contato));
        }

        log.info("Listando todos os contatos para o usuário: {}", usuario.getNome());

        return listaContatos;
    }

    public ContatoResponse buscarPorId(Long id) {
        Contato contato = contatoRepository.findById(id).orElseThrow(() -> new GenericException("Contato inexistente", "CONTATO_NOT_FOUND",
        HttpStatus.NOT_FOUND));
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());
        boolean proprietario = contato.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario());
        boolean administrador = usuario.getPermissao() == Permissao.ADMIN;
        if (!proprietario && !administrador) {
            throw new GenericException("Você não tem permissão para visualizar esse contato", "ACESSO_NEGADO", HttpStatus.FORBIDDEN);
        }

        return new ContatoResponse(contato);
    }

    @Transactional
    public void deletarContatoPorId(Long id) {
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());
        Contato contato = contatoRepository.findById(id).orElseThrow(() -> new GenericException("Contato inexistente", "CONTATO_NOT_FOUND",
        HttpStatus.NOT_FOUND));

        boolean proprietario = contato.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario());

        boolean administrador = usuario.getPermissao() == Permissao.ADMIN;

        if(!proprietario && !administrador) {
            throw new GenericException("Você não tem permissão para alterar essa contato", "ACESSO_NEGADO", HttpStatus.FORBIDDEN);
        }

        List<PublicacaoContato> associacoes = publicacaoContatoRepository.findByContato_IdContato(id);
        boolean unicoContatoDePublicacao = associacoes.stream().anyMatch(associacao ->
                publicacaoContatoRepository.countByPublicacao_IdPublicacao(
                        associacao.getPublicacao().getIdPublicacao()) <= 1);
        if (unicoContatoDePublicacao) {
            throw new GenericException("O contato é o único disponibilizado em uma ou mais publicações",
                    "CONTATO_EM_USO", HttpStatus.CONFLICT);
        }

        log.info("Deletando um contato por id! {}", id);

        publicacaoContatoRepository.deleteAllByContato_IdContato(id);
        contatoRepository.delete(contato);
    }

    public ContatoResponse editar(Long id, ContatoRequest contatoRequest) {
        Contato contato = contatoRepository.findById(id).orElseThrow(() -> new GenericException("Contato inexistente", "CONTATO_NOT_FOUND",
        HttpStatus.NOT_FOUND));
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());

        boolean proprietario = contato.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario());

        boolean administrador = usuario.getPermissao() == Permissao.ADMIN;

        if(!proprietario && !administrador) {
            throw new GenericException("Você não tem permissão para alterar essa contato", "ACESSO_NEGADO", HttpStatus.FORBIDDEN);
        }

        contato.setIdUsuario(usuario);
        contato.setContato(contatoRequest.contato());
        
        log.info("Editando um contato do usuário: {}", usuario.getNome());

        contatoRepository.save(contato);

        return new ContatoResponse(contato);
    }
}
