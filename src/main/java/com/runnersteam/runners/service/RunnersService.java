package com.runnersteam.runners.service;

import static java.util.Optional.ofNullable;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

import com.runnersteam.runners.exception.ExistingRunnerException;
import com.runnersteam.runners.exception.RunnerNotFoundException;
import com.runnersteam.runners.model.Runner;
import com.runnersteam.runners.repository.RunnersRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public class RunnersService {

  @Autowired
  private RunnersRepository runnersRepository;

  @Autowired
  private MessageChannel newRunnerRegistrationOutput;

  public Optional<Runner> findByNickname(String nickname) {
    return runnersRepository.findById(nickname);
  }

  public Runner create(Runner runner) {
    if (existRunner(runner)) {
      throw new ExistingRunnerException("Runner with " + runner.getNickname() + " already exists");
    }

    newRunnerRegistrationOutput.send(withPayload(runner).build());
    return runnersRepository.save(runner);
  }

  private boolean existRunner(Runner runner) {
    return ofNullable(runner)
        .map(Runner::getNickname)
        .flatMap(runnersRepository::findById)
        .isPresent();
  }

  public Runner update(Runner runner) {
    return ofNullable(runner)
        .map(Runner::getNickname)
        .flatMap(runnersRepository::findById)
        .map(current -> mergeRunner(current, runner))
        .map(runnersRepository::save)
        .orElseThrow(() -> new RunnerNotFoundException(
            "Runner with nickname " + runner.getNickname() + " does not exist"));
  }

  private Runner mergeRunner(Runner current, Runner runner) {
    return current.toBuilder()
        .lastRace(runner.getLastRace())
        .build();
  }

  public void deleteByNickName(String nickname) {
    runnersRepository.deleteById(nickname);
  }

}
