package com.aluracursos.service;

public interface IConvierteDatos {
    //tipo de datos genericos, no conocemos el retorno
    <T> T obtenerDatos(String json, Class<T> clase);
}
