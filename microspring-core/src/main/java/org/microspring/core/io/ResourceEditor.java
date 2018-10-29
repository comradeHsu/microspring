package org.microspring.core.io;

import org.microspring.core.env.PropertyResolver;
import org.microspring.lang.Nullable;
import org.microspring.util.Assert;

public class ResourceEditor {

    private final ResourceLoader resourceLoader;

    @Nullable
    private PropertyResolver propertyResolver;

    private final boolean ignoreUnresolvablePlaceholders;

    /**
     * Create a new instance of the {@link ResourceEditor} class
     * using a {@link DefaultResourceLoader} and {@link StandardEnvironment}.
     */
    public ResourceEditor() {
        this(new DefaultResourceLoader(), null);
    }

    /**
     * Create a new instance of the {@link ResourceEditor} class
     * using the given {@link ResourceLoader} and {@link PropertyResolver}.
     * @param resourceLoader the {@code ResourceLoader} to use
     * @param propertyResolver the {@code PropertyResolver} to use
     */
    public ResourceEditor(ResourceLoader resourceLoader, @Nullable PropertyResolver propertyResolver) {
        this(resourceLoader, propertyResolver, true);
    }

    /**
     * Create a new instance of the {@link ResourceEditor} class
     * using the given {@link ResourceLoader}.
     * @param resourceLoader the {@code ResourceLoader} to use
     * @param propertyResolver the {@code PropertyResolver} to use
     * @param ignoreUnresolvablePlaceholders whether to ignore unresolvable placeholders
     * if no corresponding property could be found in the given {@code propertyResolver}
     */
    public ResourceEditor(ResourceLoader resourceLoader, @Nullable PropertyResolver propertyResolver,
                          boolean ignoreUnresolvablePlaceholders) {

        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
        this.propertyResolver = propertyResolver;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }
}
