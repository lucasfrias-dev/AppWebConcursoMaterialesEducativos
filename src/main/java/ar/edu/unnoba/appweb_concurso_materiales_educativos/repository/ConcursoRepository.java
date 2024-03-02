package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcursoRepository extends JpaRepository<Concurso, Long> {

    /**
     * Método de consulta para encontrar el concurso actual en función de la fecha actual.
     *
     * @param currentDate La fecha actual para verificar si está dentro del período de un concurso.
     * @return El concurso que está en curso en la fecha actual, si existe.
     */
    @Query("SELECT c FROM Concurso c WHERE :currentDate BETWEEN c.fechaInicio AND c.fechaFin")
    Concurso findCurrentConcurso(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Método de consulta para encontrar el concurso más reciente según su fecha de finalización.
     *
     * @return El concurso más reciente, es decir, el que tiene la fecha de finalización más tardía.
     */
    @Query("SELECT c FROM Concurso c WHERE c.fechaFin = (SELECT MAX(c.fechaFin) FROM Concurso c)")
    Concurso findLastConcurso();

    /**
     * Método de consulta para encontrar un concurso por su edición.
     *
     * @param edicion La edición del concurso que se desea buscar.
     * @return El concurso que coincide con la edición proporcionada.
     */
    @Query("SELECT c FROM Concurso c WHERE c.edicion = :edicion")
    Concurso findConcursoByEdicion(String edicion);

    /**
     * Método de consulta para encontrar todos los concursos que ya han finalizado en una fecha dada.
     *
     * @param currentDate La fecha actual para verificar qué concursos han finalizado.
     * @return Una lista de concursos que ya han finalizado en la fecha proporcionada.
     */
    @Query("SELECT c FROM Concurso c WHERE :currentDate NOT BETWEEN c.fechaInicio AND c.fechaFin")
    List<Concurso> findAllConcursosFinalizados(@Param("currentDate") LocalDateTime currentDate);


}
