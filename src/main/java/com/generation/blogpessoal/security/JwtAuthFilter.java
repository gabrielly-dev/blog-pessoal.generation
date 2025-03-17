package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * O código de resposta de status de erro do cliente HTTP 403 Forbidden 
 * indica que o servidor entendeu o pedido, mas se recusa a autorizá-lo.
 * 
 * O código de resposta de status de erro do cliente HTTP 401 Unauthorized indica que a solicitação 
 * não foi aplicada porque não possui credenciais de autenticação válidas para o recurso de destino.
 */

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	//vai validar o token
    @Autowired
    private JwtService jwtService;

    //confere se o usuario existe ou nao
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /*
     * ele vai receber a requisição, a resposta da requisição e vai receber alguns filtros
     * ele vai pegar a requisição, quanto entrando e saindo, e vai peneirar, filtrar
     * ele é da classe abstrata OncePerRequestFilter, sendo um método do tipo protected e abstrato
     * Quando fazemos requisições que vão ir na body, esses filtros internos vão passar na FilterChain
     * 
     * PESQUISAR:
     * - Classe abstrata Once
     */
    
    /*
     * Ao adicionar a Dependência Spring Security, 15 Filtros de Servlet são habilitados por padrão
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
    		HttpServletResponse response, FilterChain filterChain) 
    				throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
    
        try{
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
            }
            /*
             * SecurityContextHolder
             */
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            	/*
            	 * Linha 48: Verifica se a variável username é diferente de nulo, ou seja, se foi 
            	 * encontrado o usuario (e-mail) no Payload do Token JWT e Checa o Security 
            	 * Context (Contexto de Segurança), através do Método getContext() da Classe 
            	 * SecurityContextHolder, que retornará o Contexto de Segurança atual, para 
            	 * verificar se o usuario não está autenticado na Security Context, através do 
            	 * Método getAuthentication() da Interface SecurityContext.
            	 */
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                /*
                 * 	A figura acima, ilustra o filtro UsernamePasswordAuthenticationFilter checando se a Requisição
                 *  possui os Atributos usuario (e-mail) e a senha. Se os dados estiverem corretos, o Filtro de Servlet 
                 *  autentica o usuário e libera o acesso ao sistema. Caso contrário, ele retorna o Status 401 - Unauthorized.
                 *  
                 *  Linha 49: Se a condição for verdadeira, inicia o processo de construção do Objeto da Classe UserDetails, 
                 *  que armazenará as informações do usuário autenticado, através da Classe UserDetailsServiceImpl, que 
                 *  checará se o usuário existe no Banco de dados.
                 */
                if (jwtService.validateToken(token, userDetails)) {
                	/*
                	 * Linhas 52 a 54: Se o Token JWT for validado, construiremos um novo 
                	 * objeto da Classe UsernamePasswordAuthenticationToken, chamado authToken, 
                	 * que será responsável por autenticar um usuário na Spring Security e 
                	 * definir um Objeto Authentication
                	 * 
                	 * userDetails = contem dados dos usuarios
                	 * credentials = senha do usuario, normalmente null pois o userDetails já vem a senha
                	 * authorities = autorizações do usuário
                	 */
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            
            }
            filterChain.doFilter(request, response);

        }catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
                | SignatureException | ResponseStatusException e){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
    }
}