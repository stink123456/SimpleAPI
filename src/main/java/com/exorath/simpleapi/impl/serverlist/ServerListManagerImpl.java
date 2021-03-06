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

package com.exorath.simpleapi.impl.serverlist;

import com.exorath.simpleapi.api.game.Game;
import com.exorath.simpleapi.api.game.minigame.Minigame;
import com.exorath.simpleapi.api.hub.Hub;
import com.exorath.simpleapi.api.serverlist.GameServer;
import com.exorath.simpleapi.api.hub.serverlist.ServerAddedEvent;
import com.exorath.simpleapi.api.hub.serverlist.ServerRemovedEvent;
import com.exorath.simpleapi.api.redis.RedisSubscriber;
import com.exorath.simpleapi.api.serverlist.HubServer;
import com.exorath.simpleapi.api.serverlist.Server;
import com.exorath.simpleapi.api.serverlist.ServerListManager;
import com.exorath.simpleapi.impl.SimpleAPIImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This manager listens for Game Discovery Messages. See https://github.com/stink123456/SimpleAPI/wiki/Game-discovery for more info.
 * Created by Toon Sevrin on 5/17/2016.
 */
public class ServerListManagerImpl extends RedisSubscriber implements ServerListManager {
    private HashMap<String, Server> serversById = new HashMap<>();
    private HashMap<String, Integer> serverExpiryById = new HashMap<>();


    public ServerListManagerImpl() {
        super(Game.getRedisGameDiscoveryChannel());
    }

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals(Game.getRedisGameDiscoveryChannel()))
            handleGameDiscoveryMessage(new JsonParser().parse(message).getAsJsonObject());
        else if (channel.equals(Hub.getRedisHubDiscoveryChannel()))
            handleHubDiscoveryMessage(new JsonParser().parse(message).getAsJsonObject());
    }

    /**
     * This is called when a Game-Discovery-Message is received through redis. It will add the game and update the expiry scheduler.
     *
     * @param message the Game-Discovery-Message
     */
    public void handleGameDiscoveryMessage(JsonObject message) {
        Validate.isTrue(validateGameDiscoveryMessage(message), "SimpleAPI received a non-valid Game-Discovery-Message over the channel: " + Minigame.getRedisGameDiscoveryChannel());

        GameServer gameServer = GameServerImpl.deserialize(message.get("gameserver").getAsJsonObject());

        ServerAddedEvent event = new ServerAddedEvent(gameServer, message.get("expire").getAsInt());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        serversById.put(gameServer.getBungeeName(), gameServer);
        handleExpiry(gameServer.getBungeeName(), event.getExpireAfter());
    }

    /**
     * This is called when a Hub-Discovery-Message is received through redis. It will add the hub and update the expiry scheduler.
     *
     * @param message the Hub-Discovery-Message
     */
    public void handleHubDiscoveryMessage(JsonObject message) {
        Validate.isTrue(validateGameDiscoveryMessage(message), "SimpleAPI received a non-valid Hub-Discovery-Message over the channel: " + Minigame.getRedisGameDiscoveryChannel());

        GameServer gameServer = GameServerImpl.deserialize(message.get("gameserver").getAsJsonObject());

        ServerAddedEvent event = new ServerAddedEvent(gameServer, message.get("expire").getAsInt());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        serversById.put(gameServer.getBungeeName(), gameServer);
        handleExpiry(gameServer.getBungeeName(), event.getExpireAfter());
    }

    /**
     * Updates the expiry scheduler with the new expiration time.
     *
     * @param bungeeId         GameServerId to update expiration time of
     * @param expireAfterTicks after this time expires, the server will be removed if it has not been re-registered through the redis message
     */
    public void handleExpiry(String bungeeId, int expireAfterTicks) {
        if (serverExpiryById.containsKey(bungeeId) && Bukkit.getScheduler().isQueued(serverExpiryById.get(bungeeId)))
            Bukkit.getScheduler().cancelTask(serverExpiryById.get(bungeeId));
        serverExpiryById.put(bungeeId,
                Bukkit.getScheduler().scheduleSyncDelayedTask(SimpleAPIImpl.getInstance(), getOnExpire(bungeeId), expireAfterTicks));
    }

    /**
     * Gets a {@link Runnable} that calls a GameServerRemoveEvent and removes the server with bungeeId from the list.
     *
     * @param bungeeId id of {@link GameServer} that should be remove
     * @return a {@link Runnable} that calls a GameServerRemoveEvent and removes the server with bungeeId from the list
     */
    public Runnable getOnExpire(String bungeeId) {
        return () -> {
            if (serversById.containsKey(bungeeId)) {
                ServerRemovedEvent removedEvent = new ServerRemovedEvent(serversById.get(bungeeId));
                Bukkit.getPluginManager().callEvent(removedEvent);
                if (removedEvent.isCancelled())
                    return;
            }
            serversById.remove(bungeeId);
            serverExpiryById.remove(bungeeId);
        };
    }

    /**
     * Validates whether or not the provided Game-Discovery-Message is valid (Contains required keys).
     *
     * @param message Game-Discovery-Message to validate
     * @return true if the message is valid
     */
    private static boolean validateGameDiscoveryMessage(JsonObject message) {
        return message.has("expire") && message.has("gameserver") && GameServerImpl.canDeserialize(message.get("gameserver").getAsJsonObject());
    }

    /**
     * Validates whether or not the provided Hub-Discovery-Message is valid (Contains required keys).
     *
     * @param message Game-Discovery-Message to validate
     * @return true if the message is valid
     */
    private static boolean validateHubDiscoveryMessage(JsonObject message) {
        return message.has("expire") && message.has("hubserver") && GameServerImpl.canDeserialize(message.get("hubserver").getAsJsonObject());
    }

    @Override
    public Collection<GameServer> getGameServers() {
        return getServers(GameServer.class);
    }

    @Override
    public Collection<HubServer> getHubServers() {
       return getServers(HubServer.class);
    }

    private <T extends Server> Collection<T> getServers(Class<T> type) {
        Set<T> servers = new HashSet<>();
        serversById.values().stream()
                .filter(s -> type.isAssignableFrom(s.getClass()))
                .forEach(s -> servers.add((T) s));
        return servers;
    }
}
