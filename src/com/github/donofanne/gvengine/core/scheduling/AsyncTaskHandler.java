package com.github.donofanne.gvengine.core.scheduling;

/**
 * Schedules asynchronous events to be dispatched on various background threads,
 * so that long operations can be done without affecting foreground performance.
 */
public class AsyncTaskHandler
{

    //
    // Actions
    //

    /**
     * Launches a task on a background thread.
     *
     * @param task
     *          The task to launch.
     */
    protected static void launch( Task task )
    {
        Thread thread = new Thread( task::execute );
        thread.setName( "AsyncTask Background Thread" );
        thread.setPriority( 3 ); // tasks are very low priority so the main thread isn't disturbed
        thread.start();
    }

}
