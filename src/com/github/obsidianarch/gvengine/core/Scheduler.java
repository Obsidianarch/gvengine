package com.github.obsidianarch.gvengine.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.Sys;

import com.github.obsidianarch.gvengine.core.options.Option;
import com.github.obsidianarch.gvengine.core.options.SliderOption;
import com.github.obsidianarch.gvengine.core.options.ToggleOption;

/**
 * Maintains a schedule of events and when they need to be executed.
 * 
 * @author Austin
 */
public class Scheduler {
    
    //
    // Options
    //
    
    /** The maximum number of events dispatched every tick. */
    @Option( description = "Maximum events", screenName = "Maximum events per tick", x = -1, y = -1 )
    @SliderOption( minimum = 10, maximum = 500 )
    public static int            MaxEvents            = 50;
    
    /** When true, the timed events will be restricted to the {@code MaxEvents} as well. */
    @Option( description = "Timed Events Throttled", screenName = "Timed Events are restricted to the Maximum Events number", x = -1, y = -1 )
    @ToggleOption( options = { "false", "true" }, descriptions = { "Disabled (recommended)", "Enabled" } )
    public static boolean        TimedEventsThrottled = false;
    
    //
    // Fields
    //
    
    /** The list of events which have timers attached. */
    private static List< Event > timedEvents          = new ArrayList< Event >();
    
    /** The list of events which have just been scheduled. (FIFO) */
    private static List< Event > events               = new LinkedList<>();
    
    //
    // Schedulers
    //
    
    /**
     * Schedules an event {@code time} milliseconds in the future. So
     * {@code scheduleEvent( "aMethod", anObject, 5000 )} will
     * execute anObject's method {@code aMethod()} in 5 seconds.
     * 
     * @param method
     *            The method to execute.
     * @param target
     *            The object upon which the method will be invoked.
     * @param time
     *            The time (in milliseconds) until the event will be fired.
     */
    public static void scheduleEvent( String method, Object target, long time ) {
        Event e = new Event();
        
        try {
            e.action = target.getClass().getMethod( method );
        }
        catch ( NoSuchMethodException | SecurityException e1 ) {
            e1.printStackTrace(); // hopefully will never be thrown
        }
        
        e.target = target;
        e.executionTime = Sys.getTime() + MathHelper.toTicks( time );
        
        addEvent( e ); // add the event to the list
    }
    
    /**
     * Schedules and event to run, this will have a lower priority than events which have
     * been scheduled to run at a certain time.
     * 
     * @param method
     *            The method to execute.
     * @param target
     *            The object upon which the method will be invoked.
     */
    public static void scheduleEvent( String method, Object target ) {
        Event e = new Event();
        
        try {
            e.action = target.getClass().getMethod( method );
        }
        catch ( Exception e1 ) {
            e1.printStackTrace(); // hopefully will never be thrown
        }
        
        e.target = target;
        e.executionTime = -1;
        
        addEvent( e ); // add the event to the list
    }
    
    //
    // Actions
    //
    
    /**
     * Updates the scheduler, and fires events which need to be fired.
     */
    public static void doTick() {
        int firedEvents = 0;
        
        Iterator< Event > it = timedEvents.iterator(); // get the iterator for the timed events
        while ( it.hasNext() ) {
            Event e = it.next(); // get the next event
            
            // if the timed events are throttled too, then we may have to break out of the loop
            if ( TimedEventsThrottled && ( firedEvents >= MaxEvents ) ) break;
            
            // everything past this point will have a higher execution time than this one as well, and we haven't gotten to this time yet
            if ( e.executionTime > Sys.getTime() ) break;
            
            try {
                e.action.invoke( e.target ); // invoke the method
                if ( TimedEventsThrottled ) firedEvents++;
            }
            catch ( Exception ex ) {
                System.err.println( "Scheduler failed to invoke \"" + e.action.getName() + "()\"" );
                ex.printStackTrace();
            }
            
            it.remove(); // remove the iterated objects
        }
        
        it = events.iterator(); // get the iterator for the untimed events
        while ( it.hasNext() ) {
            Event e = it.next(); // get the next event
            
            // we've reached the max number of events we can fire for now
            if ( TimedEventsThrottled && ( firedEvents >= MaxEvents ) ) break;
            
            try {
                e.action.invoke( e.target ); // invoke the method
                firedEvents++;
            }
            catch ( Exception ex ) {
                System.err.println( "Scheduler failed to invoke \"" + e.action.getName() + "()\"" );
                ex.printStackTrace();
            }
            
            it.remove(); // remove the iterated object
        }
    }
    
    /**
     * Adds the event into the correct collection, and if it's timed adds it into the list
     * at the correct position.
     * 
     * @param e
     *            The event to schedule.
     */
    private static void addEvent( Event e ) {
        // if there is not timed constraint
        if ( e.executionTime == -1 ) {
            events.add( e );
            return;
        }
        
        // add the timed event into the list at the correct position, based on the surronding items
        for ( int i = 0; i < timedEvents.size(); i++ ) {
            Event event = timedEvents.get( i ); // get the event from the list
            
            if ( event.executionTime > e.executionTime ) { // this event is scheduled later than this one
                timedEvents.add( i, e );
                break; // we've added it, no reason to continue doing so
            }
        }
    }
    
    //
    // Nested Classes
    //
    
    /**
     * A container for the method which will be executed, the class which will be
     * executing the method, and the time at which the method will be fired.
     * 
     * @author Austin
     */
    private static class Event {
        
        /** The action for this event. */
        public Method action;
        
        /** The executor. */
        public Object target;
        
        /** The time this event should be executed at. -1 if there is not timing priority. */
        public long   executionTime;
        
    }
    
}
