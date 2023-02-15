package com.symphony.profserv;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Profile("test")
@SpringBootTest
class BotApplicationTest {

  @Autowired
  private BotApplication application;

  /**
   * Test that the Spring application context loads correctly (i.e. the bot starts)
   */
  @Test
  public void contextLoads() {
    assertThat(application).isNotNull();
  }

}