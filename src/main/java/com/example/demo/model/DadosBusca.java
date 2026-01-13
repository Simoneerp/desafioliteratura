package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosBusca(
        List<DadosLivro> results
) {}