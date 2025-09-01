package tomeko.legacyskyblock.chat;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class MVPEmotes {
    private static final List<SimpleEntry<String, String>> EMOTES = new ArrayList<>();

    static {
        EMOTES.add(new SimpleEntry<>("<3", "❤"));
        EMOTES.add(new SimpleEntry<>(":arrow:", "➜"));
        EMOTES.add(new SimpleEntry<>(":bum:", "♿"));
        EMOTES.add(new SimpleEntry<>(":cat:", "= ＾● ⋏ ●＾ ="));
        EMOTES.add(new SimpleEntry<>(":cute:", "(✿◠‿◠)"));
        EMOTES.add(new SimpleEntry<>(":dab:", "<o/"));
        EMOTES.add(new SimpleEntry<>(":dj:", "ヽ(⌐■_■)ノ♬"));
        EMOTES.add(new SimpleEntry<>(":dog:", "(ᵔᴥᵔ)"));
        EMOTES.add(new SimpleEntry<>(":gimme:", "༼つ◕_◕༽つ"));
        EMOTES.add(new SimpleEntry<>(":java:", "☕"));
        EMOTES.add(new SimpleEntry<>(":maths:", "√(π+x)=L"));
        EMOTES.add(new SimpleEntry<>(":no:", "✖"));
        EMOTES.add(new SimpleEntry<>(":peace:", "✌"));
        EMOTES.add(new SimpleEntry<>(":puffer:", "<('O')>"));
        EMOTES.add(new SimpleEntry<>(":pvp:", "⚔"));
        EMOTES.add(new SimpleEntry<>(":shrug:", "¯\\_(ツ)_/¯"));
        EMOTES.add(new SimpleEntry<>(":skull:", "☠"));
        EMOTES.add(new SimpleEntry<>(":sloth:", "(・⊝・)"));
        EMOTES.add(new SimpleEntry<>(":snail:", "@'-'"));
        EMOTES.add(new SimpleEntry<>(":snow:", "☃"));
        EMOTES.add(new SimpleEntry<>(":star:", "✮"));
        EMOTES.add(new SimpleEntry<>(":tableflip:", "(╯°□°）╯︵ ┻━┻"));
        EMOTES.add(new SimpleEntry<>(":thinking:", "(0.o?)"));
        EMOTES.add(new SimpleEntry<>(":totem:", "☉_☉"));
        EMOTES.add(new SimpleEntry<>(":typing:", "✎..."));
        EMOTES.add(new SimpleEntry<>(":wizard:", "('-')⊃━☆ﾟ.*･｡ﾟ"));
        EMOTES.add(new SimpleEntry<>(":yes:", "✔"));
        EMOTES.add(new SimpleEntry<>(":yey:", "ヽ (◕◡◕) ﾉ"));
        EMOTES.add(new SimpleEntry<>("ez", "ｅｚ"));
        EMOTES.add(new SimpleEntry<>("h/", "ヽ(^◇^*)/"));
        EMOTES.add(new SimpleEntry<>("o/", "( ﾟ◡ﾟ)/"));
    }

    public static void register() {
        ClientSendMessageEvents.MODIFY_CHAT.register((message) -> {
            if (LegacySkyblockConfig.MVPEmotesEnabled) {
                return replace(message);
            }

            return message;
        });

        ClientSendMessageEvents.MODIFY_COMMAND.register((command) -> {
            if (LegacySkyblockConfig.MVPEmotesEnabled) {
                String[] words = command.split(" ");
                StringBuilder newCommand = new StringBuilder(words[0]);
                for (int i = 1; i < words.length; i++) {
                    newCommand.append(" ").append(replace(words[i]));
                }
                return newCommand.toString();
            }

            return command;
        });
    }

    private static String replace(String message) {
        for (SimpleEntry<String, String> entry : EMOTES) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }
}
