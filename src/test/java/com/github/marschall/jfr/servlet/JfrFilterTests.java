package com.github.marschall.jfr.servlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JfrFilterTests {

  private JfrFilter filter;

  @BeforeEach
  void setUp() {
    this.filter = new JfrFilter();
  }

  @Test
  void filterInitalRequest() throws IOException, ServletException {
    // arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    // act
    this.filter.doFilter(request, response, chain);

    // assert
    assertNotNull(request.getAttribute(JfrFilter.EXCHANGE_ID_ATTRIBUTE));
  }

  @Test
  void filterFollowUpRequest() throws IOException, ServletException {
    // arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute(JfrFilter.EXCHANGE_ID_ATTRIBUTE, 1L);
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    // act
    this.filter.doFilter(request, response, chain);

    // assert
    assertNotNull(request.getAttribute(JfrFilter.EXCHANGE_ID_ATTRIBUTE));
  }

}
