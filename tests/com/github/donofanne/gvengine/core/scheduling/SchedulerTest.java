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

    //
    // Tests
    //

    @org.junit.Test
    public void testLaunch() throws Exception
    {
        Scheduler.launch( this::output );
        Scheduler.launch( this::output, "Hello World! (Consumers)" );
    }

    @org.junit.Test
    public void testEnqueue() throws Exception
    {
        Scheduler.enqueue( this::output );
        Scheduler.enqueue( this::output, "Hello World! (Consumers)" );
        Scheduler.doTick();
    }

}