package br.com.mecaniQA.api.repository;

import java.util.List;
import java.util.Optional;


public interface IRepository<T, ID> {

    T salvar(T entity);

    Optional<T> buscarPorId(ID id);

    List<T> buscarTodos();

    Optional<T> atualizar(ID id, T entity);

    boolean deletar(ID id);
}