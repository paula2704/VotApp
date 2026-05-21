package com.VotApp.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.VotApp.demo.entity.Survey;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    // trae solo las encuestas activas
    // genera: SELECT * FROM surveys WHERE active = ?
    List<Survey> findByActive(Boolean active);
}