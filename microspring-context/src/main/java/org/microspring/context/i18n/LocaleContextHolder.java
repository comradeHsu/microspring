package org.microspring.context.i18n;

import org.microspring.core.NamedInheritableThreadLocal;
import org.microspring.core.NamedThreadLocal;
import org.microspring.lang.Nullable;

import java.util.Locale;
import java.util.TimeZone;

public final class LocaleContextHolder {

    private static final ThreadLocal<LocaleContext> localeContextHolder =
            new NamedThreadLocal<>("LocaleContext");

    private static final ThreadLocal<LocaleContext> inheritableLocaleContextHolder =
            new NamedInheritableThreadLocal<>("LocaleContext");

    // Shared default locale at the framework level
    @Nullable
    private static Locale defaultLocale;

    // Shared default time zone at the framework level
    @Nullable
    private static TimeZone defaultTimeZone;


    private LocaleContextHolder() {
    }

    /**
     * Return the LocaleContext associated with the current thread, if any.
     * @return the current LocaleContext, or {@code null} if none
     */
    @Nullable
    public static LocaleContext getLocaleContext() {
        LocaleContext localeContext = localeContextHolder.get();
        if (localeContext == null) {
            localeContext = inheritableLocaleContextHolder.get();
        }
        return localeContext;
    }
}
