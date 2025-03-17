package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired 
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		//metodo sem retorno, void, de onde quero começar
		
		usuarioRepository.deleteAll();
		//antes de começar qualquer teste, ele iniciára vazio, para pegar um banco de dados puro, liso.
		
		usuarioService.cadastrarUsuario(new Usuario(null, "Root", "root@root.com", "rootroot", " "));
		//e deixa esse usuário padrão já criado
		//quanto mais vazio estiver melhor para o computador, pois consome memória RAM
		//para iniciar os testes, precisa de um token, então já garantimos isso criando um novo objeto
		
	}
	
	@Test
	@DisplayName("Cadastrar um usuário... ") 
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
					new Usuario(null, "Thiago", "thiago@email", "senha618", " ")
				);
		//Corrigir que aceite somente um nome, ou e-mail sem o ".", corrigir também que aceite o email apenas em número, como 26748@17374
		//senha está correta, não aceita menos que 8 caracteres
		//o campo foto aceita null
		// o campo senha não aceita null
		// o campo email não aceita null

		
		//quando passamos no lugar do id um "0L" significa que passamos a responsabilidade de criar um ID para o Spring
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange(
					"/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class
				);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(null, "Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(null, "Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));
		
		//por conta do e-mail, que aceita que seja com ponto ou sem, acaba criando um novo
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange
				("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario
				(new Usuario(null, "Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));
		
		Usuario usuarioUpdate = new Usuario
				(usuarioCadastrado.get().getId(), "Juliana Andrews", "andrews@email.com.br", "12345678", "-");
		//O erro aqui está em não conseguir mudar a senha, e isso não seria legal caso a pessoa quisesse trocar
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(null, "Gabrielly", "gabrielly@gmail.com", "7827435", "-"));
		
		usuarioService.cadastrarUsuario(new Usuario(null, "Marlene", "marlene@gmail.com", "12342345", "-"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		
	}
	
}
