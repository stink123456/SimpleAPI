/*
 *    Copyright 2016 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.simpleapi.api.hub;

import com.exorath.simpleapi.api.GameHubProvider;
import com.exorath.simpleapi.api.PrimaryModule;
import com.exorath.simpleapi.api.SimpleAPI;
import com.exorath.simpleapi.api.events.EventManager;
import com.exorath.simpleapi.api.hub.serverdisplay.ServerDisplayManager;
import com.exorath.simpleapi.api.hub.serverlist.GameServerFilter;
import com.exorath.simpleapi.api.player.GamePlayer;
import com.exorath.simpleapi.impl.hub.serverdisplay.ServerDisplayManagerImpl;
import com.exorath.simpleapi.impl.serverlist.ServerListManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;


/**
 * The API can register up to one Game. It must be registered through a {@link GameHubProvider}.
 * Created by Toon Sevrin on 5/15/2016.
 */
public abstract class Hub extends PrimaryModule {
    public static String HUB_SERVERS_PREFIX = "hubservers.";
    private World world;
    private YamlConfiguration worldConfig;

    public Hub() {
        this("world");
    }

    public Hub(String worldName) {
        loadWorld(worldName);
        SimpleAPI.getInstance().getManager(EventManager.class).protectWorld(world);

        SimpleAPI.getInstance().addManager(new ServerListManagerImpl());
        SimpleAPI.getInstance().addManager(new ServerDisplayManagerImpl(this));
    }

    private void loadWorld(String worldName) {
        world = Bukkit.createWorld(WorldCreator.name(worldName));
        worldConfig = YamlConfiguration.loadConfiguration(new File(world.getWorldFolder(), "exorath.yml"));
    }

    /**
     * Adds a filter that may be used by the already integrated game selector signs and NPCs (You can also add your own game selectors through the {@link com.exorath.simpleapi.api.hub.serverdisplay.ServerDisplayManager}.
     * Do not use isJoinable() or isPlayable() here.
     *
     * @param id     filter id to reference to this filter (fe. from the exorath.yml world file)
     * @param filter filter to add
     */

    public void addFilter(String id, GameServerFilter filter) {
        SimpleAPI.getInstance().getManager(ServerDisplayManager.class).addFilter(id, filter);
    }

    /**
     * True if this game can be joined (Regardless whether or not you will be playing after joining).
     * @return true if this game can be joined
     */
    public boolean isJoinable(){
        return Bukkit.getOnlinePlayers().size() < getMaxPlayers();
    }

    /**
     * Gets the maximum amount of players allowed to play on this server. By default this calls {@link Bukkit#getMaxPlayers()}.
     * @return allowed to play on this server
     */
    public int getMaxPlayers(){
        return Bukkit.getMaxPlayers();
    }

    /**
     * Gets the players playing on this server (Not spectating).
     * @return the players playing on this server (Not spectating)
     */
    public abstract Collection<GamePlayer> getPlayers();
    public World getHubWorld() {
        return world;
    }

    public YamlConfiguration getWorldConfiguration() {
        return worldConfig;
    }

    /**
     * Gets the redis channel where hubs of this GameHubProvider's ID publish their status to
     * @return the redis channel where hubs of this GameHubProvider's ID publish their status to, or null if no GameHubProvider registered.
     */
    public static String getRedisHubDiscoveryChannel(){
        return SimpleAPI.getInstance().getGameHubProvider() == null ? null : HUB_SERVERS_PREFIX + SimpleAPI.getInstance().getGameHubProvider().getID();
    }
}
