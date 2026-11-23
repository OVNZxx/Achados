package com.br.projetointegrador.pi.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.projetointegrador.pi.dto.publicacao.PublicacaoRequest;
import com.br.projetointegrador.pi.dto.publicacao.PublicacaoResponse;
import com.br.projetointegrador.pi.enumerators.Permissao;
import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Contato;
import com.br.projetointegrador.pi.model.Local;
import com.br.projetointegrador.pi.model.Publicacao;
import com.br.projetointegrador.pi.model.PublicacaoContato;
import com.br.projetointegrador.pi.model.Usuario;
import com.br.projetointegrador.pi.model.UsuarioAutenticado;
import com.br.projetointegrador.pi.repository.ContatoRepository;
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

    @Autowired
    private ContatoRepository contatoRepository;

    @Transactional
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
        List<Contato> contatos = validarContatos(publicacaoRequest.idsContatos(), usuario);

        log.info("Adicionando publicação...");

        publicacaoRepository.save(publicacao);
        publicacao.getContatosDisponibilizados().addAll(
                criarAssociacoes(publicacao, contatos));
        publicacaoRepository.save(publicacao);

        return new PublicacaoResponse(publicacao);
    }

    @Transactional(readOnly = true)
    public PublicacaoResponse buscarPorId(Long id) {
        log.info("Buscando publicação por id {}", id);

        Publicacao publi = publicacaoRepository.findById(id).orElseThrow(() -> new GenericException("Erro! Publicação não existe!",
        "PUBLI_NOT_FOUND", HttpStatus.NOT_FOUND));

        return new PublicacaoResponse(publi);
    }


    @Transactional(readOnly = true)
    public Page<PublicacaoResponse> listarTodos(Pageable pageable) {
        log.info("Listando todas as publicações");
        
        return publicacaoRepository.findAll(pageable).map(PublicacaoResponse::new);
    }

    @Transactional
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

    @Transactional
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

        List<Contato> contatos = validarContatos(publicacaoRequest.idsContatos(), publi.getIdUsuario());

        log.info("Atualizando publicação do ID: {}", id);

        publi.setIdLocal(local);
        publi.setNome(publicacaoRequest.nome());
        publi.setDataOcorrencia(publicacaoRequest.dataOcorrencia());
        publi.setDescricao(publicacaoRequest.descricao());
        publi.setStatus(publicacaoRequest.status());
        publi.setTipo(publicacaoRequest.tipo());
        sincronizarAssociacoes(publi, contatos);

        publicacaoRepository.save(publi);

        return new PublicacaoResponse(publi);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Page<PublicacaoResponse> listarMinhas(Pageable pageable) {
        log.info("Listando todas as publicações de um usuário");

        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioAutenticado.getUsername());

        return publicacaoRepository.findByIdUsuario(usuario, pageable).map(PublicacaoResponse::new);
    }

    private List<Contato> validarContatos(List<Long> idsContatos, Usuario dono) {
        if (idsContatos == null) {
            throw new GenericException("A lista de contatos não pode ser nula", "CONTATOS_INVALIDOS",
                    HttpStatus.BAD_REQUEST);
        }
        if (idsContatos.isEmpty()) {
            throw new GenericException("Informe ao menos um contato para a publicação",
                    "CONTATOS_INVALIDOS", HttpStatus.BAD_REQUEST);
        }

        Set<Long> idsUnicos = new HashSet<>(idsContatos);
        if (idsUnicos.size() != idsContatos.size()) {
            throw new GenericException("A lista contém contatos duplicados", "CONTATOS_INVALIDOS",
                    HttpStatus.BAD_REQUEST);
        }
        List<Contato> contatos = contatoRepository.findByIdContatoInAndIdUsuario(idsContatos, dono);
        Map<Long, Contato> contatosPorId = new HashMap<>();
        for (Contato contato : contatos) {
            contatosPorId.put(contato.getIdContato(), contato);
        }
        if (contatosPorId.size() != idsContatos.size()) {
            throw new GenericException("Um ou mais contatos não pertencem ao usuário da publicação",
                    "CONTATOS_INVALIDOS", HttpStatus.BAD_REQUEST);
        }

        return idsContatos.stream()
                .map(contatosPorId::get)
                .toList();
    }

    private List<PublicacaoContato> criarAssociacoes(Publicacao publicacao, List<Contato> contatos) {
        return contatos.stream()
                .map(contato -> new PublicacaoContato(publicacao, contato))
                .toList();
    }

    private void sincronizarAssociacoes(Publicacao publicacao, List<Contato> contatos) {
        Map<Long, PublicacaoContato> existentes = new HashMap<>();
        for (PublicacaoContato associacao : publicacao.getContatosDisponibilizados()) {
            existentes.put(associacao.getContato().getIdContato(), associacao);
        }

        Set<Long> desejados = new HashSet<>();
        for (Contato contato : contatos) {
            desejados.add(contato.getIdContato());
        }

        Iterator<PublicacaoContato> iterator = publicacao.getContatosDisponibilizados().iterator();
        while (iterator.hasNext()) {
            PublicacaoContato associacao = iterator.next();
            if (!desejados.contains(associacao.getContato().getIdContato())) {
                iterator.remove();
            }
        }

        for (Contato contato : contatos) {
            if (!existentes.containsKey(contato.getIdContato())) {
                publicacao.getContatosDisponibilizados().add(new PublicacaoContato(publicacao, contato));
            }
        }
    }
}
