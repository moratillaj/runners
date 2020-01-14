package com.runnersteam.runners.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.runnersteam.runners.exception.RunnerNotFoundException;
import com.runnersteam.runners.model.Runner;
import com.runnersteam.runners.service.RunnersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
public class RunnersController {

  @Autowired
  private RunnersService runnersService;

  @ResponseStatus(OK)
  @GetMapping("/{nickname}")
  public Runner findByNickname(@PathVariable("nickname") String nickname) {
    log.info("findByNickname-" + nickname);
    return runnersService.findByNickname(nickname)
        .orElseThrow(() ->
            new RunnerNotFoundException("Runner with nickname " + nickname + " does not exist"));
  }

  @ResponseStatus(CREATED)
  @PostMapping(value = "/")
  public Runner create(@RequestBody Runner runner) {
    log.info("create-" + runner.toString());
    return runnersService.create(runner);
  }

  @ResponseStatus(OK)
  @PutMapping("/{nickname}")
  public Runner update(@PathVariable("nickname") String nickname, @RequestBody Runner runner) {
    log.info("update-" + nickname + ";" + runner.toString());
    Runner toUpdate = runner.toBuilder().nickname(nickname).build();
    return runnersService.update(toUpdate);
  }

  @ResponseStatus(NO_CONTENT)
  @DeleteMapping("/{nickname}")
  public void deleteByNickname(@PathVariable("nickname") String nickname) {
    log.info("deleteByNickname-" + nickname);
    runnersService.deleteByNickName(nickname);
  }
}
