package com.kodari.dailyquotes.config;

import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigManager {

    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isDailyMessageEnabled() {
        return plugin.getConfig().getBoolean("daily-message.enabled", true);
    }

    public String getDailyTitle() {
        return plugin.getConfig().getString("daily-message.title", "<gold>每日一言</gold>");
    }

    public String getDailySubtitle() {
        return plugin.getConfig().getString("daily-message.subtitle", "%quote%");
    }

    public int getDailyFadeIn() {
        return plugin.getConfig().getInt("daily-message.fade-in", 10);
    }

    public int getDailyStay() {
        return plugin.getConfig().getInt("daily-message.stay", 80);
    }

    public int getDailyFadeOut() {
        return plugin.getConfig().getInt("daily-message.fade-out", 20);
    }

    public List<String> getQuotes() {
        return plugin.getConfig().getStringList("daily-message.quotes");
    }

    public boolean isHourlyBroadcastEnabled() {
        return plugin.getConfig().getBoolean("hourly-broadcast.enabled", true);
    }

    public String getHourlyBroadcastMessage() {
        return plugin.getConfig().getString("hourly-broadcast.message",
                "<gold>叮咚！现在是 <yellow>{hour}</yellow> 点整！</gold>");
    }

    public boolean isJoinAnnouncementEnabled() {
        return plugin.getConfig().getBoolean("join-announcement.enabled", true);
    }

    public List<String> getJoinAnnouncementMessages() {
        return plugin.getConfig().getStringList("join-announcement.messages");
    }
}
