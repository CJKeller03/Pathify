/*
 * Copyright (C) 2020 Caleb Keller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.calebjkeller.pathify.wizard;

import java.util.HashMap;

/**
 * A simple class for allowing data collected in a WizardPage to be
 * accessible by subsequent pages.
 * 
 * @author Caleb Keller
 */
public class WizardModel {
    private HashMap<String, Object> data;
    
    /**
     * Create a new WizardModel.
     */
    public WizardModel() {
        this.data = new HashMap<String, Object>();
    }
    
    /**
     * Check if this model contains the specified object.
     * @param key The key to check for
     * @return Whether the model contains an object for that key
     */
    public boolean hasObject(String key) {
        return this.data.containsKey(key);
    }
    
    /**
     * Get the object associated with the provided key.
     * @param key The key of the object to retrieve
     * @return The object associated with the key
     */
    public Object getObject(String key) {
        return this.data.get(key);
    }
    
    /**
     * Create a new key - object mapping in the model.
     * @param key The key to associate with the object
     * @param data The object to store
     */
    public void setObject(String key, Object data) {
        this.data.put(key, data);
    }
    
    /**
     * Remove a key - object mapping from the model.
     * @param key The key to remove
     * @return If the key was in the model before attempting to remove it
     */
    public boolean deleteObject(String key) {
        return this.data.remove(key) == null;
    }
}
