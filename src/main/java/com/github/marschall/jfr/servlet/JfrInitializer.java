package com.github.marschall.jfr.servlet;

import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;

/**
 * Registers {@link JfrFilter} for {@link DispatcherType#REQUEST}
 */
public final class JfrInitializer implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) {
    Dynamic registration = ctx.addFilter("jfrFilter", JfrFilter.class);
    registration.addMappingForUrlPatterns(null, true, "/*");
    registration.setAsyncSupported(true);
  }

}
