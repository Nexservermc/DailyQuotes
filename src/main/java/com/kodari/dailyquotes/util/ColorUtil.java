package com.kodari.dailyquotes.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * 颜色工具：把 Bukkit 传统颜色码（&a）、十六进制颜色（&#FFAA00 / &x&F&F&A&A&0&0）
 * 统一转换为 MiniMessage，再渲染为组件或控制台 ANSI 颜色。
 */
public final class ColorUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /** &x&R&R&G&G&B&B 形式（Bukkit 十六进制） */
    private static final Pattern HEX_X_PATTERN = Pattern.compile(
            "(?i)([&§])x(?:[&§])([0-9a-f])(?:[&§])([0-9a-f])(?:[&§])([0-9a-f])"
                    + "(?:[&§])([0-9a-f])(?:[&§])([0-9a-f])(?:[&§])([0-9a-f])");
    /** &#RRGGBB 形式 */
    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)([&§])#([0-9a-f]{6})");
    /** &a / §a 等传统颜色与格式码 */
    private static final Pattern LEGACY_PATTERN = Pattern.compile("(?i)([&§])([0-9a-fk-or])");

    private static final String ANSI_RESET = "\u001b[0m";
    private static final String ANSI_BOLD = "\u001b[1m";
    private static final String ANSI_ITALIC = "\u001b[3m";
    private static final String ANSI_UNDERLINED = "\u001b[4m";
    private static final String ANSI_STRIKETHROUGH = "\u001b[9m";

    private static final Map<String, String> NAMED_COLORS = new HashMap<>();

    static {
        NAMED_COLORS.put("black", "\u001b[30m");
        NAMED_COLORS.put("dark_blue", "\u001b[34m");
        NAMED_COLORS.put("dark_green", "\u001b[32m");
        NAMED_COLORS.put("dark_aqua", "\u001b[36m");
        NAMED_COLORS.put("dark_red", "\u001b[31m");
        NAMED_COLORS.put("dark_purple", "\u001b[35m");
        NAMED_COLORS.put("gold", "\u001b[33m");
        NAMED_COLORS.put("gray", "\u001b[37m");
        NAMED_COLORS.put("dark_gray", "\u001b[90m");
        NAMED_COLORS.put("blue", "\u001b[94m");
        NAMED_COLORS.put("green", "\u001b[92m");
        NAMED_COLORS.put("aqua", "\u001b[96m");
        NAMED_COLORS.put("red", "\u001b[91m");
        NAMED_COLORS.put("light_purple", "\u001b[95m");
        NAMED_COLORS.put("yellow", "\u001b[93m");
        NAMED_COLORS.put("white", "\u001b[97m");
    }

    private ColorUtil() {
    }

    /**
     * 把文本渲染为 Adventure 组件（用于 showTitle / sendMessage）。
     */
    public static Component colorize(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(toMiniMessage(text));
    }

    /**
     * 把文本转换为带 ANSI 颜色的字符串（用于控制台日志）。
     */
    public static String consoleColorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return parseTagsToAnsi(toMiniMessage(text)) + ANSI_RESET;
    }

    private static String toMiniMessage(String text) {
        String result = replaceHexX(text);
        result = replaceHex(result);

        Matcher matcher = LEGACY_PATTERN.matcher(result);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(legacyTag(matcher.group(2).charAt(0))));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String parseTagsToAnsi(String text) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        int length = text.length();

        while (i < length) {
            char c = text.charAt(i);
            if (c == '<') {
                int close = text.indexOf('>', i);
                if (close == -1) {
                    out.append(c);
                    i++;
                } else {
                    out.append(handleTag(text.substring(i + 1, close)));
                    i = close + 1;
                }
            } else {
                out.append(c);
                i++;
            }
        }
        return out.toString();
    }

    private static String handleTag(String tag) {
        if (tag.isEmpty()) {
            return "";
        }
        if (tag.startsWith("/")) {
            return ANSI_RESET;
        }

        switch (tag) {
            case "reset":
            case "r":
                return ANSI_RESET;
            case "bold":
            case "b":
                return ANSI_BOLD;
            case "italic":
            case "em":
            case "i":
                return ANSI_ITALIC;
            case "underlined":
            case "u":
                return ANSI_UNDERLINED;
            case "strikethrough":
            case "st":
                return ANSI_STRIKETHROUGH;
            case "obfuscated":
            case "obf":
                return "";
            default:
                break;
        }

        // <#RRGGBB>
        if (tag.startsWith("#") && tag.length() == 7) {
            return hexToAnsi(tag.substring(1));
        }

        // <gradient:起始色:结束色>：控制台只取起始色
        if (tag.startsWith("gradient:")) {
            String[] parts = tag.split(":");
            if (parts.length >= 2) {
                String start = parts[1];
                if (start.startsWith("#") && start.length() == 7) {
                    return hexToAnsi(start.substring(1));
                }
                String code = NAMED_COLORS.get(start.toLowerCase());
                if (code != null) {
                    return code;
                }
            }
            return "";
        }

        String code = NAMED_COLORS.get(tag.toLowerCase());
        return code != null ? code : "";
    }

    private static String hexToAnsi(String hex) {
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return "\u001b[38;2;" + r + ";" + g + ";" + b + "m";
        } catch (NumberFormatException ignored) {
            return "";
        }
    }

    private static String replaceHexX(String text) {
        Matcher matcher = HEX_X_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String hex = "<#" + matcher.group(2) + matcher.group(3) + matcher.group(4)
                    + matcher.group(5) + matcher.group(6) + matcher.group(7) + ">";
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(hex));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String replaceHex(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Matcher.quoteReplacement("<#" + matcher.group(2) + ">"));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String legacyTag(char code) {
        switch (Character.toLowerCase(code)) {
            case '0':
                return "<black>";
            case '1':
                return "<dark_blue>";
            case '2':
                return "<dark_green>";
            case '3':
                return "<dark_aqua>";
            case '4':
                return "<dark_red>";
            case '5':
                return "<dark_purple>";
            case '6':
                return "<gold>";
            case '7':
                return "<gray>";
            case '8':
                return "<dark_gray>";
            case '9':
                return "<blue>";
            case 'a':
                return "<green>";
            case 'b':
                return "<aqua>";
            case 'c':
                return "<red>";
            case 'd':
                return "<light_purple>";
            case 'e':
                return "<yellow>";
            case 'f':
                return "<white>";
            case 'k':
                return "<obfuscated>";
            case 'l':
                return "<bold>";
            case 'm':
                return "<strikethrough>";
            case 'n':
                return "<underlined>";
            case 'o':
                return "<italic>";
            case 'r':
                return "<reset>";
            default:
                return "";
        }
    }
}
