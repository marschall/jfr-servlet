package com.github.marschall.jfr.servlet;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Relational;
import jdk.jfr.StackTrace;

/**
 * Generates JFR events for HTTP exchanges.
 */
public final class JfrFilter implements Filter {

  static final String EXCHANGE_ID_ATTRIBUTE = "com.github.marschall.jfr.servlet.exchangeId";

  private static final AtomicLong EXCHANGE_ID_GENERATOR = new AtomicLong();

  @Override
  public void init(FilterConfig filterConfig) {
    // nothing
  }

  @Override
  public void destroy() {
    // nothing
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    Long exchangeId = (Long) request.getAttribute(EXCHANGE_ID_ATTRIBUTE);
    if (exchangeId != null) {
      // dispatched request
      this.filterRelatedRequest(exchangeId, request, response, chain);

    } else {
      this.filterNewRequest(request, response, chain);
    }
  }

  private void filterNewRequest(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    long newExchangeId = generateExchangeId();
    request.setAttribute(EXCHANGE_ID_ATTRIBUTE, newExchangeId);

    HttpEvent event = new HttpEvent();
    event.setExchangeId(newExchangeId);
    if (request instanceof HttpServletRequest) {
      copyHttpRequestAttributes((HttpServletRequest) request, event);
    }

    event.begin();
    try {
      chain.doFilter(request, response);
      if (response instanceof HttpServletResponse) {
        copyResponeAttributes((HttpServletResponse) response, event);
      }
    } finally {
      event.end();
      event.commit();
    }
  }

  private static void copyHttpRequestAttributes(HttpServletRequest httpRequest, HttpEvent event) {
    event.setMethod(httpRequest.getMethod());
    event.setUri(httpRequest.getRequestURI());
    event.setQuery(httpRequest.getQueryString());
    event.setDispatcherType(httpRequest.getDispatcherType().name());
  }

  private static void copyResponeAttributes(HttpServletResponse httpRespone, HttpEvent event) {
    event.setStatus(httpRespone.getStatus());
  }

  private void filterRelatedRequest(long exchangeId, ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    RelatedHttpEvent event = new RelatedHttpEvent();
    event.setExchangeId(exchangeId);
    event.setDispatcherType(request.getDispatcherType().name());
    try {
      chain.doFilter(request, response);
    } finally {
      event.end();
      event.commit();
    }
  }

  private static long generateExchangeId() {
    return EXCHANGE_ID_GENERATOR.incrementAndGet();
  }

  @Label("Exchange Id")
  @Description("Id to track requests that have been dispatched multiple times")
  @Relational
  @Target(FIELD)
  @Retention(RUNTIME)
  @interface ExchangeId {

  }

  @Label("related HTTP exchange")
  @Description("An HTTP exchange related to a different event")
  @Category("HTTP")
  @StackTrace(false)
  static class RelatedHttpEvent extends Event {

    @Label("Dispatcher Type")
    @Description("The dispatcher type of this request")
    private String dispatcherType;

    @ExchangeId
    private long exchangeId;

    String getDispatcherType() {
      return this.dispatcherType;
    }

    void setDispatcherType(String dispatcherType) {
      this.dispatcherType = dispatcherType;
    }

    long getExchangeId() {
      return this.exchangeId;
    }

    void setExchangeId(long exchangeId) {
      this.exchangeId = exchangeId;
    }

  }

  @Label("HTTP exchange")
  @Description("An HTTP exchange")
  @Category("HTTP")
  @StackTrace(false)
  static class HttpEvent extends Event {

    @Label("Method")
    @Description("The HTTP method")
    private String method;

    @Label("URI")
    @Description("The request URI")
    private String uri;

    @Label("Query")
    @Description("The query string")
    private String query;

    @Label("Status")
    @Description("The HTTP response status code")
    private int status;

    @Label("Dispatcher Type")
    @Description("The dispatcher type of this request")
    private String dispatcherType;

    @ExchangeId
    private long exchangeId;

    String getMethod() {
      return this.method;
    }

    void setMethod(String operationName) {
      this.method = operationName;
    }

    String getUri() {
      return this.uri;
    }

    void setUri(String query) {
      this.uri = query;
    }

    String getQuery() {
      return this.query;
    }

    void setQuery(String query) {
      this.query = query;
    }

    int getStatus() {
      return this.status;
    }

    void setStatus(int status) {
      this.status = status;
    }

    String getDispatcherType() {
      return this.dispatcherType;
    }

    void setDispatcherType(String dispatcherType) {
      this.dispatcherType = dispatcherType;
    }

    long getExchangeId() {
      return this.exchangeId;
    }

    void setExchangeId(long exchangeId) {
      this.exchangeId = exchangeId;
    }

  }

}
