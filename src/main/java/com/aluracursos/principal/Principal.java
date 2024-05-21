package com.aluracursos.principal;

import com.aluracursos.model.DatosEpisodio;
import com.aluracursos.model.DatosSerie;
import com.aluracursos.model.DatosTemporadas;
import com.aluracursos.model.Episodio;
import com.aluracursos.service.ConsumoAPI;
import com.aluracursos.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=4ed229ed";
    private ConvierteDatos conversor  =  new ConvierteDatos();

    public void muestraMenu(){
        System.out.println("Por favor escribe el nombre de la serie que deseas buscar: ");
        var nombreSerie = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+")+API_KEY );
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(json);
        System.out.println(datos);

        //Busca los datos de todas las temporadas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for(int i = 1; i <= datos.totalDeTemporadas(); i++){
            json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+")+ "&Season="+i+API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);

        }
        temporadas.forEach(System.out::println);

        //Mostrar solo el titulo de los episodios para las temporadas
        for (int i = 0; i < datos.totalDeTemporadas(); i++) {
            List<DatosEpisodio> episodiosTemporada =  temporadas.get(i).episodios();
            for (int j = 0; j <episodiosTemporada.size() ; j++) {
                System.out.println(episodiosTemporada.get(i).titulo());
            }
        }

        //Uso de funciones lambdas para reducir los for
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())) );

        //Converitr todas las informaciones a una lista del tipo DatosEpisodios
        List <DatosEpisodio> datosEpisodios =  temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        //Top 5 episodios
        datosEpisodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Primer filtro (N/A)"+e))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                .peek(e -> System.out.println("Segundo filtro (M>m"+e))
                .map(e -> e.titulo().toUpperCase())
                .peek(e -> System.out.println("Tercer filtro (m->M)"+e))
                .limit(5)
                .forEach( System.out::println);



        //Convirtiendo los datos a una lista de tipo Episodio
        List<Episodio> episodios= temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                        .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        //Busqueda de episodios a partir de x año
        System.out.println("Indica el año a partir del cual deseas ver el episodio");
        var fecha = teclado.nextInt();
        teclado.nextLine(); //en caso de que no lea bien el enter

        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy");
        episodios.stream()
                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        "Temporada "+ e.getTemporada() +
                                "Episodio " + e.getTitulo() +
                                "Fecha de lanzamiento "+ e.getFechaDeLanzamiento().format(dtf)
                ));

        //Busqueda de episodios por coincidencia del titulo
        System.out.println("Escribe el titulo del episodio que deseas ver:");
        var tituloName = teclado.nextLine();
        Optional<Episodio> episodioBuscado =  episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(tituloName.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado");
            System.out.println(episodioBuscado.get());
        }else{
            System.out.println("Episodio no encontrado");
        }

        //Uso de maps
        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println(evaluacionesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println(est);
    }
}
