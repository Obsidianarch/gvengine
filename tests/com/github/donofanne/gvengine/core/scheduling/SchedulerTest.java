package com.github.donofanne.gvengine.core.scheduling;

public class SchedulerTest
{

    //
    // Resources
    //

    public synchronized void output()
    {
        System.out.println( "Hello World! (Delegate)" );
    }

    public synchronized void output( Object[] params )
    {
        System.out.println( ( String ) params[ 0 ] );
    }

    public synchronized void output( String s )
    {
        System.out.println( s );
    }

    //
    // Tests
    //

    @org.junit.Test
    public void testLaunch() throws Exception
    {
        Scheduler.launch( this::output );
        Scheduler.launch( this::output, "Hello World! (Consumers)" );
        Scheduler.launch( this, "output", "Hello World! (Reflections)" );
    }

    @org.junit.Test
    public void testEnqueue() throws Exception
    {
        Scheduler.enqueue( this::output );
        Scheduler.enqueue( this::output, "Hello World! (Consumers)" );
        Scheduler.enqueue( this, "output", "Hello World! (Reflections) " );
        Scheduler.doTick();
    }

}