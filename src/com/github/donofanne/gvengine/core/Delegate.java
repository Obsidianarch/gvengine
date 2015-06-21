package com.github.donofanne.gvengine.core;

/**
 * A delegate method. Passed to the Scheduler. No parameters
 * or return values.
 *
 * I love method references.
 * Much nicer than the awful Reflections framework that
 * only gives RuntimeExceptions that hard af to debug. :-)
 */
@FunctionalInterface
public interface Delegate
{

    public void invoke();

}
