package com.github.obsidianarch.gvengine.tests;

import com.github.obsidianarch.gvengine.core.shaders.Shader;
import com.github.obsidianarch.gvengine.core.shaders.ShaderProgram;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
import static org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_SHADER_ARB;
import static org.lwjgl.opengl.GL11.*;

/**
 * Tests the shaders to make sure they are working.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public class ShaderTester
{

    /**
     * Launches the shader test.
     *
     * @param args
     *         Command line arguments (ignored for this test).
     *
     * @throws LWJGLException
     *         If the window couldn't be created.
     * @throws IOException
     *         If the shader files couldn't be read.
     * @since 14.03.30
     */
    public static void main( String[] args ) throws LWJGLException, IOException
    {
        TestingHelper.createDisplay();
        TestingHelper.setupGL();

        Shader vertexShader = new Shader( GL_VERTEX_SHADER_ARB );
        Shader fragmentShader = new Shader( GL_FRAGMENT_SHADER_ARB );

        vertexShader.setSource( new File( "shaders/ambient/Vertex.glsl" ) );
        fragmentShader.setSource( new File( "shaders/ambient/Fragment.glsl" ) );

        System.out.printf( "Vertex: %b%n", vertexShader.getCompileStatus() );
        System.out.printf( ">  %s%n", vertexShader.getErrorLog() );

        System.out.printf( "Fragment: %b%n", fragmentShader.getCompileStatus() );
        System.out.printf( ">  %s%n", fragmentShader.getErrorLog() );

        ShaderProgram program = new ShaderProgram();
        program.attachShader( vertexShader );
        program.attachShader( fragmentShader );
        program.link();
        program.validate();

        System.out.printf( "Link %b%n", program.isLinked() );
        System.out.printf( "Validated %b%n", program.isValidated() );

        while ( !Display.isCloseRequested() )
        {
            program.enable();
            render();
            ShaderProgram.disable();

            TestingHelper.updateDisplay( "ShaderTest", 60 );
        }

        program.delete();
        fragmentShader.delete();
        vertexShader.delete();

    }

    /**
     * Renders the VBO.
     *
     * @since 14.03.30
     */
    public static void render()
    {
        glLoadIdentity();
        glColor3f( 1.0f, 1.0f, 1.0f ); //white

        glBegin( GL_QUADS );
        {
            glVertex3f( -1.0f, 1.0f, 0.0f );
            glVertex3f( 1.0f, 1.0f, 0.0f );
            glVertex3f( 1.0f, -1.0f, 0.0f );
            glVertex3f( -1.0f, -1.0f, 0.0f );
        }
        glEnd();

    }

}
