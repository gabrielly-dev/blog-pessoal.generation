package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		/*
		 * o usuario tem todas as informações como nome, senha, etc
		 * verifica se está presente no banco de dados
		 */
		//SELECT * FROM tb_usuarios WHERE usuario = "Daniele@gmail.com"
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();
			/*
			 * retorna nada
			 * para o usuario, email, não pode permitir que duas pessoas tenham o mesmo email
			 * se for true de que está presente ele estará vazio e não vai prosseguir para as próximas linhas
			 * tratando apenas as regras de negócio e não resposta ao insomnia
			 */
		
		// setar a senha criptografada no banco de dados
		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		
		// guardar as informações dentro do optional, o usuario no banco de dados
		// optional DE, desse resultado tal
		return Optional.of(usuarioRepository.save(usuario));
	}
	
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		
		if(usuarioRepository.findById(usuario.getId()).isPresent()) {
			
			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
			
			if ( (buscaUsuario.isPresent()) && ( buscaUsuario.get().getId() != usuario.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
			/*
			 * buscar se ela está presente, e o id do usuário é diferente do id dele, impede de alguém usar o e-mail dela
			 */
			
			usuario.setSenha(criptografarSenha(usuario.getSenha()));
			
			return Optional.ofNullable(usuarioRepository.save(usuario));
		}
		
		return Optional.empty();
	}
	
	/*
	 * Quando trabalhamos com autenticação, recebemos um token, devido o Usuario não ter token, usamos o UsuarioLogin
	 * seria o LOGAR
	 */
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {
        
        // Gera o Objeto de autenticação, vai ter a instancia de outra classe
		/*
		 * vai pegar do usuario login o email e a senha, 
		 */
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha());
		
        // Autentica o Usuario
		/*
		 * Classe principal de autenticação de requisições, uma das, entregando as credenciais passadas anteriormente
		 * credenciais e authentication já conhece o email e senha
		 */
		Authentication authentication = authenticationManager.authenticate(credenciais);
        
        // Se a autenticação foi efetuada com sucesso
		/*
		 * 
		 */
		if (authentication.isAuthenticated()) {

            // Busca os dados do usuário, retornar o que vai achar no banco sobre o usuario, 
			Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

            // Se o usuário foi encontrado
			/*
			 * se passou toda a camada de validação, significa que ele tem o objeto usuarioLogin e retorna preenchido
			 */
			if (usuario.isPresent()) {

                // Preenche o Objeto usuarioLogin com os dados encontrados 
				//ERRO
				usuarioLogin.get().setId(usuario.get().getId());
                usuarioLogin.get().setNome(usuario.get().getNome());
                usuarioLogin.get().setFoto(usuario.get().getFoto());
                usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
                usuarioLogin.get().setSenha("");
				
                 // Retorna o Objeto preenchido
			   return usuarioLogin;
			
			}

        } 
            
		return Optional.empty();

    }

	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.encode(senha);

	}

	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}

}
