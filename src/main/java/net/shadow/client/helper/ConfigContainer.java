/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class ConfigContainer {
    static final Gson gson = new Gson();
    final File path;
    final String key;
    @Getter
    JsonObject value;
    boolean loaded = false;

    public ConfigContainer(File f, String key) {
        this.path = f;
        this.key = key;
        this.value = new JsonObject();
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> this.save());
        reload();
    }

    public <T> T get(Class<T> type) {
        if (!loaded)
            return null;
        return gson.fromJson(getValue(), type);
    }

    public void set(Object data) {
        set(gson.toJsonTree(data).getAsJsonObject());
    }

    public void set(JsonObject obj) {
        value = obj;
    }

    void write(String data) {
        try {
            FileUtils.write(path, data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        if (!path.exists()) {
            return;
        }
        try {
            String p = FileUtils.readFileToString(path, StandardCharsets.UTF_8);
            set(JsonParser.parseString(p).getAsJsonObject());
            loaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        write(value.toString());
    }
}
