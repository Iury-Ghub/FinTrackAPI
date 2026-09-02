package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.SaldoDTO;
import org.example.dto.TransacaoDTO;

import org.example.model.Usuario;
import org.example.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transacoes")
@Tag(name = "Transações", description = "Endpoints para gerenciar as transações financeiras do usuário")
public class TransacaoController {
    @Autowired
    private TransacaoService transacaoService;

    @Operation(summary = "Cria uma nova transação", description = "Cria uma transação (receita ou despesa) associada ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transação criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Categoria informada não existe")
    })
    @PostMapping
    public ResponseEntity<TransacaoDTO> salvar(@RequestBody TransacaoDTO transacaoDTO, @AuthenticationPrincipal Usuario usuario) {
        TransacaoDTO salva = transacaoService.salvar(transacaoDTO, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @Operation(summary = "Lista as transações do usuário", description = "Retorna todas as transações associadas ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @GetMapping
    public List<TransacaoDTO> listar(@AuthenticationPrincipal Usuario usuario) {
        return transacaoService.listar(usuario);
    }

    @Operation(summary = "Atualiza uma transação", description = "Atualiza os dados de uma transação existente, desde que pertença ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada ou não pertence ao usuário autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransacaoDTO> atualizar(@Parameter(description = "Id da transação a ser atualizada") @PathVariable Integer id,
                                                   @RequestBody TransacaoDTO transacaoDTO,
                                                   @AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(transacaoService.atualizar(id, transacaoDTO, usuario));
    }

    @Operation(summary = "Remove uma transação", description = "Remove uma transação existente, desde que pertença ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transação removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada ou não pertence ao usuário autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Id da transação a ser removida") @PathVariable Integer id,
                                         @AuthenticationPrincipal Usuario usuario){
        transacaoService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consulta o saldo do usuário", description = "Calcula o saldo (receitas - despesas) do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo calculado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @GetMapping("/saldo")
    public SaldoDTO saldo(@AuthenticationPrincipal Usuario usuario) {
        return transacaoService.calcularSaldo(usuario);
    }
}
