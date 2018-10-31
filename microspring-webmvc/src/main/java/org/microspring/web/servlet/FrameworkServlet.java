package org.microspring.web.servlet;

import org.microspring.context.i18n.LocaleContext;
import org.microspring.context.i18n.LocaleContextHolder;
import org.microspring.context.i18n.SimpleLocaleContext;
import org.microspring.lang.Nullable;
import org.microspring.util.StringUtils;
import org.microspring.web.context.request.NativeWebRequest;
import org.microspring.web.context.request.RequestAttributes;
import org.microspring.web.context.request.RequestContextHolder;
import org.microspring.web.context.request.ServletRequestAttributes;
import org.microspring.web.context.request.async.CallableProcessingInterceptor;
import org.microspring.web.context.request.async.WebAsyncManager;
import org.microspring.web.context.request.async.WebAsyncUtils;
import org.microspring.web.util.NestedServletException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;

public abstract class FrameworkServlet extends HttpServletBean{

    /** Should we dispatch an HTTP OPTIONS request to {@link #doService}?. */
    private boolean dispatchOptionsRequest = false;

    /** Should we dispatch an HTTP TRACE request to {@link #doService}?. */
    private boolean dispatchTraceRequest = false;

    /**
     * Delegate GET requests to processRequest/doService.
     * <p>Will also be invoked by HttpServlet's default implementation of {@code doHead},
     * with a {@code NoBodyResponse} that just captures the content length.
     * @see #doService
     * @see #doHead
     */
    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    /**
     * Delegate POST requests to {@link #processRequest}.
     * @see #doService
     */
    @Override
    protected final void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    /**
     * Delegate PUT requests to {@link #processRequest}.
     * @see #doService
     */
    @Override
    protected final void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    /**
     * Delegate DELETE requests to {@link #processRequest}.
     * @see #doService
     */
    @Override
    protected final void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    /**
     * Delegate OPTIONS requests to {@link #processRequest}, if desired.
     * <p>Applies HttpServlet's standard OPTIONS processing otherwise,
     * and also if there is still no 'Allow' header set after dispatching.
     * @see #doService
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (this.dispatchOptionsRequest || CorsUtils.isPreFlightRequest(request)) {
            processRequest(request, response);
            if (response.containsHeader("Allow")) {
                // Proper OPTIONS response coming from a handler - we're done.
                return;
            }
        }

        // Use response wrapper in order to always add PATCH to the allowed methods
        super.doOptions(request, new HttpServletResponseWrapper(response) {
            @Override
            public void setHeader(String name, String value) {
                if ("Allow".equals(name)) {
                    value = (StringUtils.hasLength(value) ? value + ", " : "") + HttpMethod.PATCH.name();
                }
                super.setHeader(name, value);
            }
        });
    }

    /**
     * Delegate TRACE requests to {@link #processRequest}, if desired.
     * <p>Applies HttpServlet's standard TRACE processing otherwise.
     * @see #doService
     */
    @Override
    protected void doTrace(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (this.dispatchTraceRequest) {
            processRequest(request, response);
            if ("message/http".equals(response.getContentType())) {
                // Proper TRACE response coming from a handler - we're done.
                return;
            }
        }
        super.doTrace(request, response);
    }


    /**
     * Process this request, publishing an event regardless of the outcome.
     * <p>The actual event handling is performed by the abstract
     * {@link #doService} template method.
     */
    protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        Throwable failureCause = null;

        LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
        LocaleContext localeContext = buildLocaleContext(request);

        RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);

        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());

        initContextHolders(request, localeContext, requestAttributes);

        try {
            doService(request, response);
        }
        catch (ServletException | IOException ex) {
            failureCause = ex;
            throw ex;
        }
        catch (Throwable ex) {
            failureCause = ex;
            throw new NestedServletException("Request processing failed", ex);
        }

        finally {
            resetContextHolders(request, previousLocaleContext, previousAttributes);
            if (requestAttributes != null) {
                requestAttributes.requestCompleted();
            }
            logResult(request, response, failureCause, asyncManager);
            publishRequestHandledEvent(request, response, startTime, failureCause);
        }
    }

    /**
     * Build a LocaleContext for the given request, exposing the request's
     * primary locale as current locale.
     * @param request current HTTP request
     * @return the corresponding LocaleContext, or {@code null} if none to bind
     * @see LocaleContextHolder#setLocaleContext
     */
    @Nullable
    protected LocaleContext buildLocaleContext(HttpServletRequest request) {
        return new SimpleLocaleContext(request.getLocale());
    }

    /**
     * Build ServletRequestAttributes for the given request (potentially also
     * holding a reference to the response), taking pre-bound attributes
     * (and their type) into consideration.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param previousAttributes pre-bound RequestAttributes instance, if any
     * @return the ServletRequestAttributes to bind, or {@code null} to preserve
     * the previously bound instance (or not binding any, if none bound before)
     * @see RequestContextHolder#setRequestAttributes
     */
    @Nullable
    protected ServletRequestAttributes buildRequestAttributes(HttpServletRequest request,
                                                              @Nullable HttpServletResponse response, @Nullable RequestAttributes previousAttributes) {

        if (previousAttributes == null || previousAttributes instanceof ServletRequestAttributes) {
            return new ServletRequestAttributes(request, response);
        }
        else {
            return null;  // preserve the pre-bound RequestAttributes instance
        }
    }

    /**
     * Subclasses must implement this method to do the work of request handling,
     * receiving a centralized callback for GET, POST, PUT and DELETE.
     * <p>The contract is essentially the same as that for the commonly overridden
     * {@code doGet} or {@code doPost} methods of HttpServlet.
     * <p>This class intercepts calls to ensure that exception handling and
     * event publication takes place.
     * @param request current HTTP request
     * @param response current HTTP response
     * @throws Exception in case of any kind of processing failure
     * @see javax.servlet.http.HttpServlet#doGet
     * @see javax.servlet.http.HttpServlet#doPost
     */
    protected abstract void doService(HttpServletRequest request, HttpServletResponse response)
            throws Exception;

    /**
     * CallableProcessingInterceptor implementation that initializes and resets
     * FrameworkServlet's context holders, i.e. LocaleContextHolder and RequestContextHolder.
     */
    private class RequestBindingInterceptor implements CallableProcessingInterceptor {

        @Override
        public <T> void preProcess(NativeWebRequest webRequest, Callable<T> task) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
                initContextHolders(request, buildLocaleContext(request),
                        buildRequestAttributes(request, response, null));
            }
        }
        @Override
        public <T> void postProcess(NativeWebRequest webRequest, Callable<T> task, Object concurrentResult) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                resetContextHolders(request, null, null);
            }
        }
    }

}
