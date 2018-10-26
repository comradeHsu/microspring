package org.microspring.lang;

import javafx.beans.binding.When;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Nonnull(when = When.MAYBE)
//@TypeQualifierNickname
public @interface Nullable {
}
