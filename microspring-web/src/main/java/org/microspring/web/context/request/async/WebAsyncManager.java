package org.microspring.web.context.request.async;

import org.microspring.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

public final class WebAsyncManager {

    private final Map<Object, CallableProcessingInterceptor> callableInterceptors = new LinkedHashMap<>();

    /**
     * Register a {@link CallableProcessingInterceptor} under the given key.
     * @param key the key
     * @param interceptor the interceptor to register
     */
    public void registerCallableInterceptor(Object key, CallableProcessingInterceptor interceptor) {
        Assert.notNull(key, "Key is required");
        Assert.notNull(interceptor, "CallableProcessingInterceptor  is required");
        this.callableInterceptors.put(key, interceptor);
    }
}
