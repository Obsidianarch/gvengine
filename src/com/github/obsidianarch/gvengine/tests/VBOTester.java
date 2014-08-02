package com.github.obsidianarch.gvengine.tests;

import com.github.obsidianarch.gvengine.core.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

/**
 * The first test, tests the validity of the methods of VertexBufferObject.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public class VBOTester
{

    /**
     * the VertexBufferObject we're testing.
     */
    private static VertexBufferObject vbo;

    /**
     * A second VBO for testing two at once. Great name, right?
     */
    private static VertexBufferObject vbo2;

    /**
     * Starts the test.
     *
     * @throws Exception
     *         If something couldn't be initialized.
     *
     * @since 14.03.30
     */
    private static void init() throws Exception
    {
        TestingHelper.isDeveloping(); // we just need to get the static initializer to get LWJGL linked, it's a terrible hack but it works

        Display.setDisplayMode( new DisplayMode( 640, 480 ) );
        Display.create();

        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        glOrtho( 0, 1, 0, 1, 1, -1 );
        glMatrixMode( GL_MODELVIEW );

        glEnableClientState( GL_VERTEX_ARRAY ); // enable vertex arrays
        glEnableClientState( GL_COLOR_ARRAY ); // enable color arrays

        vbo = new VertexBufferObject( PositionSystem.XY, ColorSystem.RGB, NormalSystem.DISABLED, 6, 9, 0 );
        vbo.setCoordinates( 0, 0, 1, 0, 0, 1 );
        vbo.setChannels( 1, 1, 1, 1, 1, 1, 1, 1, 1 );

        vbo2 = new VertexBufferObject( PositionSystem.XY, ColorSystem.RGB, NormalSystem.DISABLED, 6, 9, 0 );
        vbo2.setCoordinates( 1, 1, 1, 0, 0, 1 );
        vbo2.setChannels( 1, 0, 0, 1, 0, 0, 1, 0, 0 );
    }

    /**
     * Runs the test.
     *
     * @since 14.03.30
     */
    private static void run()
    {
        while ( !Display.isCloseRequested() )
        {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

            vbo.render();
            vbo2.render();

            Scheduler.doTick();

            Display.update();
            Display.sync( 60 );
        }
    }

    /**
     * Destroys the test.
     *
     * @since 14.03.30
     */
    private static void destroy()
    {
        Display.destroy();
    }

    /**
     * Starts the test.
     *
     * @param args
     *         The command line arguments.
     *
     * @throws Exception
     *         If a problem happened setting up or running the test.
     *
     * @since 14.03.30
     */
    public static void main( String[] args ) throws Exception
    {
        init();
        run();
        destroy();
    }
}
