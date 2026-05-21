package com.VotApp.demo.service;

import com.VotApp.demo.dto.VoteRequest;
import com.VotApp.demo.entity.Option;
import com.VotApp.demo.entity.Survey;
import com.VotApp.demo.entity.User;
import com.VotApp.demo.entity.Vote;
import com.VotApp.demo.repository.OptionRepository;
import com.VotApp.demo.repository.SurveyRepository;
import com.VotApp.demo.repository.UserRepository;
import com.VotApp.demo.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final SurveyRepository surveyRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    public void vote(Long surveyId, VoteRequest request, String username) {
        // primera línea de defensa: verifica en el service si ya votó
        if (voteRepository.existsBySurveyIdAndUserId(surveyId,
                userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado")).getId())) {
            throw new RuntimeException("Ya votaste en esta encuesta");
        }

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        // verifica que la encuesta esté activa
        if (!survey.getActive()) {
            throw new RuntimeException("La encuesta no está activa");
        }

        Option option = optionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new RuntimeException("Opción no encontrada"));

        // verifica que la opción pertenezca a la encuesta
        // evita que alguien vote con una opción de otra encuesta
        if (!option.getSurvey().getId().equals(surveyId)) {
            throw new RuntimeException("La opción no pertenece a esta encuesta");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Vote vote = new Vote();
        vote.setSurvey(survey);
        vote.setOption(option);
        vote.setUser(user);

        // segunda línea de defensa: el UNIQUE en la BD rechaza duplicados
        voteRepository.save(vote);
    }
}