package org.microspring.beans;

public final class PropertyAccessorFactory {

    private PropertyAccessorFactory() {
    }


    /**
     * Obtain a BeanWrapper for the given target object,
     * accessing properties in JavaBeans style.
     * @param target the target object to wrap
     * @return the property accessor
     * @see BeanWrapperImpl
     */
    public static BeanWrapper forBeanPropertyAccess(Object target) {
        return new BeanWrapperImpl(target);
    }

}
