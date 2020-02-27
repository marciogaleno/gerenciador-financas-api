package com.marcio.financas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marcio.financas.exceptions.RegraNegocioException;
import com.marcio.financas.model.entity.Lancamento;
import com.marcio.financas.model.enums.StatusLancamento;
import com.marcio.financas.model.enums.TipoLancamento;
import com.marcio.financas.model.repository.LancamentoRepository;
import com.marcio.financas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {

	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		this.validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);;
		return this.repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		this.validar(lancamento);
		return this.repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		this.repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamento) {
		Example example = Example.of(lancamento, 
				ExampleMatcher.matching()
					.withIgnoreCase()
					.withStringMatcher(StringMatcher.CONTAINING));
		
		return this.repository.findAll(example);
	}

	@Override
	@Transactional
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		this.atualizar(lancamento);
	}
	
	public void validar(Lancamento lancamento) {
		if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma descrição válida");
		}
		
		if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um mês válido");
		}
		
		if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um ano válido");
		}
		
		if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um usuário");
		}
		
		if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um valor válido");
		}
		
		if (lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um tipo de lançamento");
		}
	}

	@Override
	public Optional<Lancamento> obterPorId(Long id) {
		return this.repository.findById(id);
	}

	@Override
	public BigDecimal obterSaldoPorUsuario(Long usuarioId) {
		BigDecimal receita = this.repository.saldoPorUsuarioETipoLancamento(usuarioId, TipoLancamento.RECEITA);
		BigDecimal despesa = this.repository.saldoPorUsuarioETipoLancamento(usuarioId, TipoLancamento.DESPESA);

		if (receita == null) {
			receita = BigDecimal.ZERO;
		}
		
		if (despesa == null) {
			despesa = BigDecimal.ZERO;
		}
		
		return receita.subtract(despesa);
	}

}
