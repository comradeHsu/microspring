package org.microspring.beans;

import org.microspring.lang.Nullable;
import org.microspring.util.Assert;

import java.io.Serializable;

public class PropertyValue implements Serializable {

    private final String name;

    @Nullable
    private final Object value;

    /**
     * Create a new PropertyValue instance.
     * @param name the name of the property (never {@code null})
     * @param value the value of the property (possibly before type conversion)
     */
    public PropertyValue(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.value = value;
    }

    /**
     * Return the name of the property.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the value of the property.
     * <p>Note that type conversion will <i>not</i> have occurred here.
     * It is the responsibility of the BeanWrapper implementation to
     * perform type conversion.
     */
    @Nullable
    public Object getValue() {
        return this.value;
    }
}
