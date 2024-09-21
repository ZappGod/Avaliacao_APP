package com.example.avaliacao_app

object Estoque {
    private val _produtos = mutableListOf<Produto>()

    val produtos: List<Produto>
        get() = _produtos

    fun adicionarProduto(produto: Produto) {
        _produtos.add(produto)
    }

    fun calcularValorTotalEstoque(): Double {
        return _produtos.sumOf { it.preco * it.quantidade }
    }

    fun calcularQuantidadeTotal(): Int {
        return _produtos.sumOf { it.quantidade }
    }
}
