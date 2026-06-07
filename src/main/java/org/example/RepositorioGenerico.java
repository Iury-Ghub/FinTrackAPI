package org.example;

import java.util.List;

public interface RepositorioGenerico<T> {
    void inserir(T object);
    List<T> listarTodos();
    void remover(Integer id);
    void atualizar(Integer id, T object);
}
