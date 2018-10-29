package org.microspring.context;

import org.microspring.beans.factory.Aware;
import org.microspring.core.env.Environment;

public interface EnvironmentAware extends Aware {

    /**
     * Set the {@code Environment} that this component runs in.
     */
    void setEnvironment(Environment environment);
}
