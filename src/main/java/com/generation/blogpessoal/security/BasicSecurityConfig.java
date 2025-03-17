package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * A Classe BasicSecurityConfig é responsável por sobrescrever a configuração padrão da 
 * Spring Security e definir como ela irá funcionar. Nesta Classe vamos definir quais 
 * serão as formas de autenticação, quais endpoints serão protegidos pelo Token JWT, 
 * entre outras configurações. Depois da implementação desta Classe, aquela tela de 
 * login, que apareceu no Navegador vai ser desativada e uma nova configuração será 
 * definida como padrão.
 * 
 * @Configuration: indica que a Classe é do tipo configuração, ou seja, 
 * define uma Classe como fonte de definições de Beans, além de ser uma das 
 * anotações essenciais ao utilizar uma configuração baseada em Java.
 * 
 * @EnableWebSecurity: habilita a segurança de forma Global (toda a aplicação) e 
 * sobrescreve os Métodos que irão redefinir as regras de Segurança da sua aplicação.
 */

@Configuration
@EnableWebSecurity
public class BasicSecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    @Bean
    UserDetailsService userDetailsService() {

        return new UserDetailsServiceImpl();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
    	/*
    	 * Linhas 38 e 39: Foi implementado o Método authenticationProvider, que retornará 
    	 * uma instância da Classe AuthenticationProvider, informando o Método de autenticação que será utilizado.
    	 * 
    	 * Linha 40: Cria um Objeto da Classe DaoAuthenticationProvider, chamado authenticationProvider. 
    	 * A Classe DaoAuthenticationProvider é utilizada para autenticar um Objeto da Classe Usuario 
    	 * através do usuario (e-mail) e a senha, validando os dados no Banco de dados de aplicação, 
    	 * através da Classe UserDetailsServiceImpl.
    	 * 
    	 * Linha 40: Cria um Objeto da Classe DaoAuthenticationProvider, chamado authenticationProvider. 
    	 * A Classe DaoAuthenticationProvider é utilizada para autenticar um Objeto da Classe Usuario 
    	 * através do usuario (e-mail) e a senha, validando os dados no Banco de dados de aplicação, 
    	 * através da Classe UserDetailsServiceImpl.
    	 * 
    	 * Linha 41: Adiciona um Objeto da Classe UserDetailsServiceImpl através do Método 
    	 * setUserDetailsService(), que será utilizado para validar o usuario (e-mail) do Objeto da Classe Usuario.
    	 * 
    	 * Linha 42: Adiciona um Objeto da Classe PasswordEncoder através do Método 
    	 * setPasswordEncoder(), que será utilizado para validar a senha do Usuário.
    	 * 
    	 * 
    	 */
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    	http
	        .sessionManagement(management -> management
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        		.csrf(csrf -> csrf.disable())
	        		.cors(withDefaults());

    	http
	        .authorizeHttpRequests((auth) -> auth
	                .requestMatchers("/usuarios/logar").permitAll()
	                .requestMatchers("/usuarios/cadastrar").permitAll()
	                .requestMatchers("/error/**").permitAll()
	                .requestMatchers(HttpMethod.OPTIONS).permitAll()
	                .anyRequest().authenticated())
	        .authenticationProvider(authenticationProvider())
	        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
	        .httpBasic(withDefaults());

		return http.build();

    }

}