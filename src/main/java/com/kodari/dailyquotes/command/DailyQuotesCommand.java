package com.kodari.dailyquotes.command;

import com.kodari.dailyquotes.DailyQuotesPlugin;
import com.kodari.dailyquotes.util.ColorUtil;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class DailyQuotesCommand implements CommandExecutor, TabCompleter {

    private final DailyQuotesPlugin plugin;

    public DailyQuotesCommand(DailyQuotesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("dailyquotes.admin")) {
            sender.sendMessage(ColorUtil.colorize("<red>你没有权限执行这个指令。</red>"));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ColorUtil.colorize("<green>DailyQuotes 配置已重载。</green>"));
            return true;
        }
        sender.sendMessage(ColorUtil.colorize("<yellow>用法：/dailyquotes reload</yellow>"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase())) {
            return List.of("reload");
        }
        return Collections.emptyList();
    }
}
