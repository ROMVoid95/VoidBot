/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2020
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package net.romvoid.discord.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 */
public class Configuration {
    
    /** The file. */
    private File file;
    
    /** The json. */
    private JsonObject json;
    
    /** The json parser. */
    public JsonParser jsonParser;

    /**
     * Instantiates a new configuration.
     *
     * @param file the file
     */
    public Configuration(final File file) {

        this.file = file;
        String cont = null;
        jsonParser = new JsonParser();

        try {
            if (file.exists()) {
                cont = new BufferedReader(new FileReader(file)).lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cont == null || cont.equals("")) {
            cont = "{}";
        }
        json = jsonParser.parse(cont).getAsJsonObject();
    }

    /**
     * Sets the.
     *
     * @param key the key
     * @param val the val
     * @return the configuration
     * @description Sets tha value of a key in config
     */
    public Configuration set(final String key, final String val) {
        if (json.has(key)) {
            json.remove(key);
        }
        if (val != null) {
            json.addProperty(key, val);
        }
        return this.save();
    }

    /**
     * Sets the.
     *
     * @param key the key
     * @param val the val
     * @return the configuration
     * @description Sets tha value of a key in config
     */
    public Configuration set(final String key, final int val) {
        if (json.has(key)) {
            json.remove(key);
        }
        this.json.addProperty(key, val);
        return this.save();
    }

    /**
     * Unset.
     *
     * @param key the key
     * @return the configuration
     * @description Removes key from config
     */
    public Configuration unset(final String key) {
        if (json.has(key))
            json.remove(key);

        return this.save();
    }

    /**
     * Save.
     *
     * @return the configuration
     * @description Saves the config
     */
    private Configuration save() {
        try {
            if (json.entrySet().size() == 0) {
                if (file.exists()) {
                    file.delete();
                }
            } else {
                if (!file.exists()) {
                    file.createNewFile();
                }

                BufferedWriter br = new BufferedWriter(new FileWriter(file));
                br.write(json.toString());
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Gets the string.
     *
     * @param key the key
     * @return Value of key in config as string
     */
    public String getString(final String key) {
        try {
            return json.get(key).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Gets the int.
     *
     * @param key the key
     * @return Value of key in config as integer
     */
    public int getInt(final String key) {
        if (json.has(key)) {
            return json.get(key).getAsInt();
        }
        return 0;
    }

    /**
     * Checks for.
     *
     * @param key the key
     * @return If key exists
     */
    public boolean has(final String key) {
        try {
            return json.has(key);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    /**
     * Key set.
     *
     * @return the list
     */
    public List<String> keySet() {
        List<String> keys = new ArrayList<>();
        Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    /**
     * Values.
     *
     * @return the list
     */
    public List<String> values() {
        List<String> values = new ArrayList<>();
        Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            values.add(entry.getValue().getAsString());
        }
        return values;
    }


}