package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Postagem;
/*
 * ele vai buscar o tipo de dado, que é a nossa model, 
 * sendo BIGINT, buscar no banco de dados os dados que foram 
 * criados através da classe model se orientando pelo ID
 */
public interface PostagemRepository extends JpaRepository<Postagem, Long> {
	public List<Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo") 	String titulo);
}
