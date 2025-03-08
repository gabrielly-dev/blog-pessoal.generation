package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//Vai refletir no banco de dados como uma nova entidade/nova tabela
@Entity
@Table(name = "tb_postagens")
public class Postagem {
//CTRL + SHIFT + O importa todas as bibliotecas que mencionamos
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "É obrigatório que o título seja preenchido!")
	@Size(min = 5, max = 100, message = "O título tem que ser entre 5 a 100 caracteres.")
	private String titulo;
	
	@NotBlank(message = "É obrigatório que o texto seja preenchido!")
	@Size(min = 5, max = 1000, message = "O texto tem que ser entre 5 a 1000 caracteres.")
	private String texto;
	
	@UpdateTimestamp
	private LocalDateTime data;
	
	@ManyToOne 
	/*
	 * indicando que o objeto tema vai ter um comportamento no seu relacionamento, a classe que estou é Many
	 * e a classe que vai se referenciar é o One
	 * classe postagem é muitos
	 * classe tema é um
	 */
	@JsonIgnoreProperties("postagem")
	/*
	 * vai ignorar propriedades da postagem, na classe tema
	 * fazendo com que não se forme um ciclo infinito de postagem se referenciar a tema e tema se referenciar a postagem
	 */
	private Tema tema;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}
	
	
	
}
