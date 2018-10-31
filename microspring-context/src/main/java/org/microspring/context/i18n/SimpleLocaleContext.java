package org.microspring.context.i18n;

import org.microspring.lang.Nullable;

import java.util.Locale;

public class SimpleLocaleContext implements LocaleContext{

    @Nullable
    private final Locale locale;


    /**
     * Create a new SimpleLocaleContext that exposes the specified Locale.
     * Every {@link #getLocale()} call will return this Locale.
     * @param locale the Locale to expose, or {@code null} for no specific one
     */
    public SimpleLocaleContext(@Nullable Locale locale) {
        this.locale = locale;
    }

    @Override
    @Nullable
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public String toString() {
        return (this.locale != null ? this.locale.toString() : "-");
    }
}
