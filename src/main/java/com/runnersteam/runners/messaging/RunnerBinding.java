package com.runnersteam.runners.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface RunnerBinding {

  String OUTPUT = "newRunnerRegistrationOutput";

  @Output(OUTPUT)
  MessageChannel newRunnerRegistrationOutput();
}
