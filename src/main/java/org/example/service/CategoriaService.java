package org.example.service;

import org.example.dto.CategoriaDTO;
import org.example.exception.RecursoNaoEncontrado;
import org.example.model.Categoria;
import org.example.model.Usuario;
import org.example.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaDTO salvar(CategoriaDTO categoriaDTO, Usuario usuario) {
        Categoria categoria = new Categoria();
        categoria.setNome(categoriaDTO.getNome());
        categoria.setUsuario(usuario);
        categoriaRepository.save(categoria);
        return categoriaDTO;
    }

    public List<CategoriaDTO> listar(Usuario usuario) {
        return categoriaRepository.findByUsuario(usuario)
                .stream()
                .map(c -> new CategoriaDTO(c.getId(), c.getNome()))
                .toList();
    }
    public CategoriaDTO atualizar(Integer id, CategoriaDTO categoriaDTO, Usuario usuario){
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() ->new RecursoNaoEncontrado("Não foi possível encontrar a Categoria"));
        categoria.setNome(categoriaDTO.getNome());
        if(!categoria.getUsuario().getId().equals(usuario.getId())){
            throw new RecursoNaoEncontrado("Categoria não encontrada");
        }
        categoriaRepository.save(categoria);
        return categoriaDTO;
    }

    public void deletar(Integer id, Usuario usuario) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("Categoria não foi encontrada"));
        if(!categoria.getUsuario().getId().equals(usuario.getId())){
            throw new RecursoNaoEncontrado("Categoria não encontrada");
        }
        categoriaRepository.delete(categoria);
    }
}
