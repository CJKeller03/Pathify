/*
 * Copyright (C) 2020 caleb
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
 * @version 0.1
 */
public class WizardModel {
    private HashMap<String, Object> data;
    
    public WizardModel() {
        this.data = new HashMap<String, Object>();
    }
    
    public boolean hasObject(String key) {
        return this.data.containsKey(key);
    }
    
    public Object getObject(String key) {
        return this.data.get(key);
    }
    
    public void setObject(String key, Object data) {
        System.out.println("set");
        
        this.data.put(key, data);
    }
    
    public boolean deleteObject(String key) {
        return this.data.remove(key) == null;
    }
}