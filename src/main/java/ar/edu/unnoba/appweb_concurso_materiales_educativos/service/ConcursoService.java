package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcursoService {

    Concurso getConcursoActual();

    Concurso getConcursoByEdicion(String edicion);

    List<Concurso> getConcursosAnteriores();

    Concurso createConcurso(Concurso concurso);

    void cerrarConcurso(String edicion);

    void reabrirConcurso(String edicion, LocalDateTime fechaFin);
}
