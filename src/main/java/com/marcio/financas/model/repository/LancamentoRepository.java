package com.marcio.financas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marcio.financas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
