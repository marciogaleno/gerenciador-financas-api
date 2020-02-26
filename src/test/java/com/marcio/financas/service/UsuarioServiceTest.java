package com.marcio.financas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.marcio.financas.FinancasApplication;
import com.marcio.financas.exceptions.ErroAutenticacaoException;
import com.marcio.financas.exceptions.RegraNegocioException;
import com.marcio.financas.model.entity.Usuario;
import com.marcio.financas.model.repository.UsuarioRepository;
import com.marcio.financas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ComponentScan(basePackageClasses = FinancasApplication.class)
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveValidarEmail() {
		Mockito.when(this.repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		//ação
		Assertions.assertDoesNotThrow(() -> {
			service.validarEmail("marcio@email.com");
		});
	}
	
	@Test
	public void deveLancarExceptionSeExistirEmailCadastrado() {
		//cenário
		Mockito.when(this.repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validarEmail("marcio@email.com");
		});
	}
	
	@Test
	public void deveAutenticarUsuarioComSucesso() {
		//cenario
		String email = "marcio@teste.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(this.repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//ação
		Usuario result = this.service.autenticar(email, senha);
				
		//verificação
		org.assertj.core.api.Assertions.assertThat(result).isNotNull();		
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioPorEmail() {
		//cenário
		Mockito.when(this.repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//ação
		Throwable exception = Assertions.assertThrows(ErroAutenticacaoException.class, () -> {
			service.autenticar("marci@teste.com", "senha");
		});
		
		assertEquals("Usuário não existe para o e-mail informado", exception.getMessage());
	}
	
	@Test
	public void deveLancarErroQuandoSenhaForInvalida() {
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("marcio@teste.com").senha(senha).build();
		
		Mockito.when(this.repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//ação
		Throwable exception = Assertions.assertThrows(ErroAutenticacaoException.class, () -> {
			service.autenticar("galeno@teste.com", "123");
		});
		
		//Verificação
		assertEquals("Senha inválida", exception.getMessage());
	}
	
	@Test
	public void deveSalvarUsuario() {
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1l).nome("marcio").email("marcio@teste.com").senha("senha").build();
		
		Mockito.when(this.repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//Ação
		Usuario usuarioSalvo = this.service.salvar(new Usuario());
		
		//Verificação
		Assertions.assertNotNull(usuarioSalvo);
		Assertions.assertEquals(usuarioSalvo.getId(), 1l);
		Assertions.assertEquals(usuarioSalvo.getNome(), "marcio");
		Assertions.assertEquals(usuarioSalvo.getEmail(), "marcio@teste.com");
		Assertions.assertEquals(usuarioSalvo.getSenha(), "senha");
	}
	
	public void noaDeveSalvarUsuario( ) {
		//Cenáriuo
		String email = "marcio@teste.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(this.service).validarEmail(email);
		
		//ação
		this.service.salvar(usuario);
		
		//verificação
		Mockito.verify(this.repository, Mockito.never()).save(usuario);
	}
}
