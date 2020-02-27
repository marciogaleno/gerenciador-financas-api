package com.marcio.financas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.marcio.financas.FinancasApplication;
import com.marcio.financas.exceptions.RegraNegocioException;
import com.marcio.financas.model.entity.Lancamento;
import com.marcio.financas.model.enums.StatusLancamento;
import com.marcio.financas.model.enums.TipoLancamento;
import com.marcio.financas.model.repository.LancamentoRepository;
import com.marcio.financas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ComponentScan(basePackageClasses = FinancasApplication.class)
public class LancamentoServiceTest {
	
	@SpyBean
	private LancamentoServiceImpl service;
	
	@MockBean
	private LancamentoRepository repository;
	
	@Test
	public void devaSalvarLancamento() {
		//cenário
		Lancamento lancamentoSalvar = this.criarLancamento();
		Mockito.doNothing().when(this.service).validar(lancamentoSalvar);
		
		Lancamento lancamentoSalvo = this.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.EFETIVADO);
		Mockito.when(this.repository.save(lancamentoSalvar)).thenReturn(lancamentoSalvo);
		
		//Execução
		Lancamento lancamento = this.service.salvar(lancamentoSalvar);
		
		//Verificação
		Assertions.assertEquals(lancamento.getId(), lancamentoSalvo.getId());
		Assertions.assertEquals(lancamento.getStatus(), lancamentoSalvo.getStatus());
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroValidacao() {
		//cenário
		Lancamento lancamentoSalvar = criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(this.service).salvar(lancamentoSalvar);
		
		//Execução e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> this.service.salvar(lancamentoSalvar));
		
		Mockito.verify(this.repository, Mockito.never()).save(lancamentoSalvar);
	}
	
	@Test
	public void devaAtualizarLancamento() {
		//cenário
		Lancamento lancamentoSalvo = this.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.EFETIVADO);
		
		Mockito.doNothing().when(this.service).validar(lancamentoSalvo);
		

		Mockito.when(this.repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//Execução
		this.service.salvar(lancamentoSalvo);
		
		//Verificação
		Mockito.verify(this.repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamentoSalvar = criarLancamento();
		
		//Execução e verificação
		Assertions.assertThrows(NullPointerException.class, () -> this.service.atualizar(lancamentoSalvar));
		
		Mockito.verify(this.service, Mockito.never()).validar(lancamentoSalvar);
		Mockito.verify(this.repository, Mockito.never()).save(lancamentoSalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		
		//Execução
		this.service.deletar(lancamento);
		
		//Verificação
		Mockito.verify(this.service).deletar(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = criarLancamento();

		Assertions.assertThrows(NullPointerException.class, () -> this.service.deletar(lancamento));
		
		//Verificação
		Mockito.verify(this.repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamento() {
		//cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(this.repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//Execução
		List<Lancamento> resultado = this.service.buscar(lancamento);
		
		//Verificação
		org.assertj.core.api.Assertions.assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
	}
	
	@Test
	public void deveAutualizarParaNovoStatus() {
		//cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(this.service).atualizar(lancamento);
		
		//Execução
		this.service.atualizarStatus(lancamento, novoStatus);
		
		//verificação
		org.assertj.core.api.Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(this.service).atualizar(lancamento);
	}
	
	@Test
	public void deveRetornarUmaLancamentoPorId() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(this.repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//Execução
		Optional<Lancamento> resultado = this.service.obterPorId(id);
		
		//Verificação
		org.assertj.core.api.Assertions.assertThat(resultado.isPresent()).isTrue();
		org.assertj.core.api.Assertions.assertThat(resultado.get()).isEqualTo(lancamento);
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExistir() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = criarLancamento();
		
		Mockito.when(this.repository.findById(id)).thenReturn(Optional.empty());
		
		//Execução
		Optional<Lancamento> resultado = this.service.obterPorId(id);
		
		//Verificação
		org.assertj.core.api.Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarLancamento() {
		//cenário
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = org.assertj.core.api.Assertions.catchThrowable(() -> this.service.validar(lancamento));
		org.assertj.core.api.Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida");
		
		lancamento.setDescricao("");
		
		erro = org.assertj.core.api.Assertions.catchThrowable(() -> this.service.validar(lancamento));
		org.assertj.core.api.Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida");
		
		lancamento.setDescricao("descrição");
		
		erro =  org.assertj.core.api.Assertions.catchThrowable(() -> this.service.validar(lancamento));
		org.assertj.core.api.Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido");
		
		lancamento.setMes(0);

		erro =  org.assertj.core.api.Assertions.catchThrowable(() -> this.service.validar(lancamento));
		org.assertj.core.api.Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido");
		
		lancamento.setMes(13);
		
		erro =  org.assertj.core.api.Assertions.catchThrowable(() -> this.service.validar(lancamento));
		org.assertj.core.api.Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido");
		
		lancamento.setMes(2);
		
		erro =  org.assertj.core.api.Assertions.catchThrowable(() -> this.service.validar(lancamento));
		org.assertj.core.api.Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido");
		
		lancamento.setAno(365);
		
		erro =  org.assertj.core.api.Assertions.catchThrowable(() -> this.service.validar(lancamento));
		org.assertj.core.api.Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido");
		
	}
	

	
	public Lancamento criarLancamento() {
		return Lancamento.builder().descricao("descricao")
					.mes(2)
					.ano(2020)
					.tipo(TipoLancamento.RECEITA)
					.status(StatusLancamento.EFETIVADO)
					.dataCadastro(LocalDate.now())
					.valor(BigDecimal.valueOf(1000))
					.build();
	}
	
}
