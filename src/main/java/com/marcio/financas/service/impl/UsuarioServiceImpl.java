package com.marcio.financas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marcio.financas.exceptions.ErroAutenticacaoException;
import com.marcio.financas.exceptions.RegraNegocioException;
import com.marcio.financas.model.entity.Usuario;
import com.marcio.financas.model.repository.UsuarioRepository;
import com.marcio.financas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if (!usuario.isPresent()) {
			throw new ErroAutenticacaoException("Usuário não existe para o e-mail informado");
		}
		
		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacaoException("Senha inválida");
		}
		
		return usuario.get();
	}

	@Override
	public Usuario salvar(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		
		if (existe) {
			throw new RegraNegocioException("Já existe um usuário com esse e-mail cadatrado");
		}
	}

}
