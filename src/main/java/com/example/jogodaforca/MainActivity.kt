package com.example.jogodaforca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jogodaforca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Binding para acessar os elementos da interface definidos no XML
    private lateinit var binding: ActivityMainBinding

    // Mapa que associa cada palavra a um recurso de imagem correspondente
    private val palavras = mapOf(
        "gloss" to R.drawable.gloss,
        "vestido" to R.drawable.ves,
        "perfume" to R.drawable.per,
        "salon-line" to R.drawable.sl,
        "saia" to R.drawable.sainha,
        "esmalte" to R.drawable.mal,
        "maquiagem" to R.drawable.make
    )

    // Variáveis para armazenar a palavra selecionada e sua versão oculta
    private lateinit var palavraSelecionada: String
    private lateinit var palavraOculta: CharArray

    companion object {
        const val TENTATIVAS_INICIAIS = 5 // Constante que define o número inicial de tentativas
    }

    private var tentativas = TENTATIVAS_INICIAIS // Armazena o número de tentativas restantes
    private val letrasAdivinhadas = mutableSetOf<Char>() // Conjunto que armazena as letras já adivinhadas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout usando o binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciarNovoJogo() // Inicia um novo jogo ao criar a atividade

        // Define o comportamento do botão "Adivinhar"
        binding.buttonAdivinhar.setOnClickListener {
            val entrada = binding.editEntrada.text.toString().uppercase() // Obtém a entrada do usuário e converte para maiúsculas
            processarEntrada(entrada) // Processa a letra inserida
        }

        // Define o comportamento do botão "Novo Jogo"
        binding.buttonNovoJogo.setOnClickListener {
            iniciarNovoJogo() // Reinicia o jogo
        }
    }

    private fun iniciarNovoJogo() {
        // Seleciona uma palavra aleatória do mapa de palavras e a converte para maiúsculas
        palavraSelecionada = palavras.keys.random().uppercase()
        // Inicializa a palavra oculta com sublinhados
        palavraOculta = CharArray(palavraSelecionada.length) { '_' }
        // Reseta as tentativas e as letras adivinhadas
        tentativas = TENTATIVAS_INICIAIS
        letrasAdivinhadas.clear()

        // Limpa a entrada de texto e habilita o botão de adivinhar
        binding.editEntrada.text.clear()
        binding.buttonAdivinhar.isEnabled = true
        binding.textStatus.text = ""

        // Atualiza a imagem correspondente à palavra sorteada
        binding.imagePalavra.setImageResource(palavras[palavraSelecionada.lowercase()] ?: 0)

        atualizarTela() // Atualiza a interface do usuário
    }

    private fun processarEntrada(entrada: String) {
        // Verifica se a entrada não está vazia e contém exatamente uma letra
        if (entrada.isNotEmpty() && entrada.length == 1) {
            val letra = entrada[0] // Obtém a letra inserida

            // Verifica se a letra já foi adivinhada
            if (letra in letrasAdivinhadas) {
                binding.textStatus.text = "Letra '$letra' já foi adivinhada."
            } else {
                letrasAdivinhadas.add(letra) // Adiciona a letra ao conjunto de letras adivinhadas
                // Verifica se a letra está na palavra selecionada
                if (palavraSelecionada.contains(letra)) {
                    revelarLetra(letra) // Revela a letra na palavra oculta
                } else {
                    tentativas-- // Decrementa o número de tentativas
                }
                binding.editEntrada.text.clear() // Limpa o campo de entrada
                atualizarTela() // Atualiza a interface do usuário
            }
        } else {
            binding.textStatus.text = "Entrada inválida. Digite uma única letra."
        }
    }

    private fun revelarLetra(letra: Char) {
        // Revela todas as ocorrências da letra na palavra oculta
        for (i in palavraSelecionada.indices) {
            if (palavraSelecionada[i] == letra) {
                palavraOculta[i] = letra
            }
        }
    }

    private fun atualizarTela() {
        // Atualiza a exibição da palavra oculta e das tentativas restantes
        binding.textOculto.text = palavraOculta.joinToString(" ")
        binding.textTentativas.text = "Restam: $tentativas"

        // Verifica se o jogo terminou (vitória ou derrota)
        when {
            !palavraOculta.contains('_') -> {
                binding.textStatus.text = "Parabéns! Você adivinhou a palavra: $palavraSelecionada"
                binding.buttonAdivinhar.isEnabled = false // Desativa o botão de adivinhar
            }
            tentativas <= 0 -> {
                binding.textStatus.text = "Perdeu! A palavra era: $palavraSelecionada"
                binding.buttonAdivinhar.isEnabled = false // Desativa o botão de adivinhar
            }
            else -> binding.textStatus.text = ""
        }
    }
}
