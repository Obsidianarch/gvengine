package com.github.obsidianarch.gvengine.core.options;

import java.lang.annotation.*;

/**
 * A preference that is set by a slider.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SliderOption
{

    /**
     * @return The minimum value of the slider, default is 0.
     */
    int minimum() default 0;

    /**
     * @return The maximum value of the slider, default is 100.
     */
    int maximum() default 100;

}
