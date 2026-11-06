package com.imobiliaria.frontend.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Imóvel como o frontend o enxerga: serve de objeto do formulário
 * e de linha da tabela. É só um objeto de dados (sem JPA).
 * O id é omitido do JSON quando nulo (cadastro novo).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Imovel {

    private Long id;
    private String descricao;
    private String endereco;
    private String tipo;
    private BigDecimal valor;
    // Já começa marcado no formulário
    private Boolean disponivel = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }
}
