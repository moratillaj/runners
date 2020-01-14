package com.runnersteam.runners.util;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.YEARS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runnersteam.runners.model.Runner;
import java.text.SimpleDateFormat;

public class TestData {

  public static Runner buildRunner() {
    return Runner.builder()
        .nickname("theNickname")
        .name("theName")
        .surname("theSurname")
        .email("theEmail@email")
        .birthDate(now().minus(35, YEARS))
        .build();
  }

  public static String toJson(Object object) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    return objectMapper.writeValueAsString(object);
  }
}
