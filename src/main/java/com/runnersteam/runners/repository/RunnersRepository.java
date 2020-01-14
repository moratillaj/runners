package com.runnersteam.runners.repository;

import com.runnersteam.runners.model.Runner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunnersRepository extends JpaRepository<Runner, String> {
}
