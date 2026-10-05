package com.kodari.dailyquotes;

import com.kodari.dailyquotes.command.DailyQuotesCommand;
import com.kodari.dailyquotes.config.ConfigManager;
import com.kodari.dailyquotes.listener.JoinListener;
import com.kodari.dailyquotes.task.HourlyBroadcastTask;
import com.kodari.dailyquotes.util.ColorUtil;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class DailyQuotesPlugin extends JavaPlugin {

    private BukkitTask hourlyBroadcastTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigManager configManager = new ConfigManager(this);

        getServer().getPluginManager().registerEvents(new JoinListener(configManager), this);
        hourlyBroadcastTask = getServer().getScheduler()
                .runTaskTimer(this, new HourlyBroadcastTask(configManager), 20L, 20L);

        PluginCommand command = getCommand("dailyquotes");
        if (command != null) {
            DailyQuotesCommand commandHandler = new DailyQuotesCommand(this);
            command.setExecutor(commandHandler);
            command.setTabCompleter(commandHandler);
        }

        printStartupInfo(configManager);
    }

    @Override
    public void onDisable() {
        if (hourlyBroadcastTask != null) {
            hourlyBroadcastTask.cancel();
        }
        getLogger().info(ColorUtil.consoleColorize("<gray>DailyQuotes 每日一言已卸载，期待下次再见！</gray>"));
    }

    private void printStartupInfo(ConfigManager config) {
        String version = getPluginMeta().getVersion();
        String author = String.join(", ", getPluginMeta().getAuthors());
        List<String> quotes = config.getQuotes();

        getLogger().info(ColorUtil.consoleColorize("<gradient:gold:yellow>=====================================================</gradient>"));
        getLogger().info(ColorUtil.consoleColorize("<gold><bold>✦ DailyQuotes 每日一言 已加载 ✦</bold></gold>"));
        getLogger().info(ColorUtil.consoleColorize("<white>版本: <aqua>" + version + "</aqua>   作者: <aqua>" + author + "</aqua>"));
        getLogger().info(ColorUtil.consoleColorize("<white>名言库: <aqua>" + quotes.size() + "</aqua> 条"));
        getLogger().info(ColorUtil.consoleColorize(
                "<white>每日一言: " + status(config.isDailyMessageEnabled())
                        + "   整点报时: " + status(config.isHourlyBroadcastEnabled())
                        + "   入服公告: " + status(config.isJoinAnnouncementEnabled())));
        getLogger().info(ColorUtil.consoleColorize(
                "<white>PlaceholderAPI: " + status(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)));

        if (!quotes.isEmpty()) {
            String random = quotes.get(ThreadLocalRandom.current().nextInt(quotes.size()));
            getLogger().info(ColorUtil.consoleColorize("<white>今日一言: <yellow>\"" + random + "\"</yellow>"));
        }

        getLogger().info(ColorUtil.consoleColorize("<gradient:gold:yellow>=====================================================</gradient>"));
        getLogger().info(ColorUtil.consoleColorize("<gray>输入 <aqua>/dailyquotes reload</aqua> 可重载配置。</gray>"));
    }

    private String status(boolean enabled) {
        return enabled ? "<green>✔ 已启用</green>" : "<red>✘ 已禁用</red>";
    }
}
