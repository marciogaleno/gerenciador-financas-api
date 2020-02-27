package com.marcio.financas.api.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcio.financas.api.dto.UsuarioDTO;
import com.marcio.financas.exceptions.ErroAutenticacaoException;
import com.marcio.financas.exceptions.RegraNegocioException;
import com.marcio.financas.model.entity.Usuario;
import com.marcio.financas.service.LancamentoService;
import com.marcio.financas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {
	
	static final String API = "/api/usuarios";
	
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		//cenário
		String email = "usuario@gmail.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		Mockito.when(this.service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// execução e veificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
												.post(API.concat("/autenticar"))
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

	}
	
	@Test
	public void deveLancarBadRequestEmErroDeAutenticacao() throws Exception {
		//cenário
		String email = "usuario@gmail.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when(this.service.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// execução e veificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
												.post(API.concat("/autenticar"))
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	
	@Test
	public void deveCriarUmUsuario() throws Exception {
		//cenário
		String email = "usuario@gmail.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		Mockito.when(this.service.salvar(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// execução e veificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
												.post(API)
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

	}
	
	@Test
	public void deveLancarUmBadrequestQuandoCastrarUmUsuarioInvalido() throws Exception {
		//cenário
		String email = "usuario@gmail.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when(this.service.salvar(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// execução e veificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
												.post(API)
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
}
