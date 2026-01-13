package com.example.demo.principal;

import com.example.demo.model.*;
import com.example.demo.repository.AutorRepository;
import com.example.demo.repository.LivroRepository;
import com.example.demo.service.ConsumoApi;
import com.example.demo.service.ConverteDados;

import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final Scanner scanner = new Scanner(System.in);

    private static final String URL_BASE = "https://gutendex.com/books/?search=";

    public Principal(LivroRepository livroRepository,
                     AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("""
                    ============================================
                    Bem vindo à biblioteca, selecione uma opção:
                    ============================================
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    0 - Sair
                    """);

            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> buscarLivroPorTitulo();
                case 2 -> listarLivros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresVivos();
                case 5 -> listarLivrosPorIdioma();
                case 0 -> {
                    System.out.println("Encerrando o programa...");
                    return;
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void buscarLivroPorTitulo() {
        System.out.print("Digite o título do livro: ");
        String titulo = scanner.nextLine();

        // 1️⃣ Busca no banco
        Optional<Livro> livroBanco = livroRepository.findByTituloIgnoreCase(titulo);
        if (livroBanco.isPresent()) {
            System.out.println(livroBanco.get());
            return;
        }

        // 2️⃣ Busca na API Gutendex
        String endereco = URL_BASE + titulo.replace(" ", "+");
        String json = consumoApi.obterDados(endereco);

        if (json == null || json.isBlank()) {
            System.out.println("Não foi possível obter dados da API.");
            return;
        }

        DadosGutendex dados = conversor.obterDados(json, DadosGutendex.class);

        if (dados.results() == null || dados.results().isEmpty()) {
            System.out.println("Livro não encontrado na API.");
            return;
        }

        DadosLivro dadosLivro = dados.results().get(0);

        // Verifica se há autores válidos
        if (dadosLivro.authors() == null || dadosLivro.authors().isEmpty()
                || dadosLivro.authors().get(0).nome() == null
                || dadosLivro.authors().get(0).nome().isBlank()) {
            System.out.println("Livro encontrado, mas não possui autor válido.");
            return;
        }

        DadosAutor dadosAutor = dadosLivro.authors().get(0);

        // ✅ Salva o autor primeiro para garantir que o ID seja gerado
        Autor autor = autorRepository
                .findByNomeIgnoreCase(dadosAutor.nome())
                .orElseGet(() -> autorRepository.saveAndFlush(new Autor(dadosAutor)));


        // Cria o livro e salva no banco
        Livro livro = new Livro(dadosLivro, autor);
        livroRepository.save(livro);

        System.out.println(livro);
    }

    private void listarLivros() {
        livroRepository.findAll()
                .forEach(System.out::println);
    }

    private void listarAutores() {
        autorRepository.findAll()
                .forEach(System.out::println);
    }

    private void listarAutoresVivos() {
        System.out.print("Ano: ");
        int ano = scanner.nextInt();
        scanner.nextLine();

        autorRepository.findAutoresVivosNoAno(ano)
                .forEach(System.out::println);
    }

    private void listarLivrosPorIdioma() {
        System.out.print("Idioma (ex: en, pt): ");
        String idioma = scanner.nextLine();

        livroRepository.findByIdioma(idioma)
                .forEach(System.out::println);
    }
}
