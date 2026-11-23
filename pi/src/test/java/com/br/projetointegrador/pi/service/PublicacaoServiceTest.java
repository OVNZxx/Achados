package com.br.projetointegrador.pi.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.br.projetointegrador.pi.dto.publicacao.PublicacaoRequest;
import com.br.projetointegrador.pi.dto.publicacao.PublicacaoResponse;
import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Local;
import com.br.projetointegrador.pi.model.Contato;
import com.br.projetointegrador.pi.model.Publicacao;
import com.br.projetointegrador.pi.model.Usuario;
import com.br.projetointegrador.pi.model.UsuarioAutenticado;
import com.br.projetointegrador.pi.repository.LocalRepository;
import com.br.projetointegrador.pi.repository.ContatoRepository;
import com.br.projetointegrador.pi.repository.PublicacaoRepository;
import com.br.projetointegrador.pi.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class PublicacaoServiceTest {

    @InjectMocks
    private PublicacaoService publicacaoService;

    @Mock
    private PublicacaoRepository publicacaoRepository;

    @Mock
    private LocalRepository localRepository;

    @Mock
    private ContatoRepository contatoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    @BeforeEach
    void setUp() {
        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    @Test
    @DisplayName("Deve cadastrar publicação com sucesso quando local e usuário existirem")
    void deveCadastrarPublicacaoComSucesso() {
        Date dataOcorrencia = Date.valueOf("2026-11-16");
        PublicacaoRequest request = new PublicacaoRequest(
                (short) 1,
                "Garrafa Térmica",
                "Garrafa preta esquecida no laboratório",
                dataOcorrencia,
                StatusPublicacao.ATIVA,
                TipoPublicacao.PERDIDO,
                List.of(20L)
        );

        Local localMock = new Local();
        localMock.setIdLocal((short) 1);
        localMock.setNome("Laboratório 02");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(10L);
        usuarioMock.setNome("Vinícius");
        usuarioMock.setEmail("vinicius@email.com");

        Contato contatoMock = new Contato();
        contatoMock.setIdContato(20L);
        contatoMock.setIdUsuario(usuarioMock);
        contatoMock.setContato("11999999999");

        when(localRepository.findById((short) 1)).thenReturn(Optional.of(localMock));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioAutenticado);
        when(usuarioAutenticado.getUsername()).thenReturn("vinicius@email.com");
        when(usuarioRepository.findByEmail("vinicius@email.com")).thenReturn(usuarioMock);
        when(contatoRepository.findByIdContatoInAndIdUsuario(List.of(20L), usuarioMock))
                .thenReturn(List.of(contatoMock));

        PublicacaoResponse response = publicacaoService.cadastrar(request);

        assertNotNull(response);
        assertEquals("Garrafa Térmica", response.nome());
        assertEquals("Garrafa preta esquecida no laboratório", response.descricao());
        assertEquals(dataOcorrencia, response.dataOcorrencia());
        assertEquals(StatusPublicacao.ATIVA, response.status());
        assertEquals(TipoPublicacao.PERDIDO, response.tipo());
        assertEquals((short) 1, response.local().idLocal());
        assertEquals(10L, response.usuario().idUsuario());
        assertEquals(1, response.contatos().size());
        assertEquals(20L, response.contatos().getFirst().idContato());
        assertEquals("11999999999", response.contatos().getFirst().contato());

        verify(publicacaoRepository, times(2)).save(any(Publicacao.class));
    }

    @Test
    @DisplayName("Deve lançar GenericException quando o local não existir")
    void deveLancarExcecaoQuandoLocalNaoExistir() {
        PublicacaoRequest request = new PublicacaoRequest(
                (short) 99,
                "Chave",
                "Chaveiro azul",
                Date.valueOf("2026-11-16"),
                StatusPublicacao.ATIVA,
                TipoPublicacao.PERDIDO,
                List.of()
        );

        when(localRepository.findById((short) 99)).thenReturn(Optional.empty());

        GenericException exception = assertThrows(
                GenericException.class,
                () -> publicacaoService.cadastrar(request)
        );

        assertEquals("Erro! Local não existente", exception.getMessage());
        assertEquals("LOCAL_NOT_FOUND", exception.getCodigoErro());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(publicacaoRepository, never()).save(any());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve lançar GenericException quando o e-mail do usuário autenticado não existir")
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        PublicacaoRequest request = new PublicacaoRequest(
                (short) 1,
                "Caderno",
                "Caderno universitário",
                Date.valueOf("2026-11-16"),
                StatusPublicacao.ATIVA,
                TipoPublicacao.PERDIDO,
                List.of()
        );

        Local localMock = new Local();
        localMock.setIdLocal((short) 1);

        when(localRepository.findById((short) 1)).thenReturn(Optional.of(localMock));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioAutenticado);
        when(usuarioAutenticado.getUsername()).thenReturn("inexistente@email.com");
        when(usuarioRepository.findByEmail("inexistente@email.com")).thenReturn(null);

        GenericException exception = assertThrows(
                GenericException.class,
                () -> publicacaoService.cadastrar(request)
        );

        assertEquals("O e-mail não existe!", exception.getMessage());
        assertEquals("EMAIL_NOT_FOUND", exception.getCodigoErro());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(publicacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve associar contatos que não pertencem ao usuário da publicação")
    void deveRejeitarContatoDeOutroUsuario() {
        PublicacaoRequest request = new PublicacaoRequest(
                (short) 1,
                "Caderno",
                "Caderno universitário",
                Date.valueOf("2026-11-16"),
                StatusPublicacao.ATIVA,
                TipoPublicacao.PERDIDO,
                List.of(99L)
        );

        Local localMock = new Local();
        localMock.setIdLocal((short) 1);
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(10L);

        when(localRepository.findById((short) 1)).thenReturn(Optional.of(localMock));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioAutenticado);
        when(usuarioAutenticado.getUsername()).thenReturn("vinicius@email.com");
        when(usuarioRepository.findByEmail("vinicius@email.com")).thenReturn(usuarioMock);
        when(contatoRepository.findByIdContatoInAndIdUsuario(List.of(99L), usuarioMock)).thenReturn(List.of());

        GenericException exception = assertThrows(GenericException.class,
                () -> publicacaoService.cadastrar(request));

        assertEquals("CONTATOS_INVALIDOS", exception.getCodigoErro());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(publicacaoRepository, never()).save(any(Publicacao.class));
    }

    @Test
    @DisplayName("Deve exigir ao menos um contato por publicação")
    void deveExigirAoMenosUmContato() {
        PublicacaoRequest request = new PublicacaoRequest(
                (short) 1,
                "Caderno",
                "Caderno universitário",
                Date.valueOf("2026-11-16"),
                StatusPublicacao.ATIVA,
                TipoPublicacao.PERDIDO,
                List.of()
        );

        Local localMock = new Local();
        localMock.setIdLocal((short) 1);
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(10L);

        when(localRepository.findById((short) 1)).thenReturn(Optional.of(localMock));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioAutenticado);
        when(usuarioAutenticado.getUsername()).thenReturn("vinicius@email.com");
        when(usuarioRepository.findByEmail("vinicius@email.com")).thenReturn(usuarioMock);

        GenericException exception = assertThrows(GenericException.class,
                () -> publicacaoService.cadastrar(request));

        assertEquals("CONTATOS_INVALIDOS", exception.getCodigoErro());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(publicacaoRepository, never()).save(any(Publicacao.class));
    }
}