package com.github.obsidianarch.gvengine.core.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used on a method which should be executed whenever a given property is
 * changed.
 * 
 * @author Austin
 * 
 * @since 14.03.30
 * @version 14.03.30
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface OptionListener {
    
    /**
     * @return The options which the method will be listeneing for.
     */
    String[] value();
    
}
