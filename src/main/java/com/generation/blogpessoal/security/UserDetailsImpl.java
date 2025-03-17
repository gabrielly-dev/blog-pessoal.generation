package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;


/*
 * A Classe UserDetailsImpl implementa a Interface UserDetails, que tem como principal funcionalidade fornecer as informações 
 * básicas do usuário para o 
 * Spring Security (Usuário, Senha, Direitos de acesso e as Restrições da conta).
 */
public class UserDetailsImpl implements UserDetails {
	/*
	 * Vai trazer quem é o usuário, a senha, e os direitos de acesso ou restrições dessa conta, 
	 * dentro da UserDetails é implementação da UserDetailsImplm
	 * Normalmente ele já é construído certo, nada é alterado
	 * Porém, ele serve para verificarmos ele no github e ver se a nossa versão local está desatualizada
	 * Podemos alterar quando fazemos alguma mudança 
	 */
	
	private static final long serialVersionUID = 1L;
	/*
	 * controle de versão para essa classe
	 * Na prática, esse número seria a versão da sua Classe. Uma nova versão de uma Classe 
	 * é criada sempre que você adicionar ou modificar um ou mais Atributos da Classe. 
	 * Essa regra não vale para Métodos, porque a Serialização só leva em consideração os Atributos.
	 */
	
	private String userName;
	private String password;
	private List<GrantedAuthority> authorities;
	// autorizações que o usuário tem, é uma lista pois pode ser uma série de autorizações
	
	public UserDetailsImpl(Usuario user) {
		this.userName = user.getUsuario();
		this.password = user.getSenha();
		/*
		 * A informação que vir do objeto user de Usuario será vinculado aos atributos dessa classe, 
		 * os dados preenchidos ou que constam no banco de dados serão atribuídas aos atributos
		 * Então o atributo usuario na classe Usuario vai ser acesso pelo userName, 
		 * o mesmo para a senha
		 */
	}

	public UserDetailsImpl() { 
		/*
		 * pode ser que a gente receba informações, como também pode ser que a gente não receba
		 */
	}
	
	/*
	 * Método está anotado com a anotação @Override, o que indica que este Método está 
	 * sendo Sobrescrito (Polimorfismo de Sobrescrita), ou seja, é um Método da Interface 
	 * UserDetails, que obrigatoriamente deve ser implementado
	 */

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		/*
		 * É uma lista que por não termos autorizações definidas, níveis de autorização, passamos ?
		 * 
		 */
		return authorities;
	}

	@Override
	public String getPassword() {

		return password;
	}

	@Override
	public String getUsername() {

		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
		/*
		 * Indica se o acesso do usuário expirou (tempo de acesso). 
		 * Uma conta expirada não pode ser autenticada (return false).
		 */
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
		/*
		 * Indica se o usuário está bloqueado ou desbloqueado. 
		 * Um usuário bloqueado não pode ser autenticado (return false).
		 */
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
		/*
		 * Indica se as credenciais do usuário (senha) expiraram (precisa ser trocada). 
		 * Senha expirada impede a autenticação (return false).
		 */
	}

	@Override
	public boolean isEnabled() {
		return true;
		/*
		 * Indica se o usuário está habilitado ou desabilitado. 
		 * Um usuário desabilitado não pode ser autenticado (return false).
		 */
	}

}
