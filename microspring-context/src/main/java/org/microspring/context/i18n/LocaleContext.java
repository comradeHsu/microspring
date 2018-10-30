package org.microspring.context.i18n;

import org.microspring.lang.Nullable;

import java.util.Locale;

public interface LocaleContext {

    /**
     * Return the current Locale, which can be fixed or determined dynamically,
     * depending on the implementation strategy.
     * @return the current Locale, or {@code null} if no specific Locale associated
     */
    @Nullable
    Locale getLocale();
}
