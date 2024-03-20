package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.ConcursoRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.utils.RomanNumerals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConcursoServiceImpl implements ConcursoService{

    private final ConcursoRepository concursoRepository;

    @Autowired
    public ConcursoServiceImpl(ConcursoRepository concursoRepository) {
        this.concursoRepository = concursoRepository;
    }

    /**
     * Retorna el concurso actual basado en la fecha y hora actual.
     *
     * @return El concurso que está actualmente en curso.
     */
    @Override
    public Concurso getConcursoActual() {
        return concursoRepository.findCurrentConcurso(LocalDateTime.now());
    }

    /**
     * Retorna el concurso asociado a la edición especificada.
     *
     * @param edicion La edición del concurso que se desea obtener.
     * @return El concurso asociado a la edición especificada, si existe.
     */
    @Override
    public Concurso getConcursoByEdicion(String edicion) {
        return concursoRepository.findConcursoByEdicion(edicion);
    }

    /**
     * Retorna una lista de todos los concursos que ya han finalizado.
     *
     * @return Una lista de concursos que ya han finalizado.
     */
    @Override
    public List<Concurso> getConcursosAnteriores() {
        return concursoRepository.findAllConcursosFinalizados(LocalDateTime.now());
    }

    /**
     * Crea un nuevo concurso y lo guarda en la base de datos.
     * Si no hay ningún concurso en la base de datos, se establece la edición del nuevo concurso como la tercera edición,
     * ya que se realizaron otras ediciones en otro sistema.
     * De lo contrario, se genera la edición del nuevo concurso basándose en la última edición existente.
     *
     * @param concurso El objeto Concurso que se va a crear y guardar.
     * @return El concurso creado y guardado en la base de datos.
     */
    @Override
    @Transactional
    public Concurso createConcurso(Concurso concurso) {
        // Busca el último concurso en la base de datos
        Concurso lastConcurso = concursoRepository.findLastConcurso();
        if (lastConcurso == null) {
            // Si no hay ningún concurso en la base de datos, comenzamos desde la tercera edición
            concurso.setEdicion(RomanNumerals.convert(3) + " Edicion");
        } else {
            // Si hay concurso(es) en la base de datos, genera la edición para el nuevo concurso
            String lastEdicionNumber = lastConcurso.getEdicion().substring(0, lastConcurso.getEdicion().indexOf(" "));
            int nextEdicionNumber = RomanNumerals.toInteger(lastEdicionNumber) + 1;
            concurso.setEdicion(RomanNumerals.convert(nextEdicionNumber) + " Edicion");
        }
        // Guarda el nuevo concurso en la base de datos y lo retorna
        return concursoRepository.save(concurso);
    }

    /**
     * Cierra el concurso con la edición especificada.
     *
     * @param edicion La edición del concurso que se va a cerrar.
     * @throws IllegalArgumentException Si no se encuentra ningún concurso con la edición especificada.
     * @throws IllegalStateException    Si el concurso ya está cerrado.
     */
    @Override
    @Transactional
    public void cerrarConcurso(String edicion) {
        // Busca el concurso con la edición especificada en la base de datos
        Concurso concurso = concursoRepository.findConcursoByEdicion(edicion);

        // Verifica si se encontró el concurso
        if (concurso == null) {
            throw new IllegalArgumentException("No se encontró el concurso con la edición: " + edicion);
        }

        // Verifica si el concurso ya está cerrado
        if (concurso.getFechaFin() != null && concurso.getFechaFin().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El concurso ya está cerrado.");
        }

        // Establece la fecha de cierre del concurso como la fecha y hora actuales
        concurso.setFechaFin(LocalDateTime.now());

        // Guarda los cambios en el concurso en la base de datos
        concursoRepository.save(concurso);
    }

    /**
     * Reabre el concurso con la edición especificada y establece una nueva fecha de cierre.
     *
     * @param edicion   La edición del concurso que se va a reabrir.
     * @param fechaFin  La nueva fecha de cierre para el concurso.
     * @throws IllegalArgumentException Si no se encuentra ningún concurso con la edición especificada.
     * @throws IllegalStateException    Si el concurso ya está abierto.
     */
    @Override
    @Transactional
    public void reabrirConcurso(String edicion, LocalDateTime fechaFin) {
        // Obtiene el concurso actual utilizando el método correspondiente.
        Concurso concursoActual = concursoRepository.findCurrentConcurso(LocalDateTime.now());

        // Si hay un concurso actual, lanza una excepción.
        if (concursoActual != null) {
            throw new IllegalStateException("No se puede reabrir un concurso porque ya hay un concurso actual.");
        }

        // Busca el concurso con la edición especificada en la base de datos
        Concurso concurso = concursoRepository.findConcursoByEdicion(edicion);

        // Verifica si se encontró el concurso
        if (concurso == null) {
            throw new IllegalArgumentException("No se encontró el concurso con la edición: " + edicion);
        }

        // Verifica si el concurso ya está abierto
        if (concurso.getFechaFin() == null || concurso.getFechaFin().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("El concurso ya está abierto.");
        }

        // Establece la nueva fecha de cierre del concurso
        concurso.setFechaFin(fechaFin);

        // Guarda los cambios en el concurso en la base de datos
        concursoRepository.save(concurso);
    }

    /*/**
     * Agrega un material a la lista de materiales ganadores de un concurso y guarda los cambios en la base de datos.
     *
     * @param concurso El concurso al que se agregarán los materiales ganadores.
     * @param material El material que se agregará a la lista de ganadores del concurso.
    @Override
    @Transactional
    public void addMaterialGanador(Concurso concurso, Material material){
        // Agrega el material a la lista de materiales ganadores del concurso.
        concurso.getMaterialesGanadores().add(material);
        // Guarda el concurso actualizado en la base de datos
        concursoRepository.save(concurso);
    }*/
}
