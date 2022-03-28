package com.menezesdaniel.controlecontabil.api.resource;

import java.util.List;
import java.util.Optional;

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

import com.menezesdaniel.controlecontabil.api.dto.AtualizaStatusDto;
import com.menezesdaniel.controlecontabil.api.dto.LancamentoDto;
import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Lancamento;
import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;
import com.menezesdaniel.controlecontabil.model.enums.TipoLancamento;
import com.menezesdaniel.controlecontabil.service.LancamentoService;
import com.menezesdaniel.controlecontabil.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

		//declaracao para o construtor da classe
	private LancamentoService service;
	private UsuarioService usuarioService;

		//construtor da classe
	public LancamentoResource(LancamentoService service, 
								UsuarioService usuarioService) {
		this.service = service;
		this.usuarioService = usuarioService;
	}


	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value="descricao", required = false) String descricao,
			@RequestParam(value="mes", required = false) Integer mes,
			@RequestParam(value="ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario
				//os parametros foram colocados individualmente para que todos sejam opcionais, exceto o parametro usuario
			) {
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);

		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o ID informado!");			
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}

		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);

	}
	
	@GetMapping("{id}")
	public ResponseEntity obterLancamento ( @PathVariable("id") Long id ) {
		return service.obterPorId(id)
				.map( lancamento -> new ResponseEntity( converter (lancamento), HttpStatus.OK) )
				.orElseGet( () -> new ResponseEntity(HttpStatus.NOT_FOUND) );		
	}

		//url para salvar, sera na propria raiz da API
	@PostMapping
	public ResponseEntity salvar( @RequestBody LancamentoDto dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);			
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}


	@PutMapping("{id}") //o put recebera o nr de id na url, o qual sera atualizado
	public ResponseEntity atualizar (@PathVariable("id") Long id, @RequestBody LancamentoDto dto ) {
			//a url tera final /id, conforme descrito no PutMapping
				//a variavel id, recebido pelo put sera a entrada, conforme descrito no PathVariable
		
		return service.obterPorId(id).map(entity ->{
				//busca o id recebido no BD
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
					//salva o lancamento no BD
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}		//orElseGet caso a funcao map nao encontre um lancamento com o id informado


	@PutMapping("{id}/atualiza-status") //o put recebera o nr de id na url o qual sera atualizado,
		//seguido de atualiza-status, de modo a diferenciar da funcao atualizar lancamento
	public ResponseEntity atualizarStatus (@PathVariable("id") Long id, @RequestBody AtualizaStatusDto dto ) {
		
		return service.obterPorId(id).map(entity ->{
				//verifica o status recebido via DTO
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			
			if(statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido!");
					//erro caso o status recebido não esteja dentre aqueles enumerados
			}
			try {
				entity.setStatus(statusSelecionado);
					//atualiza com o novo status recebido
				service.atualizar(entity);
					//salva o lancamento no BD
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}		//orElseGet caso a funcao map nao encontre um lancamento com o id informado


	@DeleteMapping("{id}")
	public ResponseEntity deletar (@PathVariable("id") Long id) {
		
		return service.obterPorId(id).map(entidade ->{
			service.deletar(entidade);
			return new ResponseEntity( HttpStatus.NO_CONTENT );
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}	
		
		//converte os atributos do lancamento recebido do BD para o tipo DTO
	private LancamentoDto converter (Lancamento lancamento) {
		
		return LancamentoDto.builder()
				.id(lancamento.getId())
				.descricao(lancamento.getDescricao())
				.valor(lancamento.getValor())
				.mes(lancamento.getMes())
				.ano(lancamento.getAno())
				.status(lancamento.getStatus().name())
				.tipo(lancamento.getTipo().name())
				.usuario(lancamento.getUsuario().getId())
				.build();
	}

		//converte todos os atributos do lancamento recebido no DTO para a classe lancamento
	private Lancamento converter (LancamentoDto dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());

		Usuario usuario = usuarioService
				.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID informado!"));

		lancamento.setUsuario(usuario);

		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}

		if(dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}

		return lancamento;
	}


}
