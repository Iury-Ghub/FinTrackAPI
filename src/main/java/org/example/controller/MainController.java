package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.TipoTransacao;
import org.example.Transacao;
import org.example.TransacaoDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MainController {
    @FXML
    private Label txtSaldo;
    @FXML
    private TableView<Transacao> tabelaTransacoes;

    @FXML
    private TableColumn<Transacao, String> colDescricao;
    @FXML
    private TableColumn<Transacao, BigDecimal> colValor;
    @FXML
    private TableColumn<Transacao, Object> colTipo;
    @FXML
    private TableColumn<Transacao, Object> colData;

    @FXML
    private TextField txtDescricao;
    @FXML
    private TextField txtValor;
    @FXML
    private ChoiceBox<TipoTransacao> choiceTipo;
    @FXML
    private DatePicker dateData;

    private TransacaoDAO transacaoDAO;

    public void initialize(){
        transacaoDAO = new TransacaoDAO();

        choiceTipo.setItems(FXCollections.observableArrayList(TipoTransacao.values()));
        choiceTipo.setValue(TipoTransacao.RECEITA);

        atualizarSaldo();

        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

        refreshTable();
        dateData.setValue(LocalDate.now());
    }

    @FXML
    private void adicionarTransacao() {
        try {
            String descricao = txtDescricao.getText().trim();
            if (descricao.isEmpty()) {
                showAlert("Descrição é obrigatória");
                return;
            }

            String valorStr = txtValor.getText().trim();
            if (valorStr.isEmpty()) {
                showAlert("Valor é obrigatório");
                return;
            }

            BigDecimal valor;
            try {
                valor = new BigDecimal(valorStr);
            } catch (NumberFormatException e) {
                showAlert("Valor inválido");
                return;
            }

            TipoTransacao tipo = choiceTipo.getValue();
            LocalDate data = dateData.getValue() != null ? dateData.getValue() : LocalDate.now();

            Transacao t = new Transacao(descricao, valor, tipo, data);
            transacaoDAO.inserir(t);

            txtDescricao.clear();
            txtValor.clear();
            choiceTipo.setValue(TipoTransacao.RECEITA);
            dateData.setValue(LocalDate.now());

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro ao adicionar transação: " + e.getMessage());
        }
    }

    @FXML
    private void removerTransacao() {
        Transacao selecionada = tabelaTransacoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            showAlert("Selecione uma transação para remover");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar remoção");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja remover a transação selecionada?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Integer id = selecionada.getId();
            if (id == null) {
                showAlert("Transação selecionada não possui id");
                return;
            }

            transacaoDAO.remover(id);
            refreshTable();
        }
    }

    private void refreshTable() {
        List<Transacao> listaDoBanco = transacaoDAO.listarTodos();
        ObservableList<Transacao> listaObservable = FXCollections.observableArrayList(listaDoBanco);
        tabelaTransacoes.setItems(listaObservable);
        atualizarSaldo();
    }

    private void atualizarSaldo() {
        BigDecimal saldo = transacaoDAO.calcularSaldo();
        txtSaldo.setText("Saldo Atual: R$ " + saldo.toString());
    }

    private void showAlert(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("FinTrack");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}