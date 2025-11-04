package com.pivot.aham.common.config;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addison
 */
@Configuration
public class PromConfig {
  @Autowired
  private  PrometheusMeterRegistry registry;

  @Bean(name = "requestTotal")
  public Counter getRequestTotal() {
    Counter requestTotal = Counter.build().name("method_counter_all")
    .labelNames("methodEnd","env")
    .help("total request couter of method")
    .register(registry.getPrometheusRegistry());
    return requestTotal;
  }

  @Bean(name = "requestError")
  public Counter getRequestError() {
    Counter requestTotal = Counter.build().name("method_counter_error")
    .labelNames("exName","methodEnd","env")
    .help("response Error couter of method")
    .register(registry.getPrometheusRegistry());
    return requestTotal;
  }

  @Bean(name = "responseSummary")
  public Summary getResponseSummary() {
    Summary responseSummary = Summary.build().name("method_summary_consuming")
    .labelNames("methodEnd","env")
    .help("response consuming of method")
    .register(registry.getPrometheusRegistry());
    return responseSummary;
  }


  @Bean(name = "apiRequestTotal")
  public Counter getApiTotal() {
    Counter requestTotal = Counter.build().name("api_couter_all")
    .labelNames("apiEnd","env")
    .help("total api request couter of api")
    .register(registry.getPrometheusRegistry());
    return requestTotal;
  }

  @Bean(name = "apiRequestError")
  public Counter getApiError() {
    Counter requestTotal = Counter.build().name("api_couter_error")
    .labelNames("exName","apiEnd","env")
    .help("api response Error couter of api")
    .register(registry.getPrometheusRegistry());
    return requestTotal;
  }

  @Bean(name = "apiResponseSummary")
  public Summary getApiSummary() {
    Summary responseSummary = Summary.build().name("api_summary_consuming")
    .labelNames("apiEnd","env")
    .help("api response consuming of api")
    .register(registry.getPrometheusRegistry());
    return responseSummary;
  }

}

