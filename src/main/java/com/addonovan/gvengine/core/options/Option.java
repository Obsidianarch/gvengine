package com.addonovan.gvengine.core.options;

import java.lang.annotation.*;

/**
 * Denotes an option for the OptionManager to account for.
 *
 * @author Austin
 * @version 14.04.03
 * @since 14.03.30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Option
{

    /**
     * @return The description of the option that appears to the user.
     */
    String value();

    /**
     * @return If the OptionManager should change the value for this option automatically or let the option listener do so.
     */
    boolean autoValueChange() default true;

}
