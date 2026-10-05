package com.kodari.dailyquotes.listener;

import com.kodari.dailyquotes.config.ConfigManager;
import com.kodari.dailyquotes.util.ColorUtil;
import com.kodari.dailyquotes.util.PlaceholderUtil;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class JoinListener implements Listener {

    private static final String[] AUTHOR_SEPARATORS = {"——", "—", "--"};

    private final ConfigManager config;

    public JoinListener(ConfigManager config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sendDailyMessage(player);
        sendJoinAnnouncement(player);
    }

    private void sendDailyMessage(Player player) {
        if (!config.isDailyMessageEnabled()) {
            return;
        }

        List<String> quotes = config.getQuotes();
        if (quotes.isEmpty()) {
            return;
        }

        String quote = quotes.get(ThreadLocalRandom.current().nextInt(quotes.size()));
        QuoteParts parts = splitQuote(quote);

        String title = replaceQuote(config.getDailyTitle(), quote, parts);
        String subtitle = parts.author().isEmpty()
                ? ""
                : replaceQuote(config.getDailySubtitle(), quote, parts);

        Times times = Times.times(
                ticksToDuration(config.getDailyFadeIn()),
                ticksToDuration(config.getDailyStay()),
                ticksToDuration(config.getDailyFadeOut()));

        player.showTitle(Title.title(
                ColorUtil.colorize(PlaceholderUtil.apply(player, title)),
                ColorUtil.colorize(PlaceholderUtil.apply(player, subtitle)),
                times));
    }

    /**
     * 将 "句子——作者" 形式的名言拆分为句子与作者两部分；未找到分隔符时作者为空。
     */
    private QuoteParts splitQuote(String quote) {
        for (String separator : AUTHOR_SEPARATORS) {
            int idx = quote.indexOf(separator);
            if (idx > 0) {
                String sentence = quote.substring(0, idx).trim();
                String author = quote.substring(idx + separator.length()).trim();
                return new QuoteParts(sentence, author);
            }
        }
        return new QuoteParts(quote.trim(), "");
    }

    private void sendJoinAnnouncement(Player player) {
        if (!config.isJoinAnnouncementEnabled()) {
            return;
        }
        for (String message : config.getJoinAnnouncementMessages()) {
            player.sendMessage(ColorUtil.colorize(PlaceholderUtil.apply(player, message)));
        }
    }

    private String replaceQuote(String message, String quote, QuoteParts parts) {
        return message
                .replace("%quote%", quote).replace("{quote}", quote)
                .replace("%quote_sentence%", parts.sentence()).replace("{quote_sentence}", parts.sentence())
                .replace("%quote_author%", parts.author()).replace("{quote_author}", parts.author());
    }

    private Duration ticksToDuration(int ticks) {
        return Duration.ofMillis(Math.max(0, ticks) * 50L);
    }

    private record QuoteParts(String sentence, String author) {
    }
}
