package com.github.donofanne.gvengine.core.scheduling;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The scheduler which handles all of the synchronous tasks, tasks which much be performed on the same thread.
 */
final class SyncTaskHandler
{

    //
    // Fields
    //

    /**
     * The tasks to do whenever time's available.
     */
    private static ArrayList< Task > tasks = new ArrayList<>();

    //
    // Actions
    //

    /**
     * Performs the various enqueued tasks in the prioritized orders.
     *
     * @param endTime
     *         The time by which all the tasks must be completed.
     */
    static void doTick( double endTime )
    {
        doEnqueuedTasks( endTime );
    }

    /**
     * Performs all the lowest priority tasks it can before reaching the end time.
     *
     * @param endTime
     *         The time all events must end in order to maintain framerate.
     */
    private static void doEnqueuedTasks( double endTime )
    {
        Iterator< Task > taskIterator = tasks.iterator();
        while ( taskIterator.hasNext() && GLFW.glfwGetTime() < endTime )
        {
            taskIterator.next().execute();
            taskIterator.remove();
        }
    }

    //
    // Adding Tasks
    //

    /**
     * Enqueues a task to be executed at a later time.
     *
     * @param task
     *         The task to enqueue.
     */
    protected static void enqueue( Task task )
    {
        tasks.add( task );
    }

}
