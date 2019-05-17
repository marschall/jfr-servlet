package com.github.marschall.jfr.servlet;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.StackTrace;

/**
 * Generates JFR events for HTTP exchanges.
 */
public final class JfrFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (request.isAsyncStarted()) {
      // 
    }

    HttpEvent event = new HttpEvent();
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      event.setMethod(httpRequest.getMethod());
      event.setUri(httpRequest.getRequestURI());
      event.setQuery(httpRequest.getQueryString());
    }

    event.begin();
    try {
      chain.doFilter(request, response);
      if (response instanceof HttpServletResponse) {
        HttpServletResponse httpRespone = (HttpServletResponse) response;
        event.setStatus(httpRespone.getStatus());
      }
    } finally {
      event.end();
      event.commit();
    }

  }


  static final class AsyncJfrListener implements AsyncListener {
    
    private final HttpEvent event;

    AsyncJfrListener(HttpEvent event) {
      this.event = event;
    }

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
      endEvent("complete");
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
      endEvent("timeout");
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
      endEvent("error");
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
      // ignore
    }
    
    private void endEvent(String state) {
      this.event.end();
      this.event.commit();
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

  }

}
