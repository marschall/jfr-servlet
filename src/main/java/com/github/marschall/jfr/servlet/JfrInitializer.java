package com.github.marschall.jfr.servlet;

import java.util.EnumSet;
import java.util.Set;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration.Dynamic;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;

/**
 * Registers {@link JfrFilter} for {@link DispatcherType#REQUEST}
 */
public final class JfrInitializer implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) {
    Dynamic registration = ctx.addFilter("jfrFilter", JfrFilter.class);
    registration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    registration.setAsyncSupported(true);
  }

}
