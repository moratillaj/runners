package com.runnersteam.runners.controller;

import static com.runnersteam.runners.util.TestData.buildRunner;
import static com.runnersteam.runners.util.TestData.toJson;
import static java.net.URI.create;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.runnersteam.runners.config.RunnersExceptionHandler;
import com.runnersteam.runners.controller.RunnersControllerTest.RunnersControllerTestConfig;
import com.runnersteam.runners.exception.ExistingRunnerException;
import com.runnersteam.runners.exception.RunnerNotFoundException;
import com.runnersteam.runners.model.Runner;
import com.runnersteam.runners.service.RunnersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RunnersController.class)
@ContextConfiguration(classes = {RunnersControllerTestConfig.class, RunnersExceptionHandler.class})
@ExtendWith(SpringExtension.class)
public class RunnersControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RunnersService runnersService;

  @Test
  public void shouldFindByNickname() throws Exception {
    //Given
    Runner runner = buildRunner().toBuilder()
        .subscriptionDate(now()).lastRace("theLastRace").build();
    when(runnersService.findByNickname(runner.getNickname())).thenReturn(of(runner));

    //When && Then
    mockMvc.perform(get(create("/" + runner.getNickname())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nickname", is(runner.getNickname())))
        .andExpect(jsonPath("$.name", is(runner.getName())))
        .andExpect(jsonPath("$.surname", is(runner.getSurname())))
        .andExpect(jsonPath("$.birthDate",
            is(runner.getBirthDate().format(ISO_DATE))))
        .andExpect(jsonPath("$.email", is(runner.getEmail())))
        .andExpect(jsonPath("$.subscriptionDate",
            is(runner.getSubscriptionDate().format(ISO_DATE))))
        .andExpect(jsonPath("$.lastRace", is(runner.getLastRace())));

    verify(runnersService).findByNickname(runner.getNickname());
  }

  @Test
  public void shouldFindByNicknameReturnNotFound() throws Exception {
    //Given
    String nickname = "nonExistingNickname";
    when(runnersService.findByNickname(nickname)).thenReturn(empty());

    //When && Then
    mockMvc.perform(get(create("/" + nickname)))
        .andExpect(status().isNotFound());

    verify(runnersService).findByNickname(nickname);
  }

  @Test
  public void shouldCreate() throws Exception {
    //Given
    Runner runner = buildRunner().toBuilder()
        .subscriptionDate(now()).lastRace("theLastRace").build();
    when(runnersService.create(runner)).thenReturn(runner);

    //When && Then
    mockMvc.perform(post(create("/"))
        .contentType(APPLICATION_JSON)
        .content(toJson(runner)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nickname", is(runner.getNickname())))
        .andExpect(jsonPath("$.name", is(runner.getName())))
        .andExpect(jsonPath("$.surname", is(runner.getSurname())))
        .andExpect(jsonPath("$.birthDate",
            is(runner.getBirthDate().format(ISO_DATE))))
        .andExpect(jsonPath("$.email", is(runner.getEmail())))
        .andExpect(jsonPath("$.subscriptionDate",
            is(runner.getSubscriptionDate().format(ISO_DATE))))
        .andExpect(jsonPath("$.lastRace", is(runner.getLastRace())));

    verify(runnersService).create(runner);
  }

  @Test
  public void shouldCreateReturnConflictWhenAlreadyExist() throws Exception {
    //Given
    Runner runner = buildRunner().toBuilder()
        .subscriptionDate(now()).lastRace("theLastRace").build();
    when(runnersService.create(runner)).thenThrow(
        new ExistingRunnerException("runner with " + runner.getNickname() + " already exists"));

    //When && Then
    mockMvc.perform(post(create("/"))
        .contentType(APPLICATION_JSON)
        .content(toJson(runner)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorMessage",
            is("runner with " + runner.getNickname() + " already exists")));

    verify(runnersService).create(runner);
  }

  @Test
  public void shouldUpdate() throws Exception {
    //Given
    Runner runner = buildRunner().toBuilder()
        .subscriptionDate(now()).lastRace("theLastRace").build();
    when(runnersService.update(runner)).thenReturn(runner);

    //When && Then
    mockMvc.perform(put(create("/" + runner.getNickname()))
        .contentType(APPLICATION_JSON)
        .content(toJson(runner)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nickname", is(runner.getNickname())))
        .andExpect(jsonPath("$.name", is(runner.getName())))
        .andExpect(jsonPath("$.surname", is(runner.getSurname())))
        .andExpect(jsonPath("$.birthDate",
            is(runner.getBirthDate().format(ISO_DATE))))
        .andExpect(jsonPath("$.email", is(runner.getEmail())))
        .andExpect(jsonPath("$.subscriptionDate",
            is(runner.getSubscriptionDate().format(ISO_DATE))))
        .andExpect(jsonPath("$.lastRace", is(runner.getLastRace())));

    verify(runnersService).update(runner);
  }

  @Test
  public void shouldUpdateReturnNotFoundWhenNotExist() throws Exception {
    //Given
    Runner runner = buildRunner().toBuilder()
        .subscriptionDate(now()).lastRace("theLastRace").build();
    when(runnersService.update(runner)).thenThrow(
        new RunnerNotFoundException("runner with " + runner.getNickname() + " does not exist"));

    //When && Then
    mockMvc.perform(put(create("/" + runner.getNickname()))
        .contentType(APPLICATION_JSON)
        .content(toJson(runner)))
        .andExpect(status().isNotFound());

    verify(runnersService).update(runner);
  }

  @Test
  public void shouldDelete() throws Exception {
    //Given
    String theNickname = "theNickname";

    //When && Then
    mockMvc.perform(delete(create("/" + theNickname)))
        .andExpect(status().isNoContent());

    verify(runnersService).deleteByNickName(theNickname);
  }

  @Test
  public void shouldDeleteReturnInternalErrorWhenFail() throws Exception {
    //Given
    String theNickname = "theNickname";
    doThrow(new RuntimeException("error deleting runner with nickname " + theNickname))
        .when(runnersService).deleteByNickName(theNickname);

    //When && Then
    mockMvc.perform(delete(create("/" + theNickname)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.errorMessage",
            is("error deleting runner with nickname " + theNickname)));

    verify(runnersService).deleteByNickName(theNickname);
  }

  @Configuration
  @SpringBootApplication
  static class RunnersControllerTestConfig {

  }
}
