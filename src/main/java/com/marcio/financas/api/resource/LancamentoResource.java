package com.marcio.financas.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marcio.financas.api.dto.AtualizarStatusDTO;
import com.marcio.financas.api.dto.LancamentoDTO;
import com.marcio.financas.exceptions.RegraNegocioException;
import com.marcio.financas.model.entity.Lancamento;
import com.marcio.financas.model.entity.Usuario;
import com.marcio.financas.model.enums.StatusLancamento;
import com.marcio.financas.model.enums.TipoLancamento;
import com.marcio.financas.service.LancamentoService;
import com.marcio.financas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoService service;
	
	private final UsuarioService serviceUsuario;
	
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamento = converter(dto);
			Lancamento lancamentoSalvo = this.service.salvar(lancamento);
			return new ResponseEntity(lancamentoSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return this.service.obterPorId(id).map((entity) -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				this.service.atualizar(lancamento);
				return new ResponseEntity(lancamento, HttpStatus.OK);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> new ResponseEntity("Lancamento não encontrado", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{id}/atualizar-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizarStatusDTO dto) {
		return this.service.obterPorId(id).map((entity) -> {
			try {
				StatusLancamento status = StatusLancamento.valueOf(dto.getStatus());
				
				if (status == null) {
					return ResponseEntity.badRequest().body("Status inválido");				
				}
				
				this.service.atualizarStatus(entity, status);
				return new ResponseEntity(entity, HttpStatus.OK);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body("Status inválido");	
			}

		}).orElseGet(() -> new ResponseEntity("Lancamento não encontrado", HttpStatus.BAD_REQUEST));
	}
	
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return this.service.obterPorId(id).map((entity) -> {
			try {
				this.service.deletar(entity);
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> new ResponseEntity("Lancamento não encontrado", HttpStatus.BAD_REQUEST));
	}
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long usuarioId
			) {
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuario = this.serviceUsuario.obterUsuario(usuarioId);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Usuário não encontrado");
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = this.service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setMes(dto.getMes());
		lancamento.setAno(dto.getAno());
		lancamento.setValor(dto.getValor());
		
		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		
		
		Usuario usuario = this.serviceUsuario.obterUsuario(dto.getUsuario())
				.orElseThrow( () -> new RegraNegocioException("Usuário não encontrado para o ID informado"));
		
		lancamento.setUsuario(usuario);
		
		return lancamento;
	}
}
