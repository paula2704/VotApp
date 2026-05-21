package com.VotApp.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.VotApp.demo.entity.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findBySurveyId(Long surveyId);
}