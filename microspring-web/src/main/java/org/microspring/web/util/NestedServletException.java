package org.microspring.web.util;

import com.sun.istack.internal.Nullable;
import org.microspring.core.NestedExceptionUtils;

import javax.servlet.ServletException;

public class NestedServletException extends ServletException {

    /** Use serialVersionUID from Spring 1.2 for interoperability. */
    private static final long serialVersionUID = -5292377985529381145L;

    static {
        // Eagerly load the NestedExceptionUtils class to avoid classloader deadlock
        // issues on OSGi when calling getMessage(). Reported by Don Brown; SPR-5607.
        NestedExceptionUtils.class.getName();
    }


    /**
     * Construct a {@code NestedServletException} with the specified detail message.
     * @param msg the detail message
     */
    public NestedServletException(String msg) {
        super(msg);
    }

    /**
     * Construct a {@code NestedServletException} with the specified detail message
     * and nested exception.
     * @param msg the detail message
     * @param cause the nested exception
     */
    public NestedServletException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }


    /**
     * Return the detail message, including the message from the nested exception
     * if there is one.
     */
    @Override
    @Nullable
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }
}
