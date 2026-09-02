package org.example.repository;

import org.example.model.TipoTransacao;
import org.example.model.Transacao;
import org.example.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    List<Transacao> findByUsuario(Usuario usuario);
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.usuario = :usuario AND t.tipo = :tipo")
    BigDecimal sumValorByUsuarioAndTipo(@Param("usuario") Usuario usuario, @Param("tipo") TipoTransacao tipo);
}