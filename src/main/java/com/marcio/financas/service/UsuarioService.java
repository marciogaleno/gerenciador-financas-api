package com.marcio.financas.service;

import java.util.Optional;

import com.marcio.financas.model.entity.Usuario;

public interface UsuarioService {
	
	public Usuario autenticar(String email, String senha);
	
	public Usuario salvar(Usuario usuario);
	
	public void validarEmail(String email);
	
	public Optional<Usuario> obterUsuario(Long id);
}
