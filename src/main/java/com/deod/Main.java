package com.deod;

import org.javacord.api.*;
import org.javacord.api.entity.emoji.CustomEmojiBuilder;
import org.javacord.api.entity.intent.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String token = System.getenv("TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(token).addIntents(Intent.MESSAGE_CONTENT).addIntents(Intent.GUILD_EMOJIS).addIntents(Intent.GUILD_MEMBERS).login().join();

        api.addMessageCreateListener(event -> {
            try {
                if (event.getMessageContent().startsWith("$help")){
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Help")
                            .setDescription("Guiding you thorugh Deod")
                            .setAuthor("ya boy Degure")
                            .addField("$help", "Show this embed, listing the possible commands for Deod")
                            .addInlineField("$downloadCreate :customEmote:", "sends the source of the emote, and adds it into the current server (doesn't work on DMs)")
                            .addInlineField("$download :customEmote:", "sends the source of the emote (does work on DMs)")
                            .addInlineField("$propertyIsTheft :customEmote:", "adds the emote into the current server (doesn't work on DMs)")
                            .addInlineField("$roll [number]", "generates a RNG between 1 and the number sent, standard is 20, so if you don't send any numbers it'll roll between 1 and 20")
                            .setColor(Color.CYAN);
                    event.getChannel().sendMessage(embed);
                    log(event);
                }
                else if (event.getMessageContent().startsWith("$downloadCreate")) {
                    if (event.getMessageContent().contains("<:") || event.getMessageContent().contains("<a:")) {
                        String emote = event.getMessageContent().substring(event.getMessageContent().indexOf("<"), event.getMessageContent().indexOf(">"));
                        List<String> splited = List.of(emote.split(":"));
                        boolean isAnimated = splited.get(0).equals("<a");
                        String idEmote = splited.get(splited.size() - 1).replace(">", "");
                        String emoteName = splited.get(splited.size() - 2);
                        String url = "https://cdn.discordapp.com/emojis/" + idEmote + (isAnimated ? ".gif" : ".png");
                        event.getChannel().sendMessage(url);
                        if (event.getServer().isPresent()) {
                            URL imageUrl = new URL(url);
                            Server curServ = event.getServer().get();
                            CustomEmojiBuilder emojiBuilder = curServ.createCustomEmojiBuilder();
                            emojiBuilder.setName(emoteName);
                            emojiBuilder.setImage(imageUrl);
                            emojiBuilder.create();
                        }
                        log(event);
                    }
                } else if (event.getMessageContent().startsWith("$propertyIsTheft")){
                    if (event.getMessageContent().contains("<:") || event.getMessageContent().contains("<a:")) {
                        String emote = event.getMessageContent().substring(event.getMessageContent().indexOf("<"), event.getMessageContent().indexOf(">"));
                        List<String> splited = List.of(emote.split(":"));
                        boolean isAnimated = splited.get(0).equals("<a");
                        String idEmote = splited.get(splited.size() - 1).replace(">", "");
                        String emoteName = splited.get(splited.size() - 2);
                        String url = "https://cdn.discordapp.com/emojis/" + idEmote + (isAnimated ? ".gif" : ".png");
                        if (event.getServer().isPresent()) {
                            URL imageUrl = new URL(url);
                            Server curServ = event.getServer().get();
                            CustomEmojiBuilder emojiBuilder = curServ.createCustomEmojiBuilder();
                            emojiBuilder.setName(emoteName);
                            emojiBuilder.setImage(imageUrl);
                            emojiBuilder.create();
                        }
                        log(event);
                    }
                } else if (event.getMessageContent().startsWith("$download")){
                    if (event.getMessageContent().contains("<:") || event.getMessageContent().contains("<a:")){
                        String emote = event.getMessageContent().substring(event.getMessageContent().indexOf("<"), event.getMessageContent().indexOf(">"));
                        List<String> splited = List.of(emote.split(":"));
                        boolean isAnimated = splited.get(0).equals("<a");
                        String idEmote = splited.get(splited.size() - 1).replace(">", "");
                        String url = "https://cdn.discordapp.com/emojis/" + idEmote + (isAnimated ? ".gif" : ".png");
                        event.getChannel().sendMessage(url);
                    }
                    log(event);
                } else if (event.getMessageContent().startsWith("$roll")){
                    String amount = event.getMessageContent().equals("$roll") || event.getMessageContent().equals("!roll ") ? "20" : event.getMessageContent().substring(6);
                    Random rng = new Random();
                    Integer generated = rng.ints(1, 1, Integer.parseInt(amount)).sum();
                    event.getChannel().sendMessage(generated.toString());
                    log(event);
                } else if (event.getMessageContent().equals("$my_github")){
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