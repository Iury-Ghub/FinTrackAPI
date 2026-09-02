package org.example;

import org.example.model.Transacao;
import org.example.model.TipoTransacao;
import org.example.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@DataJpaTest
public class TransacaoRepositoryTest {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Test
    public void testInserirEListar() {
        transacaoRepository.save(new Transacao(null, "Teste", new BigDecimal(10), TipoTransacao.RECEITA, LocalDate.now(), null, null));

        List<Transacao> transacoes = transacaoRepository.findAll();
        Assertions.assertEquals(1, transacoes.size());
    }

    @Test
    public void testCalcularSaldo() {
        transacaoRepository.save(new Transacao(null, "Teste", new BigDecimal(10), TipoTransacao.RECEITA, LocalDate.now(), null, null));
        transacaoRepository.save(new Transacao(null, "Teste", new BigDecimal(9), TipoTransacao.DESPESA, LocalDate.now(), null, null));

        BigDecimal saldoObtido = BigDecimal.ZERO;
        for (Transacao transacao : transacaoRepository.findAll()) {
            saldoObtido = transacao.getTipo() == TipoTransacao.RECEITA
                    ? saldoObtido.add(transacao.getValor())
                    : saldoObtido.subtract(transacao.getValor());
        }
        BigDecimal saldoEsperado = new BigDecimal(1);

        Assertions.assertEquals(0, saldoEsperado.compareTo(saldoObtido));
    }

    @Test
    public void testRemover() {
        Transacao transacao = transacaoRepository.save(new Transacao(null, "Teste", new BigDecimal(10), TipoTransacao.RECEITA, LocalDate.now(), null, null));

        transacaoRepository.deleteById(transacao.getId());

        Assertions.assertEquals(0, transacaoRepository.findAll().size());
    }

    @Test
    public void testAtualizar() {
        Transacao transacao = transacaoRepository.save(new Transacao(null, "Teste", new BigDecimal(10), TipoTransacao.RECEITA, LocalDate.now(), null, null));

        transacao.setDescricao("Modificada");
        transacaoRepository.save(transacao);

        Assertions.assertEquals("Modificada", transacaoRepository.findById(transacao.getId()).orElseThrow().getDescricao());
    }
}
