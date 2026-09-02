package org.example.service;

import org.example.dto.CategoriaDTO;
import org.example.dto.SaldoDTO;
import org.example.dto.TransacaoDTO;
import org.example.exception.RecursoNaoEncontrado;
import org.example.model.Categoria;
import org.example.model.TipoTransacao;
import org.example.model.Transacao;
import org.example.model.Usuario;
import org.example.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {
    @Autowired
    private TransacaoRepository transacaoRepository;

    public TransacaoDTO salvar(TransacaoDTO transacaoDTO, Usuario usuarioLogado) {
            Transacao transacao = new Transacao();
            Categoria categoriaEntidade = new Categoria();
            categoriaEntidade.setId(transacaoDTO.getCategoria().getId());
            transacao.setCategoria(categoriaEntidade);
            transacao.setData(transacaoDTO.getData());
            transacao.setDescricao(transacaoDTO.getDescricao());
            transacao.setTipo(transacaoDTO.getTipo());
            transacao.setValor(transacaoDTO.getValor());
            transacao.setUsuario(usuarioLogado);
            transacaoRepository.save(transacao);
            return transacaoDTO;
    }
    public List<TransacaoDTO> listar(Usuario usuarioLogado) {
        return transacaoRepository.findByUsuario(usuarioLogado)
                .stream()
                .map(x -> new TransacaoDTO(
                        x.getId(),
                        x.getDescricao(),
                        x.getValor(),
                        x.getTipo(),
                        x.getData(),
                        new CategoriaDTO(x.getCategoria().getId(), x.getCategoria().getNome())
                ))
                .toList();
    }

    public TransacaoDTO atualizar(Integer id, TransacaoDTO transacaoDTO, Usuario usuario){
        Transacao transacao = transacaoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("Transação não encontrada"));
        transacao.setValor(transacaoDTO.getValor());
        transacao.setTipo(transacaoDTO.getTipo());
        transacao.setDescricao(transacaoDTO.getDescricao());
        transacao.setData(transacaoDTO.getData());

        Categoria categoriaEntidade = new Categoria();
        categoriaEntidade.setId(transacaoDTO.getCategoria().getId());
        transacao.setCategoria(categoriaEntidade);

        if(!transacao.getUsuario().getId().equals(usuario.getId())){
            throw new RecursoNaoEncontrado("Transação não encontrada");
        }
        transacaoRepository.save(transacao);

        return transacaoDTO;
    }

    public void deletar(Integer id, Usuario usuario){
        Transacao transacao = transacaoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("Transação não encontrada"));
        if(!transacao.getUsuario().getId().equals(usuario.getId())){
            throw new RecursoNaoEncontrado("Transação não encontrada");
        }
        transacaoRepository.delete(transacao);
    }

    public SaldoDTO calcularSaldo(Usuario usuarioLogado) {
        BigDecimal receitas = transacaoRepository.sumValorByUsuarioAndTipo(usuarioLogado, TipoTransacao.RECEITA);
        BigDecimal despesas = transacaoRepository.sumValorByUsuarioAndTipo(usuarioLogado, TipoTransacao.DESPESA);
        BigDecimal saldo = receitas.subtract(despesas);
        return new SaldoDTO(saldo);
    }
}
