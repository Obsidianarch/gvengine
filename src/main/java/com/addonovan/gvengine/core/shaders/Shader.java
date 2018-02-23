package com.addonovan.gvengine.core.shaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

/**
 * Represents the lower level OpenGL shader objects. These are attached to a ShaderObject.
 *
 * @author Austin
 * @version 14.03.30
 * @since 14.03.30
 */
public class Shader
{

    //
    // Fields
    //

    /**
     * The type of shader this is.
     */
    private final int shaderType;

    /**
     * The ID for this shader.
     */
    private final int shaderID;

    /**
     * Did the shader compile successfully?
     */
    private boolean compiled;

    //
    // Constructors
    //

    /**
     * Constructs a shader.
     *
     * @param type
     *         The type of shader this is.
     *
     * @since 14.03.30
     */
    public Shader( int type )
    {
        shaderType = type;
        shaderID = glCreateShaderObjectARB( shaderType );
        compiled = false;
    }

    //
    // Actions
    //

    /**
     * Deletes the shader object from OpenGL.
     *
     * @since 14.03.30
     */
    public void delete()
    {
        glDeleteObjectARB( shaderID );
    }

    //
    // Setters
    //

    /**
     * @param f
     *         The file containing the source code of the shader.
     *
     * @throws IOException
     *         If the file couldn't be read.
     *
     * @since 14.03.30
     */
    public void setSource( File f ) throws IOException
    {
        try ( BufferedReader br = new BufferedReader( new FileReader( f ) ) )
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                sb.append( String.format( "%s%n", line ) );
            }

            setSource( sb.toString() );
        }
    }

    /**
     * @param s
     *         The source code of the shader.
     *
     * @since 14.03.30
     */
    public void setSource( String s )
    {
        glShaderSourceARB( shaderID, s );
        glCompileShaderARB( shaderID );

        compiled = ( glGetObjectParameteriARB( shaderID, GL_OBJECT_COMPILE_STATUS_ARB ) == GL_TRUE );
    }

    //
    // Getters
    //

    /**
     * @return The unique ID for this shader.
     */
    public int getShaderID()
    {
        return shaderID;
    }

    /**
     * @return The type of shader this represents.
     */
    public int getShaderType()
    {
        return shaderType;
    }

    /**
     * @return If the shader successfully compiled or not.
     */
    public boolean getCompileStatus()
    {
        return compiled;
    }

    /**
     * @return The error log for the shader compile status.
     */
    public String getErrorLog()
    {
        return glGetInfoLogARB( shaderID, glGetObjectParameteriARB( shaderID, GL_OBJECT_INFO_LOG_LENGTH_ARB ) );
    }

}
