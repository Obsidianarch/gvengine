package com.addonovan.gvengine;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

/**
 * Tests if your system meets the minimum requirements for OpenGL.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public class RequirementsTester
{

    /**
     * Prints if the current computer meets the minimum requirements.
     *
     * @param args
     *         Command line arguments.
     *
     * @throws Exception
     *         If there was a problem with LWJGL.
     *
     * @since 14.03.30
     */
    public static void main( String[] args ) throws Exception
    {
        TestingHelper.createDisplay();

        ContextCapabilities context = GLContext.getCapabilities();

        System.out.println( "     OpenGL 3.1 = " + context.OpenGL31 );
        System.out.println( "            VAO = " + context.GL_ARB_vertex_array_object );
        System.out.println( "  Vertex Shader = " + context.GL_ARB_vertex_shader );
        System.out.println( "Fragment Shader = " + context.GL_ARB_fragment_shader );

        TestingHelper.destroy();
    }

}
