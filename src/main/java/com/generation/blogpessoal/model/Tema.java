package com.generation.blogpessoal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity //vai indicar que é uma nova tabela no banco de dados, ele apenas cria
@Table(name = "tb_temas") //apenas renomeia, sem ela o entity criaria uma tabela com o mesmo nome da classe
public class Tema {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank (message = "A descrição é obrigatória!")
	private String descricao;
	
	/*
	 * Tema -> One / Postagem -> Many
	 * fetch -> lazy, forma em que vai trazer as informações do banco de dados para mim, traz os dados e enquanto está processando ela está entregando algo
	 * trazendo o necessário
	 * fetch -> eager = vai tentar carregar tudo de uma vez, vai trazer tudo
	 * 
	 * o eager, vai trazer todas as informações da tabela q ele ta sendo aplicado... 
	 * se eu to puxando um produto da categoria "informatica" ele iria me trazer na resposta, 
	 * todos os dados possiveis dessa tabela... no exemplo q eu tava usando, 
	 * mouse é um dos produtos da tabela "informatica"
	 * 
	 * cascade -> vai dizer como vai se comportar a tabela relacionada em momentos de deletar dados, quando apagar o tema "Carnaval", todas as
	 * postagens relacionadas a esse tema serão removidas
	 */
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "tema", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties("tema")
	private List<Postagem> postagem;
	/*
	 * tornamos a postagem uma lista pois ela será mais de uma postagem para um único tema
	 * por se tratar de mais uma postagem, devemos ter mais de uma postagem para o mesmo tema
	 * 
	 * 
	 */
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public List<Postagem> getPostagem() {
		return postagem;
	}
	public void setPostagem(List<Postagem> postagem) {
		this.postagem = postagem;
	}
	
	
}
