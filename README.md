JFR Servlet [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.marschall/jfr-servlet/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.marschall/jfr-servlet) [![Javadocs](https://www.javadoc.io/badge/com.github.marschall/jfr-servlet.svg)](https://www.javadoc.io/doc/com.github.marschall/jfr-servlet)
===========

A servlet filter that generates [JFR](https://openjdk.java.net/jeps/328) events.

```xml
<dependency>
  <groupId>com.github.marschall</groupId>
  <artifactId>jfr-servlet</artifactId>
  <version>2.0.0</version>
</dependency>
```

![Flight Recording of some HTTP requests](https://github.com/marschall/jfr-servlet/raw/master/src/main/javadoc/Screenshot.png)

This project requires Java 11.

Versions 1.x are for the `javax.servlet` namespace, versions 2.x are for the `jakarta.servlet` namespace.

Usage
-----

If your web application is not [metadata-complete](https://www.oracle.com/technetwork/articles/javaee/javaee6overview-part2-136353.html) then you only need to add the dependency.

If your web application is `metadata-complete` then you manually need to add the filter `com.github.marschall.jfr.servlet.JfrFilter` and map it.


```xml
<filter>
  <filter-name>JfrFilter</filter-name>
  <filter-class>com.github.marschall.jfr.servlet.JfrFilter</filter-class>
  <async-supported>true</async-supported>
</filter>

<filter-mapping>
  <filter-name>JfrFilter</filter-name>
  <url-pattern>/*</url-pattern>
  <dispatcher>REQUEST</dispatcher>
  <dispatcher>FORWARD</dispatcher>
  <dispatcher>INCLUDE</dispatcher>
  <dispatcher>ERROR</dispatcher>
  <dispatcher>ASYNC</dispatcher>
</filter-mapping>
```

Correlating Dispatches
----------------------

A single request may traverse the servlet chain multiple times, either because of a server side redirect or because of asynchronous processing. We generate a unique exchangeId for every request so that multiple dispatches of the same request can be correlated.

