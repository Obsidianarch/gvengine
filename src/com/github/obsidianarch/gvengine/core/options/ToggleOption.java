package com.github.obsidianarch.gvengine.core.options;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for denoting options which have a set number of options to "toggle" through.
 * 
 * @author Austin
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
@Documented
public @interface ToggleOption {
    
    /**
     * @return The possible values for the toggle option.
     */
    String[] value();

}
