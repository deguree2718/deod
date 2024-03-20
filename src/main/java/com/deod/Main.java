package com.deod;

import org.javacord.api.*;
import org.javacord.api.entity.emoji.CustomEmojiBuilder;
import org.javacord.api.entity.intent.*;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Base64;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        String token = System.getenv("TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(token).addIntents(Intent.MESSAGE_CONTENT).addIntents(Intent.GUILD_EMOJIS).login().join();

        api.addMessageCreateListener(event -> {
            try{
                if (event.getMessageContent().startsWith("!downloadCreate")){
                    if (event.getMessageContent().contains("<:") || event.getMessageContent().contains("<a:")){
                        String emote = event.getMessageContent().substring(event.getMessageContent().indexOf("<"), event.getMessageContent().indexOf(">"));
                        List<String> splited = List.of(emote.split(":"));
                        boolean isAnimated = splited.get(0).equals("<a");
                        String idEmote = splited.get(splited.size() - 1).replace(">", "");
                        String emoteName = splited.get(splited.size() - 2);
                        String url = "https://cdn.discordapp.com/emojis/" + idEmote + (isAnimated ? ".gif" : ".png");
                        event.getChannel().sendMessage(url);
                        if (event.getServer().isPresent()){
                            URL imageUrl = new URL(url);
                            Server curServ = event.getServer().get();
                            CustomEmojiBuilder emojiBuilder = curServ.createCustomEmojiBuilder();
                            emojiBuilder.setName(emoteName);
                            emojiBuilder.setImage(imageUrl);
                            emojiBuilder.create();
                        }
                        log(event);
                    }
                } else if (event.getMessageContent().startsWith("!download")){
                    if (event.getMessageContent().contains("<:") || event.getMessageContent().contains("<a:")){
                        String emote = event.getMessageContent().substring(event.getMessageContent().indexOf("<"), event.getMessageContent().indexOf(">"));
                        List<String> splited = List.of(emote.split(":"));
                        boolean isAnimated = splited.get(0).equals("<a");
                        String idEmote = splited.get(splited.size() - 1).replace(">", "");
                        String url = "https://cdn.discordapp.com/emojis/" + idEmote + (isAnimated ? ".gif" : ".png");
                        event.getChannel().sendMessage(url);
                    }
                    log(event);
                } else if (event.getMessageContent().startsWith("!roll")){
                    String amount = event.getMessageContent().equals("!roll") || event.getMessageContent().equals("!roll ") ? "20" : event.getMessageContent().substring(6);
                    Random rng = new Random();
                    Integer generated = rng.ints(1, 1, Integer.parseInt(amount)).sum();
                    event.getChannel().sendMessage(generated.toString());
                    log(event);
                } else if (event.getMessageContent().equals("!my_github")){
                    event.getChannel().sendMessage("https://github.com/deguree2718/deod");
                    log(event);
                }
            } catch (Exception e){
                log(e, event);
            }
        });
    }

    private static void log(MessageCreateEvent event){
        System.out.println("-------------------------------------------------------------------");
        System.out.println(event.getMessageAuthor().getName());
        if(event.getServer().isPresent()){
            System.out.println(event.getChannel() + " | " + event.getServer().get().getName());
        } else {
            System.out.println(event.getChannel());
        }
        System.out.println(event.getMessageContent());
        System.out.println("-------------------------------------------------------------------");
    }

    private static void log(Exception e, MessageCreateEvent event){
        System.out.println("-------------------------------------------------------------------");
        System.out.println(event.getMessageAuthor().getName());
        if(event.getServer().isPresent()){
            System.out.println(event.getChannel() + " | " + event.getServer().get().getName());
        } else {
            System.out.println(event.getChannel());
        }
        System.out.println(event.getMessageContent() + " | Err: " + e.getMessage());
        System.out.println("-------------------------------------------------------------------");
    }

}

// https://discord.com/api/oauth2/authorize?client_id=968280354446270464&scope=bot&permissions=8