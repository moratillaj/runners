package com.runnersteam.runners.repository;

import static com.runnersteam.runners.util.TestData.buildRunner;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.runnersteam.runners.model.Runner;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class RunnersRepositoryTest {

  @Autowired
  private TestEntityManager testEntityManager;

  private Runner runner;
  private static final String THE_NICKNAME = "THENICKNAME";

  @BeforeEach
  public void setUp() {
    runner = buildRunner();
    runner.setNickname(THE_NICKNAME);
  }

  @Test
  public void shouldCreate() {
    //Given
    Runner newRunner = buildRunner().toBuilder().lastRace("theLastRace").build();
    LocalDate today = now();

    //When
    Runner created = testEntityManager.persist(newRunner);

    //Then
    assertThat(created)
        .extracting("nickname", "name", "surname",
            "email", "birthDate", "lastRace")
        .contains(newRunner.getNickname(), newRunner.getName(), newRunner.getSurname(),
            newRunner.getEmail(), newRunner.getBirthDate(), newRunner.getLastRace());

    assertThat(created.getSubscriptionDate()).isBeforeOrEqualTo(now());
    assertThat(created.getSubscriptionDate()).isAfterOrEqualTo(today);
  }

  @Test
  public void shouldCreateFailWhenNicknameExist() {
    //Given
    testEntityManager.persist(runner);
    Runner withExistingNickname = runner.toBuilder()
        .email("otherEmail@email.com").build();

    //When && Then
    assertThatThrownBy(() -> testEntityManager.persist(withExistingNickname))
        .isInstanceOf(Exception.class);
  }

  @Test
  public void shouldFindById() {
    //Given
    Runner newRunner = buildRunner().toBuilder().lastRace("theLastRace").build();
    testEntityManager.persist(newRunner);

    //When
    Runner found = testEntityManager.find(Runner.class, newRunner.getNickname());

    //Then
    assertThat(found)
        .extracting("nickname", "name", "surname",
            "email", "birthDate", "lastRace")
        .contains(newRunner.getNickname(), newRunner.getName(), newRunner.getSurname(),
            newRunner.getEmail(), newRunner.getBirthDate(), newRunner.getLastRace());
    assertThat(found.getSubscriptionDate()).isNotNull();
  }

  @Test
  public void shouldNotFindById() {
    //Given && When && Then
    assertThat(testEntityManager.find(Runner.class, "unexistentNicname")).isNull();
  }

  @Test
  public void shouldUpdate() {
    //Given
    Runner created = testEntityManager.persist(runner);
    created.setLastRace("theRace");

    //When
    Runner updated = testEntityManager.persist(created);

    //Then
    assertThat(updated).extracting("nickname", "name", "surname",
        "email", "birthDate", "lastRace", "subscriptionDate")
        .contains(created.getNickname(), created.getName(), created.getSurname(),
            created.getEmail(), created.getBirthDate(), created.getLastRace(),
            created.getSubscriptionDate());
  }

  @Test
  public void shouldDeleteById() {
    //Given
    testEntityManager.persist(runner);

    //When
    testEntityManager.remove(runner);

    //Then
    assertThat(testEntityManager.find(Runner.class, runner.getNickname())).isNull();
  }

}