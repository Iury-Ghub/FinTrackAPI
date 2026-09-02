package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.CategoriaDTO;
import org.example.model.Usuario;
import org.example.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorias", description = "Endpoints para gerenciar as categorias de transação do usuário")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Cria uma nova categoria", description = "Cria uma categoria associada ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @PostMapping
    public ResponseEntity<CategoriaDTO> salvar(@RequestBody CategoriaDTO categoriaDTO, @AuthenticationPrincipal Usuario usuario) {
        CategoriaDTO salva = categoriaService.salvar(categoriaDTO, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @Operation(summary = "Lista as categorias do usuário", description = "Retorna todas as categorias associadas ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @GetMapping
    public List<CategoriaDTO> listar(@AuthenticationPrincipal Usuario usuario) {
        return categoriaService.listar(usuario);
    }

    @Operation(summary = "Atualiza uma categoria", description = "Atualiza os dados de uma categoria existente, desde que pertença ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada ou não pertence ao usuário autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@Parameter(description = "Id da categoria a ser atualizada") @PathVariable Integer id,
                                                   @RequestBody CategoriaDTO categoriaDTO,
                                                   @AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(categoriaService.atualizar(id, categoriaDTO, usuario));
    }

    @Operation(summary = "Remove uma categoria", description = "Remove uma categoria existente, desde que pertença ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada ou não pertence ao usuário autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Id da categoria a ser removida") @PathVariable Integer id,
                                         @AuthenticationPrincipal Usuario usuario){
        categoriaService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
