package org.example;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO implements RepositorioGenerico<Transacao>{
    public void criarTabela(){
        String sql = "CREATE TABLE IF NOT EXISTS transacoes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "descricao VARCHAR(100), " +
                "valor DECIMAL(10,2), " +
                "tipo VARCHAR(10), " +
                "data DATE);";

        try (Connection connection = Conexao.getConexao();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inserir(Transacao transacao){
        String sql = "INSERT INTO transacoes (descricao,valor,tipo,data) VALUES (?,?,?,?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = Conexao.getConexao();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,transacao.getDescricao());
            preparedStatement.setBigDecimal(2,transacao.getValor());
            preparedStatement.setString(3,transacao.getTipo().name());
            preparedStatement.setDate(4, Date.valueOf(transacao.getData()));

            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    transacao.setId(rs.getInt(1));
                }
            }

            connection.commit();
        }catch (SQLException e){
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                try { preparedStatement.close();
                } catch (SQLException ignore) {}
            }
            if (connection != null) {
                try { connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignore) {}
            }
        }

    }

    @Override
    public List<Transacao> listarTodos(){
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT * FROM transacoes";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = Conexao.getConexao();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String descricao = resultSet.getString("descricao");
                BigDecimal valor = resultSet.getBigDecimal("valor");
                TipoTransacao tipoTransacao = TipoTransacao.valueOf(resultSet.getString("tipo"));
                LocalDate data = resultSet.getObject("data", LocalDate.class);

                Transacao t = new Transacao(descricao,valor,tipoTransacao,data);
                t.setId(id);
                transacoes.add(t);
            }
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try { resultSet.close();
                } catch (SQLException ignore) {}
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
            } catch (SQLException ignore) {}
            }if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignore) {}
            }
        }

        return  transacoes;
    }

    @Override
    public void remover(Integer id) {
        String sql = "DELETE FROM transacoes WHERE id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = Conexao.getConexao();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try { connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignore) {}
            }
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignore) {}
            }
        }
    }

    @Override
    public void atualizar(Integer id, Transacao transacao) {
        String sql = "UPDATE transacoes SET descricao = ?, valor = ?, tipo = ?, data = ? WHERE id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = Conexao.getConexao();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,transacao.getDescricao());
            preparedStatement.setBigDecimal(2,transacao.getValor());
            preparedStatement.setString(3,transacao.getTipo().name());
            preparedStatement.setDate(4, Date.valueOf(transacao.getData()));
            preparedStatement.setInt(5,id);

            preparedStatement.executeUpdate();
            connection.commit();
        }catch (SQLException e){
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignore) {}
            }
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignore) {}
            }
        }
    }

    public BigDecimal calcularSaldo(){
        BigDecimal saldo = BigDecimal.ZERO;
        List<Transacao> todas = listarTodos();

        for(Transacao transacao : todas){
            if(transacao.getTipo() == TipoTransacao.RECEITA){
                saldo = saldo.add(transacao.getValor());
            }else{
                saldo = saldo.subtract(transacao.getValor());
            }
        }

        return saldo;
    }
}
