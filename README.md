GVEngine - Generic Voxel Engine
====
  

Creating one's own voxel engine is hard work, wouldn't you much rather get the actual _game designing_ part of making a game?  
If this is the case, this project might just be the solution for you (provided you program in Java)!  
I don't really like the whole _game_ part of game development, I'd much rather just make an engine and let other people use it (because I know I never will!)  
  

Dependencies
====

GVEngine requires only two libraries from [LWJGL](http://www.lwjgl.org/)  
lwjgl.jar  
lwjgl_util.jar  
  

Examples
====

A more in-depth example can be found at [src/com/github/obsidianarch/](https://github.com/Obsidianarch/gvengine/tree/master/src/com/github/obsidianarch), however here are some simple examples:  

Setting voxel data  
````
Voxel.setVoxelSize( 2f ); // voxels will be twice the size they normally would be (must be greated than 0)
Voxel.createVoxelList( 10 ); // this must be called before adding voxels to the list!
Voxel.createVoxel( 0, new AirVoxelType() ); // every voxel with the id '0' will have the properties described by AirVoxelType
Voxel.createVoxel( 1, new DirtVoxelType() );

// instead of Voxel.createVoxel( int, VoxelType ), one can do this instead
// new Voxel( int, VoxelType );
// this will do the same thing (as this is all that Voxel.createVoxel( int, VoxelType ) does as well
// however the use of createVoxel is more readable and should be used more often.
````
  
Getting voxel data  
````
Voxel vox = getVoxel( i ); // gets the voxel with the given id
VoxelType type = getVoxelType( i ); // get the voxel type of the voxel with the given id (shortcut for getVoxel( i ).voxelType)
````  

Generating a Chunk  
````
ChunkManager manager = ChunkManager.createChunkManager( "nameGoesHere", implementsChunkProvider );
// implementsChunkProvider is placeholder for a class which, oddly enough, implements ChunkProvider

Chunk newChunk = manager.createChunk( x, y, z ); // tells the underlying ChunkProvider to create a new chunk at the given coordinates
Chunk oldChunk = manager.getChunk( x, y, z ); // gets the chunk at the given coordinates from the underlying ChunkProvider, if there is none, then it should return null

// Use this most often!
Chunk newOrOldChunk = manager.provideChunk( x, y, z ); // tries getChunk( x, y, z ) and if it returns null then it will return createChunk( x, y, z )
````  
  
Questions
====

What is a Voxel ID?  
A Voxel ID is the positive integer given to a voxel. This is used everywhere! A Chunk does not contain each individual voxel, rather it contains a simple integer which will be used to get the information from the list in the Voxel class.  
This brings up the question of "Why not use a new Voxel object for everything?" Well think about it for a second, it's for lower memory usage.  Which is smaller, an Integer (the object not 'int') or a Voxel object containing it's own set of data? Now this may not be a large difference, but when you have 4,096 different values in a single chunk, and then a game has 4,096 chunks loaded, that's 16,777,216 voxels!  
Some simple (calculator) math:  an int is 32 bits, and I'll estimate a Voxel to be slightly larger at about 128 bits.  
````
32  * 16777216 = 603,979,776   Bits = 75,497,472  Bytes = 73,728  KBytes = 72  MBytes  
128 * 16777216 = 2,147,483,648 Bits = 268,435,456 Bytes = 262,144 KBytes = 256 MBytes  
````
That's a rather large difference for a small number of chunks.

What is a _VoxelType_, and how do I use/make one?  
A VoxelType is a set of information attributed to a Voxel ID, to make one simply implement the interface "com.github.obsidianarch.gvengine.VoxelType".  
























