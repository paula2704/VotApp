package com.VotApp.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.VotApp.demo.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    // verifica si un usuario ya votó en una encuesta
    // esto es la primera línea de defensa (la segunda es el UNIQUE en la BD)
    boolean existsBySurveyIdAndUserId(Long surveyId, Long userId);

    // esta query es más compleja — no se puede generar solo con el nombre
    // @Query nos permite escribir JPQL (similar a SQL pero con nombres de entidades)
    // cuenta cuántos votos tiene cada opción en una encuesta
    // GROUP BY o.id → agrupa por opción para contar votos de cada una
    @Query("SELECT o.id, o.text, COUNT(v) FROM Vote v JOIN v.option o WHERE v.survey.id = :surveyId GROUP BY o.id, o.text")
    List<Object[]> countVotesBySurveyId(@Param("surveyId") Long surveyId);
}