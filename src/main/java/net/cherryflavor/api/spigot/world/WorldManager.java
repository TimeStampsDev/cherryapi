package net.cherryflavor.api.spigot.world;

import net.cherryflavor.api.configuration.CherryConfig;
import net.cherryflavor.api.configuration.Configuration;
import net.cherryflavor.api.exceptions.WorldManageException;
import net.cherryflavor.api.spigot.ServerAPI;
import net.cherryflavor.api.spigot.world.events.WorldFlagAddEvent;
import net.cherryflavor.api.spigot.world.events.WorldFlagRemoveEvent;
import net.cherryflavor.api.spigot.world.generation.WorldType;
import net.cherryflavor.api.spigot.world.generation.chunkgeneration.FlatWorldChunkGenerator;
import net.cherryflavor.api.spigot.world.generation.chunkgeneration.VoidWorldChunkGeneration;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2/20/2021
 * Time 12:32 AM
 */
public class WorldManager {

    private String debugPrefix = "[WorldManager]";

    public static String accessPermission = "cherryworlds.";

    private ServerAPI serverAPI;
    private List<World> worlds;
    private List<CherryWorld> cherryWorlds;
    private Configuration config;

    public Map<CherryWorld, List<WorldFlag>> flagMap = new HashMap<>();

    public static int basicChunkLength = 16;

    //==================================================================================================================
    // CONSTRUCTORS
    //==================================================================================================================

    /**
     * @param api
     */
    public WorldManager(ServerAPI api) {
        serverAPI = api;
        this.worlds = serverAPI.getServer().getWorlds();
        this.cherryWorlds = new ArrayList<>();
        config = new CherryConfig(new File("plugins/CherryAPI/worldmanage.yml")).getConfig();
    }

    //==================================================================================================================
    // GETTERS
    //==================================================================================================================


    /**
     *
     * @return
     */
    public String getDebugPrefix() {
        return debugPrefix;
    }

    /**
     * Gets list of worlds
     * @return
     */
    public List<World> getWorlds() {
        return worlds;
    }

    /**
     * Gets list of cherry worlds
     * @return
     */
    public List<CherryWorld> getCherryWorlds() {
        return cherryWorlds;
    }

    /**
     * Gets API
     * @return
     */
    public ServerAPI getAPI() { return serverAPI; }

    /**
     * Gets Config of worldmanage.yml
     * @return
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Returns default player max in config
     * @return
     */
    public int getMaxPlayersDefaultPerWorld() {
        return getConfig().getInt("default-max-players-allowed-per-world");
    }

    //==================================================================================================================
    // SETTERS
    //==================================================================================================================

    //==================================================================================================================
    // METHODS
    //==================================================================================================================

    public void saveFlagLists() {
        for (CherryWorld world : getCherryWorlds()) {
            CherryConfig wC = world.getConfig();
            wC.getConfig().set("flags", WorldFlag.convertToConfigList(flagMap.get(world)));
            wC.saveFile();;
        }
    }

    /**
     * Generates a new World!
     */
    public void generateWorld(WorldType type, String worldName, BlockPopulator... blockPopulators) {
        if (getAPI().getServer().getWorld(worldName) != null) {
            throw new WorldManageException(worldName + " already exists, try another name");
        } else {
            if (type == WorldType.FLAT) {

                FlatWorldChunkGenerator flatWorldChunkGenerator = new FlatWorldChunkGenerator(getAPI());
    
                for (BlockPopulator blockPopulator : blockPopulators) {
                    flatWorldChunkGenerator.addBlockPopulator(blockPopulator);
                }
    
                WorldCreator newWorld = new WorldCreator(worldName);
    
                newWorld.generator(flatWorldChunkGenerator);
    
                newWorld.createWorld();
    
            } else if (type == WorldType.NORMAL) {
    
                WorldCreator newWorld = new WorldCreator(worldName);
                newWorld.environment(World.Environment.NORMAL);
    
                newWorld.createWorld();
    
            } else if (type == WorldType.END) {
    
                WorldCreator newWorld = new WorldCreator(worldName);
                newWorld.environment(World.Environment.THE_END);
    
                newWorld.createWorld();
    
            } else if (type == WorldType.NETHER) {
    
                    WorldCreator newWorld = new WorldCreator(worldName);
                    newWorld.environment(World.Environment.NETHER);
    
                    newWorld.createWorld();
    
            } else if (type == WorldType.VOID) {
                
                VoidWorldChunkGeneration voidWorldChunkGeneration = new VoidWorldChunkGeneration(getAPI());
    
                for (BlockPopulator blockPopulator : blockPopulators) {
                    voidWorldChunkGeneration.addBlockPopulator(blockPopulator);
                }
    
                WorldCreator newWorld = new WorldCreator(worldName);
    
                newWorld.generator(voidWorldChunkGeneration);
    
                newWorld.createWorld();
    
            } else {
                throw new WorldManageException("Invalid WorldType provided");
            }
        }

        getAPI().debug(getDebugPrefix() + " " + worldName + " has been successfully created");

        CherryWorld world = new CherryWorld(Bukkit.getWorld(worldName.toLowerCase()));
        world.load();
        getCherryWorlds().add(world);

        worlds.add(world.getWorld());
    }

