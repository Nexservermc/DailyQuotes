package com.kodari.dailyquotes.util;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * 通过反射调用 PlaceholderAPI，避免在未安装该插件时产生硬依赖。
 */
public final class PlaceholderUtil {

    private PlaceholderUtil() {
    }

    public static String apply(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        Plugin placeholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderApi == null || !placeholderApi.isEnabled()) {
            return text;
        }

        try {
            Class<?> placeholderApiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method setPlaceholders = placeholderApiClass.getMethod("setPlaceholders", Player.class, String.class);
            return (String) setPlaceholders.invoke(null, player, text);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return text;
        }
    }
}
