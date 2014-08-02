varying vec3 normal;
varying vec3 vertex_to_light_vector;

void main()
{
    // Defining the material colors
    const vec4 AmbientColor = vec4( 0.1, 0.0, 0.0, 1.0 );
    const vec4 DiffuseColor = vec4( 1.0, 0.0, 0.0, 1.0 );

    // scale the input vector to length 1
    vec3 normalized_normal = normalize( normal );
    vec3 normalized_vertex_to_light_vector = normalize( vertex_to_light_vector );
    
    // calculate the diffuse term and clamping it to [0, 1)
    float DiffuseTerm = clamp( dot( normal, vertex_to_light_vector ), 0.0, 1.0 );
    
    // calculate the final color
    gl_FragColor = Ambientcolor + DiffuseColor * DiffuseTerm;
}