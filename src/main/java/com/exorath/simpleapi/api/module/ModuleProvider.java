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

package com.exorath.simpleapi.api.module;


import org.bukkit.plugin.java.JavaPlugin;

/**
 * The SimpleAPI is capable of handling multiple Modules. They are registered with a JavaPlugin that implements ModuleProvider (SimpleAPI automatically registers them on PluginLoadEvent).
 * Created by Toon Sevrin on 5/15/2016.
 */
public abstract class ModuleProvider extends JavaPlugin {
    /**
     * Creates and returns a new {@link Module} object. This method will only be called once on this provider during the server runtime.
     *
     * @return the newly created game
     */
    public abstract Module create();

    /**
     * Gets the id of this provider, typically the plugin's name (Avoid spaces).
     *
     * @return the id of this provider
     */
    public abstract String getID();
}
