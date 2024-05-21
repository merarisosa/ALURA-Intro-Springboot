package com.aluracursos.principal;

import java.util.Arrays;
import java.util.List;

public class EjemploStreams {
    public void muestraEjemplo(){
        List<String> nombres = Arrays.asList("Merari", "Antonio", "Amisadai", "Azarmabeth", "Dalila");
        nombres.stream()
                .sorted() //ordenada
                .limit(4) //limita numero de opcs
                .filter(n -> n.startsWith("A")) //filtra busqueda
                .map(n -> n.toUpperCase()) //convierte a mayuscula
                .forEach(System.out::println);
    }
}