    public void loadWorlds() {
        File worldsFolder = new File("plugins/CherryAPI/worlds/");
        for (File worldYML : worldsFolder.listFiles()) {

            String worldName = worldYML.getName().replace(".yml","");

            if (Bukkit.getWorld(worldName) == null) {

                WorldCreator wc = new WorldCreator(worldName);
                wc.createWorld();

                CherryWorld cherryWorld = new CherryWorld(worldName);
                cherryWorld.load();
                worlds.add(cherryWorld.getWorld());

                ServerAPI.getAPI().debug(getDebugPrefix() + " " + cherryWorld.getWorldName() + " has been loaded");
            } else {
                CherryWorld cherryWorld = new CherryWorld(worldName);
                cherryWorld.load();
                worlds.add(cherryWorld.getWorld());
                ServerAPI.getAPI().debug(getDebugPrefix() + " " + cherryWorld.getWorldName() + " has been already loaded");
            }
        }
    }

    /**
     * Loads preexisting world
     * @param worldFolderName
     */
    public void loadWorld(String worldFolderName) {
        if (new File(worldFolderName).exists()) {
            if (getAPI().getServer().getWorld(worldFolderName) == null) {
                new WorldCreator(worldFolderName).createWorld();
                getAPI().debug(getDebugPrefix() + " " + worldFolderName + " has been successfully loaded");
            }
        } else {
            throw new NullPointerException("World " + worldFolderName + " does not exists, check names");
        }
    }

    /**
     * Registers the three vanilla worlds
     */
    public void registerVanillaWorlds() {
        for (World vanilla : Bukkit.getServer().getWorlds()) {
            if (vanilla.getName().equals("world")
                    || vanilla.getName().equals("world_the_end")
                    || vanilla.getName().equals("world_nether")) {
                CherryWorld world = new CherryWorld(vanilla.getName());
                world.load();
            }
        }
    }

    /**
     * Unloads worlds
     * @param world
     */
    public void unloadWorld(String world) {
        if (getAPI().getServer().getWorld(world) == null) {
            throw new WorldManageException(world + " does not exist, or has already been unloaded");
        } else {
            getAPI().getServer().unloadWorld(getAPI().getServer().getWorld(world), false);

            getAPI().debug(getDebugPrefix() + " " + world + " has been successfully unloaded");
        }
    }

    /**
     * Save all worlds
     */
    public void saveWorlds() {
        for (CherryWorld world : cherryWorlds) {
            for (Chunk c : world.getWorld().getLoadedChunks()) {
                c.unload();
            }
            world.getWorld().save();
            ServerAPI.getAPI().debug(getDebugPrefix() + " " + world.getWorldName() + " has been saved");
        }
    }
    
    public void deleteWorld(String world) {
        if (getAPI().getServer().getWorld(world) == null) {
            throw new WorldManageException(world + " does not exist, or has already been unloaded");
        } else {
            World w = getAPI().getServer().getWorld(world);
            World def = getAPI().getServer().getWorlds().get(0); // default world 'world' or whatever is input for server.properties

            CherryWorld cw = new CherryWorld(w);

            for (Player p : w.getPlayers()) {
                p.teleport(def.getSpawnLocation());
            }

            unloadWorld(world);

            File f = new File(world);
            CherryConfig.deleteFolder(f);

            cw.deleteConfigFile();
        }
    }

    /**
     * adds flag
     */
    public void addFlag(CherryWorld world, WorldFlag flag) {
        List<WorldFlag> flags = WorldFlag.parseStringList(world.getConfig().getConfig().getStringList("flags"));
        List<WorldFlag> mapFlags = flagMap.get(world);
        if (!flags.contains(flag)) {
            flags.add(flag);
            mapFlags.add(flag);
            flagMap.put(world, mapFlags);
            world.getConfig().getConfig().set("flags", WorldFlag.convertToConfigList(mapFlags));
            world.getConfig().saveFile();
            WorldFlagAddEvent event = new WorldFlagAddEvent(world,flag);
            Bukkit.getPluginManager().callEvent(event);
            getAPI().debug(getDebugPrefix() + " " + flag.getConfigTag() + " has been added to " + world.getWorldName());
        } else {
            throw new WorldManageException("Flag already added");
        }
    }

    /**
     * removes flag
     */
    public void removeFlag(CherryWorld world, WorldFlag flag) {
        List<WorldFlag> flags = WorldFlag.parseStringList(world.getConfig().getConfig().getStringList("flags"));
        List<WorldFlag> mapFlags = flagMap.get(world);
        if (flags.contains(flag)) {
            flags.remove(flag);
            mapFlags.remove(flag);
            flagMap.put(world, mapFlags);
            world.getConfig().getConfig().set("flags", WorldFlag.convertToConfigList(mapFlags));
            world.getConfig().saveFile();
            WorldFlagRemoveEvent event = new WorldFlagRemoveEvent(world, flag);
            Bukkit.getPluginManager().callEvent(event);
            getAPI().debug(getDebugPrefix() + " " + flag.getConfigTag() + " has been removed to " + world.getWorldName());
        } else {
            throw new WorldManageException("Flag already removed");
        }
    }

    /**
     * Adds world to world list
     * @param world
     */
    public void addWorld(World world) {
        worlds.add(world);
    }

    /**
     * Removes world to world list
     * @param world
     */
    public void removeWorld(World world) {
        worlds.remove(world);
    }
}
