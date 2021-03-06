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

package com.exorath.simpleapi.api.serverlist;

import com.exorath.simpleapi.api.player.SerializedPlayer;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.Collection;

/**
 * Created by Toon Sevrin on 6/4/2016.
 */
public interface Server {
    /**
     * Gets the bungeecord id of this game server (The server name used to connect players to this game server)
     *
     * @return the bungeecord id of this game server
     */
    String getBungeeName();

    /**
     * True if players should be able to join this server (Can be as spectator or as player)
     *
     * @return true if players should be able to join this server
     */
    boolean isJoinable();

    /**
     * Gets the maximum amount of players allowed on this hub server
     *
     * @return the maximum amount of players allowed on this hub server
     */
     default int getMaxPlayerAmount(){
         return Bukkit.getMaxPlayers();
     }

    /**
     * Gets the amount of players that are on this hub server
     *
     * @return the amount of players that are on this hub server
     */
    int getPlayerAmount();

    /**
     * Gets a list of all players on this server
     *
     * @return a list of all players on this server
     */
    Collection<SerializedPlayer> getPlayers();

    /**
     * Serializes this object to an {@link JsonObject} which can be send over redis.
     * @return serialized {@link JsonObject} of this object
     */
    JsonObject serialize();

}
