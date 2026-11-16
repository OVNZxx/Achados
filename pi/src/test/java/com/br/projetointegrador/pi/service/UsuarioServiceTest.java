package com.br.projetointegrador.pi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.br.projetointegrador.pi.dto.usuario.UsuarioCadastrarRequest;
import com.br.projetointegrador.pi.dto.usuario.UsuarioResponse;
import com.br.projetointegrador.pi.enumerators.Permissao;
import com.br.projetointegrador.pi.exceptions.GenericException;
import com.br.projetointegrador.pi.model.Usuario;
import com.br.projetointegrador.pi.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso quando e-mail institucional for válido e inédito")
    void deveCadastrarUsuarioComSucesso() {
        UsuarioCadastrarRequest request = new UsuarioCadastrarRequest(
                "Vinícius",
                "vinicius@unifio.edu.br",
                "senha123"
        );

        when(usuarioRepository.existsByEmail("vinicius@unifio.edu.br")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashSenhaCriptografada");

        UsuarioResponse response = usuarioService.cadastro(request);

        assertNotNull(response);
        assertEquals("Vinícius", response.nome());
        assertEquals(Permissao.USUARIO, response.permissao());

        verify(passwordEncoder, times(1)).encode("senha123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar GenericException quando o e-mail não for institucional da Unifio")
    void deveLancarExcecaoQuandoEmailNaoForInstitucional() {
        UsuarioCadastrarRequest request = new UsuarioCadastrarRequest(
                "Vinícius",
                "vinicius@gmail.com",
                "senha123"
        );

        GenericException exception = assertThrows(
                GenericException.class,
                () -> usuarioService.cadastro(request)
        );

        assertEquals("Erro! O e-mail deve ser o institucional da unifio", exception.getMessage());
        assertEquals("WRONG_EMAIL", exception.getCodigoErro());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar GenericException quando o e-mail já estiver cadastrado")
    void deveLancarExcecaoQuandoEmailJaExistir() {
        UsuarioCadastrarRequest request = new UsuarioCadastrarRequest(
                "Vinícius",
                "vinicius@unifio.edu.br",
                "senha123"
        );

        when(usuarioRepository.existsByEmail("vinicius@unifio.edu.br")).thenReturn(true);

        GenericException exception = assertThrows(
                GenericException.class,
                () -> usuarioService.cadastro(request)
        );

        assertEquals("Erro! O e-mail já está sendo utilizado!", exception.getMessage());
        assertEquals("EMAIL_ALREADY_EXISTS", exception.getCodigoErro());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Deve retornar UserDetails quando buscar usuário por e-mail existente")
    void deveCarregarUsuarioPorEmailComSucesso() {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(1L);
        usuarioMock.setNome("Vinícius");
        usuarioMock.setEmail("vinicius@unifio.edu.br");
        usuarioMock.setHashSenha("hashSenhaCriptografada");
        usuarioMock.setPermissao(Permissao.USUARIO);

        when(usuarioRepository.findByEmail("vinicius@unifio.edu.br")).thenReturn(usuarioMock);

        UserDetails userDetails = usuarioService.loadUserByUsername("vinicius@unifio.edu.br");

        assertNotNull(userDetails);
        assertEquals("vinicius@unifio.edu.br", userDetails.getUsername());
        assertEquals("hashSenhaCriptografada", userDetails.getPassword());
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o e-mail não existir no login")
    void deveLancarExcecaoQuandoUsuarioNaoForEncontradoNoLogin() {
        when(usuarioRepository.findByEmail("naoexiste@unifio.edu.br")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioService.loadUserByUsername("naoexiste@unifio.edu.br")
        );

        assertEquals("O email ou a senha estão incorretos", exception.getMessage());
    }
}