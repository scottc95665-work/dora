package gov.ca.cwds.rest.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.io.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.auth.PerryUserIdentity;
import gov.ca.cwds.logging.LoggingContext;
import gov.ca.cwds.logging.LoggingContext.LogParameter;
import gov.ca.cwds.rest.api.ApiException;

/**
 * @author CWDS API Team
 */
@Provider
public class RequestResponseLoggingFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

  private LoggingContext loggingContext;

  /**
   * Constructor
   *
   * @param loggingContext API logging context
   */
  @Inject
  public RequestResponseLoggingFilter(LoggingContext loggingContext) {
    this.loggingContext = loggingContext;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String uniqueId = loggingContext.initialize();

    if (request instanceof HttpServletRequest) {
      final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      final HttpServletResponse httpServletResponse = (HttpServletResponse) response;

      RequestExecutionContextImpl.startRequest();
      setLoggingContextParameters(uniqueId, httpServletRequest, httpServletResponse);

      RequestResponseLoggingHttpServletRequest wrappedRequest =
          new RequestResponseLoggingHttpServletRequest(httpServletRequest);

      RequestResponseLoggingHttpServletResponseWrapper wrappedResponse =
          new RequestResponseLoggingHttpServletResponseWrapper(httpServletResponse);

      try {
        chain.doFilter(wrappedRequest, wrappedResponse);
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        throw new ApiException("Unable to handle request:" + uniqueId, e);
      } finally {
        loggingContext.close();
        RequestExecutionContextImpl.stopRequest();
      }
    }
  }

  private void setLoggingContextParameters(String uniqueId, HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) {
    PerryUserIdentity user = (PerryUserIdentity) RequestExecutionContext.instance()
        .get(RequestExecutionContext.Parameter.USER_IDENTITY);

    if (user != null) {
      loggingContext.setLogParameter(LogParameter.USER_ID, user.getUser());
      loggingContext.setLogParameter(LogParameter.STAFF_ID, user.getStaffId());
      loggingContext.setLogParameter(LogParameter.STAFF_COUNTY, user.getCountyCwsCode());
    }

    loggingContext.setLogParameter(LogParameter.REMOTE_ADDRESS, httpServletRequest.getRemoteAddr());

    String sessionId = httpServletRequest.getHeader(LogParameter.SESSION_ID.name());
    String requestId = httpServletRequest.getHeader(LogParameter.REQUEST_ID.name());

    loggingContext.setLogParameter(LogParameter.REQUEST_ID,
        StringUtils.isBlank(requestId) ? uniqueId : requestId);
    loggingContext.setLogParameter(LogParameter.SESSION_ID, sessionId);

    final int responseStatus = httpServletResponse.getStatus();
    loggingContext.setLogParameter(LogParameter.RESPONSE_STATUS, String.valueOf(responseStatus));
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Shall override parent abstract method but nothing to do
  }

  @Override
  public void destroy() {
    // Shall override parent abstract method but nothing to do
  }

  protected static class RequestResponseLoggingHttpServletRequest
      extends HttpServletRequestWrapper {

    private final byte[] body;
    private final HttpServletRequest wrappedRequest;

    RequestResponseLoggingHttpServletRequest(HttpServletRequest request) throws IOException {
      super(request);
      body = IOUtils.toByteArray(request.getInputStream());
      wrappedRequest = request;
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
      return wrappedRequest.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.servlet.ServletRequestWrapper#getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
      return new ServletInputStream() {

        @Override
        public int read() {
          return byteArrayInputStream.read();
        }

        @Override
        public boolean isFinished() {
          return false;
        }

        @Override
        public boolean isReady() {
          return false;
        }

        @Override
        public void setReadListener(ReadListener arg0) {
          // Shall override parent abstract method but nothing to do
        }
      };
    }
  }

  static private class RequestResponseLoggingHttpServletResponseWrapper
      extends HttpServletResponseWrapper {

    private TeeServletOutputStream teeStream;

    private PrintWriter teeWriter;

    private ByteArrayOutputStream bos;

    private HttpServletResponse wrappedResponse;

    RequestResponseLoggingHttpServletResponseWrapper(HttpServletResponse response) {
      super(response);
      wrappedResponse = response;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
      if (this.teeWriter == null) {
        this.teeWriter = new PrintWriter(new OutputStreamWriter(getOutputStream()));
      }
      return this.teeWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      if (teeStream == null) {
        bos = new ByteArrayOutputStream();
        teeStream = new TeeServletOutputStream(getResponse().getOutputStream(), bos);
      }
      return teeStream;
    }

    @Override
    public void flushBuffer() throws IOException {
      if (teeStream != null) {
        teeStream.flush();
      }
      if (this.teeWriter != null) {
        this.teeWriter.flush();
      }
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
      return wrappedResponse.toString();
    }

    static private class TeeServletOutputStream extends ServletOutputStream {

      private final TeeOutputStream targetStream;

      TeeServletOutputStream(OutputStream one, OutputStream two) {
        targetStream = new TeeOutputStream(one, two);
      }

      @Override
      public void write(int arg0) throws IOException {
        this.targetStream.write(arg0);
      }

      @Override
      public void flush() throws IOException {
        super.flush();
        this.targetStream.flush();
      }

      @Override
      public void close() throws IOException {
        super.close();
        this.targetStream.close();
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setWriteListener(WriteListener writeListener) {
        // Shall override parent abstract method but nothing to do
      }
    }
  }

}
