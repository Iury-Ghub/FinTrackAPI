package org.example.repository;

import org.example.model.Categoria;
import org.example.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    List<Categoria> findByUsuario(Usuario usuario);
}
