package com.example.avaliacao_app

class Estoque {
    companion object {
        val listaProdutos = mutableListOf<Produto>()
    }

    fun adicionarProduto(produto: Produto) {
        listaProdutos.add(produto)
    }

    fun calcularValorTotalEstoque(): Double {
        return listaProdutos.sumOf { it.preco * it.quantidade }
    }

    fun calcularQuantidadeTotal(): Int {
        return listaProdutos.sumOf { it.quantidade }
    }
}