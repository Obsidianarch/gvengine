package com.github.obsidianarch.gvengine.core.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Austin
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface OptionListener {
    
    /**
     * @return The options which the method will be listeneing for.
     */
    String[] value();
    
}
