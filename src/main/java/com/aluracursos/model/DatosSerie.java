package com.aluracursos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//ignora todo lo no declarado del json
@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSerie(
        ///permite leer
        @JsonAlias("Title") String titulo,
        @JsonAlias("totalSeasons") Integer totalDeTemporadas,
        //permite leer y escribir
        @JsonProperty("imdbRating") String evaluacion){

}
