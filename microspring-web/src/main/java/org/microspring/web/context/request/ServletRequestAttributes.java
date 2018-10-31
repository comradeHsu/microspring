package org.microspring.web.context.request;

import org.microspring.lang.Nullable;
import org.microspring.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletRequestAttributes {

    private final HttpServletRequest request;

    @Nullable
    private HttpServletResponse response;

    /**
     * Create a new ServletRequestAttributes instance for the given request.
     * @param request current HTTP request
     */
    public ServletRequestAttributes(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        this.request = request;
    }

    /**
     * Create a new ServletRequestAttributes instance for the given request.
     * @param request current HTTP request
     * @param response current HTTP response (for optional exposure)
     */
    public ServletRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response) {
        this(request);
        this.response = response;
    }
}
