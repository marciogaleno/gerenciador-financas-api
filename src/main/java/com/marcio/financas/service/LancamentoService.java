package com.marcio.financas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.marcio.financas.model.entity.Lancamento;
import com.marcio.financas.model.enums.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamento);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	Optional<Lancamento> obterPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long usuarioId);
	
	void validar(Lancamento lancamento);
}
