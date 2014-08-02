package com.github.obsidianarch.gvengine.core.options;

import java.lang.annotation.*;

/**
 * Used for denoting options which have a set number of options to "toggle" through.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ToggleOption
{

    /**
     * @return The possible values for the toggle option.
     */
    String[] value();

}
