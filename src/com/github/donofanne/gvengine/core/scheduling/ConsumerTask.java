package com.github.donofanne.gvengine.core.scheduling;

import java.util.function.Consumer;

/**
 * A task which uses a Consumer (which is passed an Object[], as a workaround).
 */
public class ConsumerTask extends Task
{

    //
    // Fields
    //

    /**
     * The consumer to execute.
     */
    public final Consumer< Object[] > consumer;

    /**
     * The parameters to pass to the consumer.
     */
    public final Object[] parameters;

    //
    // Constructor
    //

    /**
     * @param consumer
     *         The consumer to execute.
     * @param parameters
     *         The parameters to pass to the consumer.
     */
    public ConsumerTask( Consumer< Object[] > consumer, Object[] parameters )
    {
        this.consumer = consumer;
        this.parameters = parameters;
    }

    //
    // Overrides
    //

    @Override
    public void execute()
    {
        consumer.accept( parameters );
    }
}
