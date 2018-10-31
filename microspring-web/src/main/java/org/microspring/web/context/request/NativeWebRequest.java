package org.microspring.web.context.request;

import org.microspring.lang.Nullable;

public interface NativeWebRequest extends WebRequest{

    /**
     * Return the underlying native request object.
     * @see javax.servlet.http.HttpServletRequest
     */
    Object getNativeRequest();

    /**
     * Return the underlying native response object, if any.
     * @see javax.servlet.http.HttpServletResponse
     */
    @Nullable
    Object getNativeResponse();

    /**
     * Return the underlying native request object, if available.
     * @param requiredType the desired type of request object
     * @return the matching request object, or {@code null} if none
     * of that type is available
     * @see javax.servlet.http.HttpServletRequest
     */
    @Nullable
    <T> T getNativeRequest(@Nullable Class<T> requiredType);

    /**
     * Return the underlying native response object, if available.
     * @param requiredType the desired type of response object
     * @return the matching response object, or {@code null} if none
     * of that type is available
     * @see javax.servlet.http.HttpServletResponse
     */
    @Nullable
    <T> T getNativeResponse(@Nullable Class<T> requiredType);
}
