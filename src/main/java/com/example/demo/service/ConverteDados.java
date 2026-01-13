package com.example.demo.service;

import tools.jackson.databind.ObjectMapper;





public class ConverteDados implements IConverteDados {

    private ObjectMapper objectMapper = new ObjectMapper();

    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return objectMapper.readValue(json, classe);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


