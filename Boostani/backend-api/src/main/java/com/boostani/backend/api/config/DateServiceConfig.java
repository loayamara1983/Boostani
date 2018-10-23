package com.boostani.backend.api.config;

import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.boostani.backend.api.service.date.DateService;
import com.boostani.backend.api.service.date.JodaDateService;

@Configuration
public class DateServiceConfig {

  @Bean
  DateService dateService() {
    return new JodaDateService(defaultTimeZone());
  }

  @Bean
  DateTimeZone defaultTimeZone() {
    return DateTimeZone.UTC;
  }
}
