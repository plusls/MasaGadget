package com.plusls.MasaGadget.mixin;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ /* No targets allowed */})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {
    String modId();

    String[] version();

    @Nullable
    Class<? extends CustomDepPredicate>[] predicate() default {};
}
