package org.microspring.web.context.request;

import org.microspring.core.NamedInheritableThreadLocal;
import org.microspring.core.NamedThreadLocal;
import org.microspring.lang.Nullable;

public abstract class RequestContextHolder {

    private static final ThreadLocal<RequestAttributes> requestAttributesHolder =
            new NamedThreadLocal<>("Request attributes");

    private static final ThreadLocal<RequestAttributes> inheritableRequestAttributesHolder =
            new NamedInheritableThreadLocal<>("Request context");

    /**
     * Return the RequestAttributes currently bound to the thread.
     * @return the RequestAttributes currently bound to the thread,
     * or {@code null} if none bound
     */
    @Nullable
    public static RequestAttributes getRequestAttributes() {
        RequestAttributes attributes = requestAttributesHolder.get();
        if (attributes == null) {
            attributes = inheritableRequestAttributesHolder.get();
        }
        return attributes;
    }
}
