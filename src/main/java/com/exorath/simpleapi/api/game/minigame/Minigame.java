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

package com.exorath.simpleapi.api.game.minigame;

import com.exorath.simpleapi.api.game.Game;
import com.exorath.simpleapi.api.player.GamePlayer;

import java.util.Collection;

/**
 * Created by Toon Sevrin on 5/15/2016.
 */
public abstract class Minigame extends Game {

    public Minigame(String gameName){
        super(gameName);
    }

    @Override
    public Collection<GamePlayer> getPlayers() {
        return null;
    }
}
