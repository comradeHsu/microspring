package org.microspring.web.context.request.async;

import org.microspring.web.context.request.NativeWebRequest;

import java.util.concurrent.Callable;

public interface CallableProcessingInterceptor {

    /**
     * Constant indicating that no result has been determined by this
     * interceptor, giving subsequent interceptors a chance.
     * @see #handleTimeout
     * @see #handleError
     */
    Object RESULT_NONE = new Object();

    /**
     * Constant indicating that the response has been handled by this interceptor
     * without a result and that no further interceptors are to be invoked.
     * @see #handleTimeout
     * @see #handleError
     */
    Object RESPONSE_HANDLED = new Object();


    /**
     * Invoked <em>before</em> the start of concurrent handling in the original
     * thread in which the {@code Callable} is submitted for concurrent handling.
     * <p>This is useful for capturing the state of the current thread just prior to
     * invoking the {@link Callable}. Once the state is captured, it can then be
     * transferred to the new {@link Thread} in
     * {@link #preProcess(NativeWebRequest, Callable)}. Capturing the state of
     * Spring Security's SecurityContextHolder and migrating it to the new Thread
     * is a concrete example of where this is useful.
     * <p>The default implementation is empty.
     * @param request the current request
     * @param task the task for the current async request
     * @throws Exception in case of errors
     */
    default <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
    }

    /**
     * Invoked <em>after</em> the start of concurrent handling in the async
     * thread in which the {@code Callable} is executed and <em>before</em> the
     * actual invocation of the {@code Callable}.
     * <p>The default implementation is empty.
     * @param request the current request
     * @param task the task for the current async request
     * @throws Exception in case of errors
     */
    default <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
    }

    /**
     * Invoked <em>after</em> the {@code Callable} has produced a result in the
     * async thread in which the {@code Callable} is executed. This method may
     * be invoked later than {@code afterTimeout} or {@code afterCompletion}
     * depending on when the {@code Callable} finishes processing.
     * <p>The default implementation is empty.
     * @param request the current request
     * @param task the task for the current async request
     * @param concurrentResult the result of concurrent processing, which could
     * be a {@link Throwable} if the {@code Callable} raised an exception
     * @throws Exception in case of errors
     */
    default <T> void postProcess(NativeWebRequest request, Callable<T> task,
                                 Object concurrentResult) throws Exception {
    }

    /**
     * Invoked from a container thread when the async request times out before
     * the {@code Callable} task completes. Implementations may return a value,
     * including an {@link Exception}, to use instead of the value the
     * {@link Callable} did not return in time.
     * <p>The default implementation always returns {@link #RESULT_NONE}.
     * @param request the current request
     * @param task the task for the current async request
     * @return a concurrent result value; if the value is anything other than
     * {@link #RESULT_NONE} or {@link #RESPONSE_HANDLED}, concurrent processing
     * is resumed and subsequent interceptors are not invoked
     * @throws Exception in case of errors
     */
    default <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        return RESULT_NONE;
    }

    /**
     * Invoked from a container thread when an error occurred while processing
     * the async request before the {@code Callable} task completes.
     * Implementations may return a value, including an {@link Exception}, to
     * use instead of the value the {@link Callable} did not return in time.
     * <p>The default implementation always returns {@link #RESULT_NONE}.
     * @param request the current request
     * @param task the task for the current async request
     * @param t the error that occurred while request processing
     * @return a concurrent result value; if the value is anything other than
     * {@link #RESULT_NONE} or {@link #RESPONSE_HANDLED}, concurrent processing
     * is resumed and subsequent interceptors are not invoked
     * @throws Exception in case of errors
     * @since 5.0
     */
    default <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
        return RESULT_NONE;
    }

    /**
     * Invoked from a container thread when async processing completes for any
     * reason including timeout or network error.
     * <p>The default implementation is empty.
     * @param request the current request
     * @param task the task for the current async request
     * @throws Exception in case of errors
     */
    default <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
    }
}
