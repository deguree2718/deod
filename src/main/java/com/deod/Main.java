package com.deod;

import org.javacord.api.*;
import org.javacord.api.entity.intent.*;

import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        String token = System.getenv("TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(token).addIntents(Intent.MESSAGE_CONTENT).login().join();

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().startsWith("!download")){
                if (event.getMessageContent().contains("<:") || event.getMessageContent().contains("<a:")){
                    String emote = event.getMessageContent().substring(event.getMessageContent().indexOf("<"), event.getMessageContent().indexOf(">"));
                    List<String> splited = List.of(emote.split(":"));
                    String idEmote = splited.get(splited.size() - 1).replace(">", "");
                    String url = "https://cdn.discordapp.com/emojis/" + idEmote + (emote.startsWith("<a") ? ".gif" : ".png");
                    event.getChannel().sendMessage(url);
                }
            } else if (event.getMessageContent().startsWith("!roll")){
                String amount = event.getMessageContent().equals("!roll") || event.getMessageContent().equals("!roll ") ? "20" : event.getMessageContent().substring(6);
                Random rng = new Random();
                Integer generated = rng.ints(1, 1, Integer.parseInt(amount)).sum();
                event.getChannel().sendMessage(generated.toString());
            }
        });
    }

}

// https://discord.com/api/oauth2/authorize?client_id=968280354446270464&scope=bot&permissions=824633788416