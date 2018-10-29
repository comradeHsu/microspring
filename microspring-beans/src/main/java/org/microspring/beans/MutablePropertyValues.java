package org.microspring.beans;

import org.microspring.lang.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MutablePropertyValues implements PropertyValues,Serializable {

    private final List<PropertyValue> propertyValueList;

    @Nullable
    private Set<String> processedProperties;

    private volatile boolean converted = false;

    /**
     * 创建一个空MutablePropertyValues对象
     * 并初始化propertyValueList属性
     */
    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList<>(0);
    }

    /**
     * Add a PropertyValue object, replacing any existing one for the
     * corresponding property or getting merged with it (if applicable).
     * @param pv the PropertyValue object to add
     * @return this in order to allow for adding multiple property values in a chain
     */
    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for (int i = 0; i < this.propertyValueList.size(); i++) {
            PropertyValue currentPv = this.propertyValueList.get(i);
            if (currentPv.getName().equals(pv.getName())) {
                pv = mergeIfRequired(pv, currentPv);
                setPropertyValueAt(pv, i);
                return this;
            }
        }
        this.propertyValueList.add(pv);
        return this;
    }

    /**
     * Modify a PropertyValue object held in this object.
     * Indexed from 0.
     */
    public void setPropertyValueAt(PropertyValue pv, int i) {
        this.propertyValueList.set(i, pv);
    }


    /**
     * Merges the value of the supplied 'new' {@link PropertyValue} with that of
     * the current {@link PropertyValue} if merging is supported and enabled.
     * @see Mergeable
     */
    private PropertyValue mergeIfRequired(PropertyValue newPv, PropertyValue currentPv) {
        Object value = newPv.getValue();
        if (value instanceof Mergeable) {
            Mergeable mergeable = (Mergeable) value;
            if (mergeable.isMergeEnabled()) {
                Object merged = mergeable.merge(currentPv.getValue());
                return new PropertyValue(newPv.getName(), merged);
            }
        }
        return newPv;
    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return new PropertyValue[0];
    }

    @Override
    public PropertyValue getPropertyValue(String propertyName) {
        return null;
    }

    @Override
    public PropertyValues changesSince(PropertyValues old) {
        return null;
    }

    @Override
    public boolean contains(String propertyName) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
