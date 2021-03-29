package net.cherryflavor.api.spigot.world.generation.chunkgeneration;

import net.cherryflavor.api.spigot.ServerAPI;
import net.cherryflavor.api.spigot.world.WorldManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 3/29/2021
 * Time 12:20 AM
 */

public class VoidWorldChunkGeneration {

    ServerAPI serverAPI;

    int currentHeight = 0;
    int chunkCount = 0;
    boolean hasStarted = false;

    List<BlockPopulator> blockPopulatorsList;

    //==================================================================================================================
    // CONSTRUCTORS
    //==================================================================================================================

    public VoidWorldChunkGeneration(ServerAPI serverAPI) {
        this.serverAPI = serverAPI;
        this.blockPopulatorsList = new ArrayList<>();
    }

    //==================================================================================================================
    // GETTERS
    //==================================================================================================================

    /**
     * Returns server API
     * @return
     */
    public ServerAPI getAPI() {
        return serverAPI;
    }

    /**
     * Overriden chunk gereration method
     * @param world
     * @param random
     * @param chunkX
     * @param chunkZ
     * @param biomeGrid
     * @return
     */
    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        if (!hasStarted) {
            serverAPI.debug("[WorldGen] Generating chunks...");
            hasStarted = true;
        }

        ChunkData chunk = createChunkData(world);
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);

        generator.setScale(0.005D); // Larger the scale, the deeper the terrain.

        double heightVariation = 15; // changing this randomly makes chunk-sized pits (HOLES)
        double lowestLevel = 50;


        for (int x = 0; x < WorldManager.basicChunkLength; x++) {
            for (int z = 0; z < WorldManager.basicChunkLength; z++) {
                chunk.setBlock(x, 4, z, Material.AIR);
                for (int i = 1; i < 4; i++){
                    chunk.setBlock(x, i, z, Material.AIR);
                }
                chunk.setBlock(x, 0, z, Material.AIR);
            }
        }

        return chunk;
    }

    /**
     * Returns block populator list (trees, etc)
     * @param world
     * @return
     */
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return blockPopulatorsList;
    }

    //==================================================================================================================
    // SETTERS
    //==================================================================================================================

    /**
     * Adds block populator (trees, etc)
     * @param blockPopulator
     */
    public void addBlockPopulator(BlockPopulator blockPopulator) {
        blockPopulatorsList.add(blockPopulator);
    }


    /**
     * Removes block populator (trees, etc)
     * @param blockPopulator
     */
    public void removeBlockPopulator(BlockPopulator blockPopulator) {
        blockPopulatorsList.remove(blockPopulator);
    }

}

