package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(allowedHeaders = "*", origins = "*") //eu posso receber, sem essa linha, o front end não chega no back end
public class PostagemController {

	@Autowired 
	/*
	 * quando eu precisar de algo dessa repository, ela cria a instancia, roda o que precisa e 
	 * fecha a instancia, inversão de controle e injeção de dependencia, vai abrir e fechar 
	 * sozinho conforme a necessidade
	 */
	private PostagemRepository postagemRepository;
	
	@Autowired
	private TemaRepository temaRepository;
	
	
	@GetMapping
	public ResponseEntity<List<Postagem>> getAll() {
		/*
		 * ResponseRentity serve para nos enviar os dados em Json de maneira organizada
		 * quando eu fizer uma requisição de get, quero retornar do meu repository o finAll e ele vai no banco de dados
		 * e dar Select * From para devolver todos os dados e devolver em formato JSON.
		 * 
		 * ResponseEntity.ok é o cara responsável por criar o meu JSON, ele vai usar o postagemrepository que tem conexão com o banco de dados
		 * para ir no banco e buscar todas as postagens em formato JSON
		 */
		return ResponseEntity.ok(postagemRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id) {
		/*
		 * o{id} é uma variavel, então temos que informar que no caminho dele
		 * ele vai receber uma variavel do tipo Long, serve para denonimar 
		 * se tivesse sem a chave teria que ser só id, ser puro, 
		 * ELE VAI SABER QUE TEM QUE PESQUISAR NO BANCO PELO ID
		 */
		return postagemRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
		/*
		 * JPA FORNECE O findByID e a postagem tem que existir ou não
		 * caso encontre, vai fazr o mapeamento sendo o processo de pegar ele e mostrar na tela
		 * vai passar o resposta e retornar uma resposta nova com OK
		 * SE NÃO vai retornar um status de NÃO ENCONTRADO invés de nulo
		 * 
		 * foi criado o resposta e ele se assemelha ao:
		 * resposta = postagemRepository.finByID(id)
		 * mas com lambda ele já cria uma variável dentro de map que referencia ao findById
		 */
	}
	
	
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo) {
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	/*
	 * Criamos um método público, que recebe a anotação postmapping, ele recebe a entidade ResponseEntity do tipo Postagem denominado post
	 * ele vai checar se o parametro que chegar vai ser valido(seguir as regras que colocamos na model
	 *  e que tenha corpo, seguindo o padrão dos atributos na classe postagem
	 * ele vai retornar o status 201 e no corpo da requisição a criação que fizemos
	 */
	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {
		//@Valid -> validações feita na model como size, not blank, etc
		//@RequestBody -> indicando que teremos um corpo para requisição
		if (temaRepository.existsById(postagem.getTema().getId()))
			return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
		
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
				
		/*
		 * botãozinho - status -> statuscode 201
		 * JPA está sendo extendida pelo Repository
		 */
	}
	
	/*
	 * Criamos um método público, que recebe o ResponseEntity do tipo Postagem, chamado put, 
	 * ele deve ser válido(seguir as regras que colocamos nos atributos da model Postagem)
	 * e ter como obrigatório um corpo para preenchermos e enviarmos
	 * ele vai retornar um método do repository, procurando pelo ID que tem que ser igual ao atributo id da classe Postagem
	 * ele vai mapear por todo o conteúdo que preenchermos na Body e retornar um status OK caso funcione, retornando
	 * no body a alteração realizada
	 * se não funcionar ele vai retornar NOT FOUND 
	 * não sei porque do build.
	 */
	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
		if(postagemRepository.existsById(postagem.getId())) {
			
			if(temaRepository.existsById(postagem.getTema().getId()))
				return ResponseEntity.status(HttpStatus.OK)
						.body(postagemRepository.save(postagem));
		
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	/*
	 * quando formos usar o deletar, 
	 * vai retornar o status: sem conteúdo
	 * caso delete, vai aparecer a mensagem "No Content"
	 * 
	 * nesse caso é um método delete que pode ser somente @DeleteMapping("/{id}") pois não existe outro delete do mesmo formato
	 * ele vai receber a anotação ResponseStatus "sem conteúdo" para se caso dê certo, mas não entendi por que está acima e não no meio do código se é necessária a validação primeira
	 * Ele deve ser um Optional para evitar o NullPointerException, e o optional vai procurar pelo id 
	 * se caso for vazio, vai gerar uma exception NOT FOUND, e por ser exception, programa se encerrará
	 * caso não seja vazia, não entrará no if, então será deletado através do método repository deletando pelo id
	 * 
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		//tratando e prevenindo a exception NullPointerException
		Optional<Postagem> postagem = postagemRepository.findById(id);
		if(postagem.isEmpty()) //confirmando que é vazio
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);//vai criar um response status que vai gerar uma exception e retornar not found
		postagemRepository.deleteById(id);
	}
}
