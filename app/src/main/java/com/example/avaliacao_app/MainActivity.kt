package com.example.inventarioapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.avaliacao_app.Estoque
import com.example.avaliacao_app.Produto
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventarioApp()
        }
    }
}

@Composable
fun InventarioApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "telaCadastro") {
        composable("telaCadastro") { TelaCadastroProduto(navController) }
        composable("telaLista") { TelaListaProdutos(navController) }
        composable("telaDetalhes/{produtoIndex}") { backStackEntry ->
            val produtoIndex = backStackEntry.arguments?.getString("produtoIndex")?.toInt() ?: 0
            TelaDetalhesProduto(produtoIndex, navController)
        }
        composable("telaEstatisticas") { TelaEstatisticas() }
    }
}

@Composable
fun TelaCadastroProduto(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cadastro de Produto", fontSize = 22.sp)

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") }
        )
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") }
        )
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade em Estoque") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (nome.isNotBlank() && categoria.isNotBlank() && preco.isNotBlank() && quantidade.isNotBlank()) {
                    val precoDouble = preco.toDoubleOrNull()
                    val quantidadeInt = quantidade.toIntOrNull()

                    if (precoDouble != null && quantidadeInt != null && quantidadeInt > 0 && precoDouble >= 0) {
                        val produto = Produto(nome, categoria, precoDouble, quantidadeInt)
                        Estoque().adicionarProduto(produto)
                        Toast.makeText(context, "Produto cadastrado!", Toast.LENGTH_SHORT).show()

                        // Limpar campos após o cadastro
                        nome = ""
                        categoria = ""
                        preco = ""
                        quantidade = ""

                        // Navegar para a lista de produtos
                        navController.navigate("telaLista")
                    } else {
                        Toast.makeText(context, "Preço ou quantidade inválidos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }
    }
}

@Composable
fun TelaListaProdutos(navController: NavController) {
    val produtos = Estoque.listaProdutos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Lista de Produtos", fontSize = 22.sp)

        LazyColumn {
            items(produtos.size) { index ->
                val produto = produtos[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${produto.nome} (${produto.quantidade} unidades)")
                    Button(onClick = {
                        navController.navigate("telaDetalhes/$index")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate("telaEstatisticas")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Estatísticas")
        }
    }
}

@Composable
fun TelaDetalhesProduto(produtoIndex: Int, navController: NavController) {
    val produto = Estoque.listaProdutos[produtoIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Detalhes do Produto", fontSize = 22.sp)

        Text(text = "Nome: ${produto.nome}")
        Text(text = "Categoria: ${produto.categoria}")
        Text(text = "Preço: R$ ${produto.preco}")
        Text(text = "Quantidade: ${produto.quantidade} unidades")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

@Composable
fun TelaEstatisticas() {
    val valorTotalEstoque = Estoque().calcularValorTotalEstoque()
    val quantidadeTotalProdutos = Estoque().calcularQuantidadeTotal()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Estatísticas do Estoque", fontSize = 22.sp)

        Text(text = "Valor Total do Estoque: R$ $valorTotalEstoque")
        Text(text = "Quantidade Total de Produtos: $quantidadeTotalProdutos unidades")
    }
}

