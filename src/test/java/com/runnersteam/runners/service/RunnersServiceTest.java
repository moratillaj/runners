package com.runnersteam.runners.service;

import static com.runnersteam.runners.util.TestData.buildRunner;
import static java.time.LocalDate.now;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.runnersteam.runners.exception.ExistingRunnerException;
import com.runnersteam.runners.exception.RunnerNotFoundException;
import com.runnersteam.runners.model.Runner;
import com.runnersteam.runners.repository.RunnersRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RunnersServiceTest {

  private static final String THE_NICKNAME = "theNickname";

  @InjectMocks
  private RunnersService runnersService;

  @Mock
  private RunnersRepository runnersRepository;

  @Mock
  private Runner runner;

  @Test
  public void shouldFindByNickname() {
    //Given
    when(runnersRepository.findById(THE_NICKNAME)).thenReturn(of(runner));

    //When
    Optional<Runner> found = runnersService.findByNickname(THE_NICKNAME);

    //Then
    assertThat(found).isPresent();
    verify(runnersRepository).findById(THE_NICKNAME);

  }

  @Test
  public void shouldNotFindByNickname() {
    //Given
    when(runnersRepository.findById(THE_NICKNAME)).thenReturn(empty());

    //When
    Optional<Runner> found = runnersService.findByNickname(THE_NICKNAME);

    //Then
    assertThat(found).isNotPresent();
    verify(runnersRepository).findById(THE_NICKNAME);
  }

  @Test
  public void shouldCreate() {
    //Given
    when(runnersRepository.findById(THE_NICKNAME)).thenReturn(empty());
    when(runner.getNickname()).thenReturn(THE_NICKNAME);
    when(runnersRepository.save(runner)).thenReturn(runner);

    //When
    Runner created = runnersService.create(runner);

    //Then
    assertThat(created).isNotNull();
    InOrder inOrder = inOrder(runnersRepository, runner);
    inOrder.verify(runner).getNickname();
    inOrder.verify(runnersRepository).findById(THE_NICKNAME);
    inOrder.verify(runnersRepository).save(runner);
  }

  @Test
  public void shouldCreateFailWhenExist() {
    //Given
    when(runnersRepository.findById(THE_NICKNAME)).thenReturn(of(runner));
    when(runner.getNickname()).thenReturn(THE_NICKNAME);

    //When && Then
    assertThatThrownBy(() -> runnersService.create(runner))
        .hasMessage("Runner with " + runner.getNickname() + " already exists")
        .isInstanceOf(ExistingRunnerException.class);
    InOrder inOrder = inOrder(runnersRepository, runner);
    inOrder.verify(runner).getNickname();
    inOrder.verify(runnersRepository).findById(THE_NICKNAME);
    verify(runnersRepository, never()).save(runner);
  }

  @Test
  public void shouldUpdate() {
    //Given
    Runner current = buildRunner();
    current.setSubscriptionDate(now());
    current.setNickname(THE_NICKNAME);
    when(runnersRepository.findById(THE_NICKNAME)).thenReturn(of(current));
    when(runnersRepository.save(any(Runner.class)))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    String theLastRace = "theLastRace";
    Runner toUpdate = current.toBuilder()
        .lastRace(theLastRace).email("fakemail@email.com").build();

    //When
    Runner updated = runnersService.update(toUpdate);

    //Then
    assertThat(updated).extracting("nickname", "name", "surname",
        "email", "birthDate", "lastRace", "subscriptionDate")
        .contains(current.getNickname(), current.getName(), current.getSurname(),
            current.getEmail(), toUpdate.getLastRace(), current.getSubscriptionDate());
    InOrder inOrder = inOrder(runnersRepository);
    inOrder.verify(runnersRepository).findById(THE_NICKNAME);
    inOrder.verify(runnersRepository).save(any(Runner.class));

  }

  @Test
  public void shouldUpdateFailWhenNotExist() {
    //Given
    when(runner.getNickname()).thenReturn(THE_NICKNAME);
    when(runnersRepository.findById(THE_NICKNAME)).thenReturn(empty());

    //When && Then
    assertThatThrownBy(() -> runnersService.update(runner))
        .isInstanceOf(RunnerNotFoundException.class)
        .hasMessage("Runner with nickname " + THE_NICKNAME + " does not exist");
    verify(runnersRepository).findById(THE_NICKNAME);
    verify(runnersRepository, never()).save(runner);
  }

  @Test
  public void shouldDeleteById() {
    //Given && When
    runnersService.deleteByNickName(THE_NICKNAME);

    //Then
    verify(runnersRepository).deleteById(THE_NICKNAME);
  }
}