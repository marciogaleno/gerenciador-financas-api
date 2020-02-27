package com.marcio.financas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.marcio.financas.model.entity.Lancamento;
import com.marcio.financas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
	@Query(value=" SELECT SUM(l.valor) FROM Lancamento l JOIN l.usuario u WHERE u.id = :usuarioId AND l.tipo = :tipo GROUP BY u")
	BigDecimal saldoPorUsuarioETipoLancamento(@Param("usuarioId") Long usuarioId, @Param("tipo") TipoLancamento tipo);
}
