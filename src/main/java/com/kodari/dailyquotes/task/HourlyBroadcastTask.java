package com.kodari.dailyquotes.task;

import com.kodari.dailyquotes.config.ConfigManager;
import com.kodari.dailyquotes.util.ColorUtil;
import com.kodari.dailyquotes.util.PlaceholderUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class HourlyBroadcastTask implements Runnable {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ConfigManager config;
    private LocalDateTime lastBroadcastHour;

    public HourlyBroadcastTask(ConfigManager config) {
        this.config = config;
    }

    @Override
    public void run() {
        if (!config.isHourlyBroadcastEnabled()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.getMinute() != 0) {
            return;
        }

        LocalDateTime currentHour = now.withSecond(0).withNano(0);
        if (currentHour.equals(lastBroadcastHour)) {
            return;
        }
        lastBroadcastHour = currentHour;

        String message = replaceTimeTokens(config.getHourlyBroadcastMessage(), now);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ColorUtil.colorize(PlaceholderUtil.apply(player, message)));
        }
    }

    private String replaceTimeTokens(String message, LocalDateTime now) {
        String hour = String.valueOf(now.getHour());
        String minute = String.format("%02d", now.getMinute());
        String time = now.format(TIME_FORMATTER);
        return message
                .replace("{hour}", hour).replace("%hour%", hour)
                .replace("{minute}", minute).replace("%minute%", minute)
                .replace("{time}", time).replace("%time%", time);
    }
}
