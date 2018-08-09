## Geographic tiled map base for libgdx

The project is mainly based on a bunch of interfaces, covering tile download, memory and storage persistence, rendering and navigating.
It contains a robust implementation of some aspects and some utilities, providing a ready to use map base.

[MainTest](https://github.com/DranikProgrammer/gdx-map/blob/master/core/src/main/java/com/dranikpg/gdxmap/MainTest.java)  is an ApplicationAdapter which showcases the basic functionality. The rest of the project is **not** documented.
 
Overview:

![kek](http://www.dranikpg.com/img/gdxmap/big.png)
 
Dynamic map load example: 

![kek](http://www.dranikpg.com/img/gdxmap/dynamic_load.png)


### General info
The macro level building blocks are *MapTile*s. Each MapTile has a x/y position as well as a zoomlevel it belongs to.
For faster access and searches those 3 components  are compressed via bit shifts into a long value, called the tile code.
The *TileProvider* is an interface for supplying *MapHolder*s with tiles. In general there should be only one TileProvider per application, 
as they might use a lot of resources(depends on the strategies, more on soon). MapHolders are utility objects which hold a position,
a zoom level and update automatically on tile updates. *MapView* is a utility for navigating the map, smooth zooms and rendering. 
The *TileProviderInfo* interface tells the TileProvider where to download the tiles, what boundaries and map sizes to expect.
*OSMProviderInfo* is a ready to use implementation and is based on the OpenSteetMap project.

**GdxMapCodes** (responsible for generating tile codes) needs to be initialized by calling the static generate method. It generates bit masks based
on the `MAX_COORD`(1e5) and  `MAX_LEVEL`(1e2) values. Two times `MAX_COORD` plus  `MAX_LEVEL` should **not exceed 1e18**.
It contains some constants as well.

### Some defaults

[CachedEncodedTileProvider](https://github.com/DranikProgrammer/gdx-map/blob/master/core/src/main/java/com/dranikpg/gdxmap/impl/CachedEncodedTileProvider.java) 
is a robust *TileProvider* implementation. But the robustness doesn't come out of thin air: it needs to be fed with stategies.
An *InternetStrategy*, an *PersistenceStrategy* and an *MemoryPersistenceStrategy* are required to be present. 
The *InternetStrategy* is responsible for downloading tiles.
The *PersistenceStrategy* is responsible for persisting downloaded tiles on some kind of storage and *ideally* not blowing up the users storage,
and *MemoryPersistenceStrategy* should *ideally* take care of not blowing up the users RAM.

The *LimitedBoundsEjectNetStrategy* is a Network strategy which has a controlled amount of worker threads based around a blocking queue. It tires to **prevent tiles which are not visible any more from loading**. This way after a long pan&zoom journey the strategy ejects tiles left behind to focus instead on fetching the currently visible area.

PS: Keeping the amount of worker thread limited is generally a good idea. After I configured a similar extension for google's tile server I almost instantly got blocked from google maps for 12 hours. I'm sorry I hadn't read the GTC :(

The *InstantWriteBytePersistence* is **just a utility** for a painless start. As you might have guessed it fails on filtering the tiles to persist. It saves every downloaded tile. This is actually a good way of **blowing up the users storage** with a few thousand files. So either try clearing it regularly or ejecting obsolete tiles. 

We can't precalculate the byte array size because most providers use the JPEG format, with sizes mostly ranging anywhere from 0.1 up to 50 kilobytes, so provided implementations allocate a new byte array for every implementation. But we also have some hidden guests that don't show up in the heap.
Each tile holds a texture, so keep in mind that **it has to be disposed**. One could inventing some clever gc based mechanism... but one of the libgdx founders told me that native resources should generally have a well defined life cycle. 
The *LimitedBoundsEjectMemoryPersistence* can be used to **dispose invisible tiles** when the amount of cached tiles reaches a certain size. The implementation is inefficient with small sizes.

Note that the *CachedEncodedTileProvider* tries to abstract multithreaded and context based execution via the *MapExecutionProvider* interface.

Some more info might be found here [dranikpg home](http://www.dranikpg.com/blog/gdxmap.html)



