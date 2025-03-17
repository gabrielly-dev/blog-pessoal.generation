package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/*
 * A Classe JwtService é responsável por criar e validar o Token JWT. O Token JWT será 
 * criado durante o processo de autenticação (login) do usuário e o mesmo será validado 
 * em todas as requisições HTTP enviadas para os endpoints protegidos, que serão 
 * definidos na Classe BasicSecurityConfig, que será implementada mais à frente.
 */

/*
 * Component é uma anotação genérica, para qualquer classe gerenciada pelo Spring
 * Controller e Service são "alias"/apelidos da Component,
 * Seu diferencial é que não é necessário instanciar objetos ou indicar um uso específico
 * (se é service, controller ou repository). Ela não se limita ao Spring
 */
@Component
public class JwtService {

	//ela é um atributo que vai armazenar uma chave de assinatura
	//uma constante, sendo uma sha 256 que será nossa chave de codificação do token
	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
	
	/*
	 * 	vai retornar um objeto do tipo key, uma chave, é um método que pertence a security e vai como função 
	 *	aplicar o decode da BASE64 na nossa secret
	 *	vamos pegar o método e retornar o método Key com a chave embaralhada pela BASE64
	 *	vai gerar a assinatura (Signature) do Token JWT codificada pelo Algoritmo HMAC SHA256
	 */
	
	
	private Key getSignKey() {
		/*
		 * foi criado um vetor, byte[], para receber o resultado da codificação em Base 64
		 * Foi criado com tipo byte pois durante o processo de codificação ele irá trabalhar 
		 * com bits (0, 1)
		 */
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	/*
	 * ele apenas abriu o pacote para que o extractClaim extraia
	 */
	private Claims extractAllClaims(String token) {
		/*
		 * ele retornará um objeto da interface Claims, não foi preciso instanciar pois utilizamos
		 * a anotação @Component
		 */
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				/*
				 * O método ".setSigningKey(getSignKey()).build()" verifica se a assinatura do Token JWT é válida
				 */
				.parseClaimsJws(token).getBody();
				/*
				 * Caso seja válida, o método ".parseClaimsJws(token).getBody();" da interface JwtParserBuilder
				 * extrai todas as claims
				 */
	}

	/*
	 * vai extrair claims, informações do token, trazendo todas as informações da classe claim e tudo relacionado a ela
	 * informações do token, quando expira, qual o usuario, etc
	 * criamos uma constante claim, pegando o token e extraindo todas as infos
	 * 
	 * receberá 2 parâmetros T. O primeiro é o tipo de entrada, em nosso exemplo ela receberá um 
	 * Objeto da Classe Claim e o segundo é o tipo de saída, ou seja, receberá o 
	 * Método get equivalente a claim que se deseja recuperar o valor
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
		/*
		 * Ele é do tipo genérico e o outro T vai definir o método com o tipo recebido
		 */
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
		/*
		 * O Método extractUsername(String token) recupera os dados da Claim sub, 
		 * onde se encontra o usuario (e-mail), através do Método 
		 * extractClaim(String token, Function< Claims, T > claimsResolver).
		 * 
		 * A Interface Funcional Function recebeu como entrada a Classe Claims e 
		 * na saída, receberá a execução do Método getSubject(), que está sendo 
		 * chamado através do operador de referência de métodos (::), que retorna 
		 * o valor da claim sub.
		 */
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
		/*
		 * O Método extractExpiration(String token) recupera os dados da Claim exp, onde 
		 * se encontra a data e o horário de expiração do Token JWT, através do Método 
		 * extractClaim(String token, Function< Claims, T > claimsResolver).
		 * 
		 * A Interface Funcional Function recebeu como entrada a Classe Claims e na saída, 
		 * receberá a execução do Método getExpiration(), que está sendo chamado através 
		 * do operador de referência de métodos (::), que retorna o valor da claim exp.
		 */
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
		/*
		 * O Método isTokenExpired(String token) recupera os dados da Claim exp, onde se 
		 * encontra a data e o horário de expiração do Token JWT, através do Método 
		 * extractExpiration(String token) e verifica através do Método before(), da 
		 * Classe Date, se o token está ou não expirado (fora da data e hora de validade). 
		 * Se a data e a hora do token for anterior a data e hora atual, o Token JWT 
		 * estará expirado, o Método retornará true e será necessário autenticar 
		 * novamente para gerar um novo Token JWT válido.
		 */
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		/*
		 * O Método retornará true se o usuario que foi extraído do token (claim sub), 
		 * for igual ao usuario autenticado (atributo username do Objeto da Classe UserDetails) 
		 * e o token não estiver expirado (!isTokenExpired(token)). Para checar o usuario, 
		 * foi utilizado método equals() da Classe String. Para checar se o token expirou, 
		 * foi utilizado o Método isTokenExpired(String token).
		 */
	}

	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(userName)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}

}
