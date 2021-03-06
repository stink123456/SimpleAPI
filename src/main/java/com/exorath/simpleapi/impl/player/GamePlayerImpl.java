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

package com.exorath.simpleapi.impl.player;

import com.exorath.simpleapi.api.SimpleAPI;
import com.exorath.simpleapi.api.database.DBData;
import com.exorath.simpleapi.api.player.GamePlayer;
import com.exorath.simpleapi.api.properties.Properties;
import com.exorath.simpleapi.impl.SimpleAPIImpl;
import com.exorath.simpleapi.impl.database.DBDataImpl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Toon Sevrin on 5/15/2016.
 */
public class GamePlayerImpl implements GamePlayer {
    private UUID uuid;
    private Properties properties = Properties.createNewProperties();

    private DBData publicDBData;
    private DBData gameDBData;

    public GamePlayerImpl(Player player) {
        this(player.getUniqueId());
    }

    public GamePlayerImpl(UUID id) {
        uuid = id;

        publicDBData = new DBDataImpl(SimpleAPIImpl.getInstance(), "players", id.toString(), true);
        if (SimpleAPI.getInstance().getGameHubProvider() != null)
            gameDBData = new DBDataImpl(SimpleAPI.getInstance().getGameHubProvider(), "players", id.toString(), true);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Player bukkit() {
        OfflinePlayer player = getOfflinePlayer();
        return player.isOnline() ? player.getPlayer() : null;
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public DBData getGameData() {//TODO: Create method content
        return null;
    }

    @Override
    public DBData getGlobalData() {//TODO: Create method content
        return null;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
