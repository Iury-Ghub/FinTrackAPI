package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAOTest {
    static {
        // usar um arquivo temporário para o banco durante os testes — evita problemas de conexões em memória
        String tmp = System.getProperty("java.io.tmpdir");
        System.setProperty("JDBC_URL", "jdbc:sqlite:" + tmp + "/transacoes_test.db");
    }

    private TransacaoDAO transacaoDAO;

    @BeforeEach
    public void setUp(){
        // cria instância do DAO após configurar JDBC_URL
        transacaoDAO = new TransacaoDAO();

        try (Connection conn = Conexao.getConexao();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS transacoes;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        transacaoDAO.criarTabela();
    }

    @Test
    public void testInserirEListar(){
        Transacao transacao = new Transacao("Teste",new BigDecimal(10),TipoTransacao.RECEITA, LocalDate.now());
        transacaoDAO.inserir(transacao);
        List<Transacao> transacaos = transacaoDAO.listarTodos();
        Assertions.assertEquals(1,transacaos.size());
    }

    @Test
    public void testCalcularSaldo(){
        Transacao transacao1 = new Transacao("Teste",new BigDecimal(10),TipoTransacao.RECEITA, LocalDate.now());
        transacaoDAO.inserir(transacao1);
        Transacao transacao2 = new Transacao("Teste",new BigDecimal(9),TipoTransacao.DESPESA, LocalDate.now());
        transacaoDAO.inserir(transacao2);

        BigDecimal saldoObtido = transacaoDAO.calcularSaldo();
        BigDecimal saldoEsperado = new BigDecimal(1);

        Assertions.assertEquals(0, saldoEsperado.compareTo((saldoObtido)));
    }
    @Test
    public void testRemover() {
        Transacao transacao = new Transacao("Teste", new BigDecimal(10), TipoTransacao.RECEITA, LocalDate.now());
        transacaoDAO.inserir(transacao);

        transacaoDAO.remover(1);

        List<Transacao> transacaos = transacaoDAO.listarTodos();
        Assertions.assertEquals(0, transacaos.size());
    }

    @Test
    public void testAtualizar() {
        Transacao transacao = new Transacao("Teste", new BigDecimal(10), TipoTransacao.RECEITA, LocalDate.now());
        transacaoDAO.inserir(transacao);

        Transacao modificada = new Transacao("Modificada", new BigDecimal(1), TipoTransacao.RECEITA, LocalDate.now());
        transacaoDAO.atualizar(1, modificada);

        Assertions.assertEquals("Modificada", transacaoDAO.listarTodos().get(0).getDescricao());
    }
}
