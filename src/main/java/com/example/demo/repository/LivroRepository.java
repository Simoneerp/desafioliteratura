package com.example.demo.repository;

import com.example.demo.model.Livro;
import com.example.demo.repository.LivroRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;




@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    /**
     * Busca um livro pelo título (case-insensitive)
     */
    @Query("SELECT l FROM Livro l WHERE UPPER(l.titulo) = UPPER(:titulo)")
    Optional<Livro> findByTituloIgnoreCase(@Param("titulo") String titulo);

    /**
     * Busca livros por idioma (case-insensitive)
     */
    @Query("SELECT l FROM Livro l WHERE UPPER(l.idioma) = UPPER(:idioma) ORDER BY l.titulo")
    List<Livro> findByIdiomaIgnoreCase(@Param("idioma") String idioma);

    /**
     * Busca livros por autor
     */
    @Query("SELECT l FROM Livro l WHERE l.autor.id = :autorId ORDER BY l.titulo")
    List<Livro> findByAutorId(@Param("autorId") Long autorId);

    Iterable<Object> findByIdioma(String idioma);
}