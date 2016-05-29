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

package com.exorath.tdm.hub;

import com.exorath.simpleapi.api.hub.Hub;
import de.inventivegames.hologram.HologramAPI;

/**
 * The hub is a server which primarily allows the players to connect to game servers.
 * It may also used for things like kit unlocking and selection, displaying leaderboards, stat upgrading, how-to-play guiding...
 *
 * GameServer discovery is done automatically.
 * 
 * Created by Toon Sevrin on 5/25/2016.
 */
public class TDMHub extends Hub {
    public TDMHub(){
    }
}