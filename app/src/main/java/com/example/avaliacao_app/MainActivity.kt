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
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { TelaCadastroProduto(navController) }
        composable("productList") { TelaListaProdutos(navController) }
        composable("productDetail/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson") ?: ""
            TelaDetalhesProduto(produtoJson, navController)
        }
        composable("statistics") { TelaEstatisticas(navController) }
    }
}

@Composable
fun TelaCadastroProduto(navController: NavHostController) {
    val context = LocalContext.current
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cadastro de Produto", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome do Produto") })
        Spacer(modifier = Modifier.height(10.dp))
        TextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoria") })
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

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            try {
                val precoDouble = preco.toDoubleOrNull()
                val quantidadeInt = quantidade.toIntOrNull()

                if (nome.isBlank() || categoria.isBlank() || precoDouble == null || quantidadeInt == null || quantidadeInt < 1 || precoDouble < 0) {
                    Toast.makeText(context, "Campos inválidos!", Toast.LENGTH_SHORT).show()
                } else {
                    Estoque.adicionarProduto(Produto(nome, categoria, precoDouble, quantidadeInt))
                    Toast.makeText(context, "Produto adicionado!", Toast.LENGTH_SHORT).show()
                    navController.navigate("productList")
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao cadastrar o produto: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            navController.navigate("productList")
        }) {
            Text("Ver Produtos")
        }
    }
}

@Composable
fun TelaListaProdutos(navController: NavHostController) {
    val produtos = Estoque.produtos
    val valorTotalEstoque = Estoque.calcularValorTotalEstoque()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Lista de Produtos",fontSize = 22.sp)
        Text("Valor Total: R$ $valorTotalEstoque")

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(produtos.size) { index ->
                val produto = produtos[index]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("${produto.nome} (${produto.quantidade} unidades)")
                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("productDetail/$produtoJson")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { navController.navigate("statistics") }) {
            Text("Ver Estatísticas")
        }
    }
}


@Composable
fun TelaDetalhesProduto(produtoJson: String, navController: NavHostController) {
    val produto = Gson().fromJson(produtoJson, Produto::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Detalhes do Produto",fontSize = 22.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Nome: ${produto.nome}")
        Text("Categoria: ${produto.categoria}")
        Text("Preço: R$ ${produto.preco}")
        Text("Quantidade: ${produto.quantidade}")

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}


@Composable
fun TelaEstatisticas(navController: NavHostController) {

    val valorTotalEstoque = Estoque.calcularValorTotalEstoque()
    val quantidadeTotalProdutos = Estoque.calcularQuantidadeTotal()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Estatísticas do Estoque", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Valor Total do Estoque: R$ $valorTotalEstoque")
        Text(text = "Quantidade Total de Produtos: $quantidadeTotalProdutos unidades")

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}
