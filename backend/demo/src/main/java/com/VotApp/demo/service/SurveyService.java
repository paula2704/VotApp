package com.VotApp.demo.service;

import com.VotApp.demo.dto.OptionResponse;
import com.VotApp.demo.dto.SurveyRequest;
import com.VotApp.demo.dto.SurveyResponse;
import com.VotApp.demo.entity.Option;
import com.VotApp.demo.entity.Survey;
import com.VotApp.demo.entity.User;
import com.VotApp.demo.repository.SurveyRepository;
import com.VotApp.demo.repository.UserRepository;
import com.VotApp.demo.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    // convierte una entidad Survey a SurveyResponse (DTO)
    private SurveyResponse toResponse(Survey survey) {
        // obtiene los votos de cada opción
        List<Object[]> voteCounts = voteRepository.countVotesBySurveyId(survey.getId());

        // convierte la lista de Object[] a un mapa optionId -> votos
        // para buscar rápido cuántos votos tiene cada opción
        Map<Long, Long> voteMap = voteCounts.stream()
                .collect(Collectors.toMap(
                    row -> (Long) row[0],   // id de la opción
                    row -> (Long) row[2]    // cantidad de votos
                ));

        // construye la lista de opciones con sus votos
        List<OptionResponse> options = survey.getOptions().stream()
                .map(opt -> {
                    OptionResponse or = new OptionResponse();
                    or.setId(opt.getId());
                    or.setText(opt.getText());
                    or.setVotes(voteMap.getOrDefault(opt.getId(), 0L));
                    return or;
                }).collect(Collectors.toList());

        // construye el response final
        SurveyResponse response = new SurveyResponse();
        response.setId(survey.getId());
        response.setQuestion(survey.getQuestion());
        response.setCreatedBy(survey.getCreatedBy().getUsername());
        response.setActive(survey.getActive());
        response.setCreatedAt(survey.getCreatedAt());
        response.setOptions(options);
        return response;
    }

    public List<SurveyResponse> getAll() {
        return surveyRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SurveyResponse> getActive() {
        return surveyRepository.findByActive(true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SurveyResponse getById(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        return toResponse(survey);
    }

    public SurveyResponse create(SurveyRequest request, String username) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Survey survey = new Survey();
        survey.setQuestion(request.getQuestion());
        survey.setCreatedBy(admin);

        // convierte cada string de opción a una entidad Option
        List<Option> options = request.getOptions().stream()
                .map(text -> {
                    Option opt = new Option();
                    opt.setText(text);
                    opt.setSurvey(survey); // asocia la opción a la encuesta
                    return opt;
                }).collect(Collectors.toList());

        survey.setOptions(options);
        return toResponse(surveyRepository.save(survey));
    }

    public SurveyResponse update(Long id, SurveyRequest request) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        survey.setQuestion(request.getQuestion());

        // limpia las opciones anteriores y agrega las nuevas
        // orphanRemoval=true en la entidad borra las opciones viejas automáticamente
        survey.getOptions().clear();
        List<Option> newOptions = request.getOptions().stream()
                .map(text -> {
                    Option opt = new Option();
                    opt.setText(text);
                    opt.setSurvey(survey);
                    return opt;
                }).collect(Collectors.toList());
        survey.getOptions().addAll(newOptions);

        return toResponse(surveyRepository.save(survey));
    }

    public void delete(Long id) {
        surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        surveyRepository.deleteById(id);
    }

    public void toggleActive(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        // cambia el estado: si estaba activa la desactiva y viceversa
        survey.setActive(!survey.getActive());
        surveyRepository.save(survey);
    }
}