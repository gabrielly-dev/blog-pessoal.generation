package com.generation.blogpessoal.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;

/*
 * A Classe UserDetailsServiceImpl é uma implementação da Interface UserDetailsService, 
 * responsável por validar a existência de um usuário no sistema através do Banco de 
 * dados e retornar um Objeto da Classe UserDetailsImpl (implementada no passo anterior), 
 * com os dados do Objeto encontrado no Banco de dados. A busca será feita através do 
 * atributo usuario (e-mail).
 * 
 * Vale lembrar que para isso é necessário que ao persistir o usuário no Banco de dados, 
 * a senha deve estar obrigatoriamente criptografada, como veremos na implementação da 
 * Classe UsuarioService, utilizando um algoritmo hash de criptografia, e o usuario 
 * (e-mail) deve ser único no sistema, ou seja, não podem existir 2 usuários com o mesmo e-mail.
 * 
 * ATENÇÃO: Gravar a senha criptografada no Banco de dados não é opcional, é obrigatório por lei. 
 * A LGPD (Lei Geral de Proteção de Dados) determina que apenas o próprio usuário pode conhecer a sua senha.
 */

@Service
/*
 * @Service, o que indica que esta Classe é uma Classe de Serviço. Classe de Serviço é 
 * uma Classe responsável por implementar as regra de negócio e as tratativa de dados 
 * de uma parte do ou recurso do sistema.
 */
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	/*
	 * se conecta com o banco de dados e realiza a manipulação dos dados 
	 * dentro do banco de dados
	 */
	
	//pegar o usuario que foi passado, carregar o Usuário pelo Username 
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		/*
		 * criar o optional, que recebe um usuario e que vai selecionar todas as informações onde o usuário seja igual ao
		 * usuário passado pela pessoa
		 */
		
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(userName);


		if (usuario.isPresent())
			/*
			 * Caso o usuário exista no banco, ele cria um novo objeto/registro para o UserDetailsImpl
			 * para que haja a conexão entre o nosso banco de dados e a Spring Security
			 * fornecendo as informações básicas do usuário para o Spring Security
			 */
			return new UserDetailsImpl(usuario.get());
		else
			/*
			 * Casp o usuário não exista no nosso banco de dados, vai retornar um Status Forbidden 
			 * que indica que o servidor entendeu o pedido, mas se recusa a autorizá-lo.
			 */
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			
	}
}