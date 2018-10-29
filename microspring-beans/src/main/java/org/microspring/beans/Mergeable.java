package org.microspring.beans;

import org.microspring.lang.Nullable;

public interface Mergeable {

    /**
     * 是否为此特定实例启用合并
     */
    boolean isMergeEnabled();

    /**
     * Merge the current value set with that of the supplied object.
     * <p>The supplied object is considered the parent, and values in
     * the callee's value set must override those of the supplied object.
     * @param parent the object to merge with
     * @return the result of the merge operation
     * @throws IllegalArgumentException if the supplied parent is {@code null}
     * @throws IllegalStateException if merging is not enabled for this instance
     * (i.e. {@code mergeEnabled} equals {@code false}).
     */
    Object merge(@Nullable Object parent);

}
