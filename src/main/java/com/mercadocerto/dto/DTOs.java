package com.mercadocerto.dto;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
 
public class DTOs {
 
    public record PrecoMercadoDTO(
        Integer idMercado,
        String  nomeMercado,
        BigDecimal valor,
        LocalDateTime dataHora
    ) {}
 
    public record ComparadorDTO(
        Integer idProduto,
        String  nomeProduto,
        List<PrecoMercadoDTO> precos
    ) {}
 
    public record ItemOtimizadoDTO(
        Integer idProduto,
        String  nomeProduto,
        Integer idMercado,
        String  nomeMercado,
        BigDecimal valor,
        Integer quantidade
    ) {}
 
    public record PlanoMercadoDTO(
        Integer idMercado,
        String  nomeMercado,
        Double  latitude,
        Double  longitude,
        List<ItemOtimizadoDTO> itens,
        BigDecimal subtotal
    ) {}
 
    public record ResultadoOtimizadorDTO(
        Integer idLista,
        String  nomeLista,
        List<PlanoMercadoDTO> planos,
        BigDecimal totalOtimizado,
        BigDecimal totalMercadoMaisBarato,
        BigDecimal economia
    ) {}
 
    public record LoginResponseDTO(
        Integer idUsuario,
        String  nomeUsuario,
        String  email,
        String  login,
        String  tipoConta
    ) {}
 
    public record CadastroUsuarioDTO(
        String nomeUsuario,
        String email,
        String login,
        String senha
    ) {}
 
    public record CadastroComercioDTO(
        String nomeFantasia,
        String email,
        String senha,
        String cnpj
    ) {}
 
    public record ReputacaoMensalDTO(
        String mes,
        Double mediaMes,
        Long   totalAvaliacoes
    ) {}
 
    public record DashboardComercioDTO(
        Double mediaGeral,
        Long   totalAvaliacoes,
        Integer totalProdutos,
        List<ReputacaoMensalDTO> evolucaoMensal,
        List<Map<String, Object>> avaliacoesRecentes
    ) {}
 
    public record ItemListaRequestDTO(
        Integer idProduto,
        Integer quantidade
    ) {}
}