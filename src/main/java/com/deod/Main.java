package com.deod;

import org.javacord.api.*;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.emoji.CustomEmojiBuilder;
import org.javacord.api.entity.intent.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.sticker.StickerBuilder;
import org.javacord.api.entity.sticker.StickerFormatType;
import org.javacord.api.entity.sticker.StickerItem;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String token = System.getenv("TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(token).addIntents(Intent.MESSAGE_CONTENT).addIntents(Intent.GUILD_EMOJIS).addIntents(Intent.GUILD_MEMBERS).login().join();

        api.addMessageCreateListener(event -> {
            try {
                if (event.getMessageContent().startsWith("$help")){
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Help")
                            .setDescription("Guiding you through Deod")
                            .setAuthor("degu, the medieval coder")
                            .addField("$help", "Show this embed, listing the possible commands for Deod")
                            .addField("$downloadCreate :customEmote:", "sends the source of the emote, and adds it into the current server (doesn't work on DMs)")
                            .addField("$download :customEmote:", "sends the source of the emote (does work on DMs)")
                            .addInlineField("$download @user", "sends the avatar of the mentioned user (does work on DMs)")
                            .addField("$propertyIsTheft :customEmote:", "adds the emote into the current server (doesn't work on DMs)")
                            .addField("$roll [number]", "generates a RNG between 1 and the number sent, standard is 20, so if you don't send any numbers it'll roll between 1 and 20")
                            .addField("$roll [number]d[number]", "generates an amount of numbers equal to the first number, each with a max equal to the second number, shows the numbers rolled and the sum of them all")
                            .addInlineField("adv", "with $roll you can send also the adv flag, which will show all numbers rolled, but it will not sum them all, instead returning the highest rolled value")
                            .addInlineField("disadv", "with $roll you can send also the disadv flag, which will show all numbers rolled, but it will not sum them all, instead returning the lowest rolled value")
                            .addInlineField("h3", "with $roll you can send also the h3 flag, which will show all numbers rolled, but it will sum only the 3 highest values")
                            .addField("$nitro :customEmote:", "Sends the source image for the emote, whilst deleting the trigger message")
                            .addField("$my_github", "Nothing important really, it's just the bot's github page, it has the permalink invite for the bot and an out of date README (I will update it, one day)")
                            .setColor(Color.CYAN);
                    event.getChannel().sendMessage(embed);
                }
                else if (event.getMessageContent().startsWith("$downloadCreate") || event.getMessageContent().startsWith("$dc")) {
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
                    }
                } else if (event.getMessageContent().startsWith("$propertyIsTheft") || event.getMessageContent().startsWith("$pit")){
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
                    }
                } else if (event.getMessageContent().startsWith("$download") || event.getMessageContent().startsWith("$d")){
                    if (event.getMessageContent().contains("<:") || event.getMessageContent().contains("<a:")){
                        String emote = event.getMessageContent().substring(event.getMessageContent().indexOf("<"), event.getMessageContent().indexOf(">"));
                        List<String> splited = List.of(emote.split(":"));
                        boolean isAnimated = splited.get(0).equals("<a");
                        String idEmote = splited.get(splited.size() - 1).replace(">", "");
                        String url = "https://cdn.discordapp.com/emojis/" + idEmote + (isAnimated ? ".gif" : ".png");
                        event.getChannel().sendMessage(url);
                    } else if (event.getMessageContent().contains("@")){
                        String userId = event.getMessageContent().substring(event.getMessageContent().indexOf("@") + 1, event.getMessageContent().indexOf(">"));
                        User pinged = api.getUserById(userId).get();
                        if (event.getServer().isPresent()){
                            Icon avatar = pinged.getEffectiveAvatar(event.getServer().get());
                            event.getChannel().sendMessage("https://cdn.discordapp.com" + avatar.getUrl().getFile());
                        } else {
                            Icon avatar = pinged.getAvatar();
                            event.getChannel().sendMessage("https://cdn.discordapp.com" + avatar.getUrl().getFile());
                        }
                    }
                } else if (event.getMessageContent().startsWith("$roll")){
                    String amount = event.getMessageContent().equals("$roll") ? "20" : event.getMessageContent().substring(6);
                    String flags = "";
                    if(amount.indexOf(" ") > -1){
                        flags = amount.substring(amount.indexOf(" "));
                        amount = amount.substring(0, amount.indexOf(" "));
                    }
                    Random rng = new Random();
                    Integer generated = 0;
                    if (amount.contains("d")){
                        Integer quantity = Integer.parseInt(amount.split("d")[0]);
                        Integer sides = Integer.parseInt(amount.split("d")[1]);
                        List<Integer> generatedList = rng.ints(quantity,1,sides).boxed().collect(Collectors.toList());
                        if (flags.contains("disadv")){
                            for(Integer val : generatedList){
                                if (val < generated || generated == 0) {
                                    generated = val;
                                }
                            }
                        } else if (flags.contains("adv")){
                            for(Integer val : generatedList){
                                if (val > generated) {
                                    generated = val;
                                }
                            }
                        } else if (flags.contains("h3") || quantity >= 3) {
                            generatedList.sort(null);
                            Collections.reverse(generatedList);
                            generated = generatedList.get(0) + generatedList.get(1) + generatedList.get(2);
                        }
                        else {
                            for(Integer val : generatedList){
                                generated += val;
                            }
                        }
                        event.getChannel().sendMessage(generatedList.toString() + "\n" +
                                "**" + generated + "**");
                    } else {
                        generated = rng.ints(1, 1, Integer.parseInt(amount)).sum();
                        event.getChannel().sendMessage(generated.toString());
                    }
                } else if (event.getMessageContent().equals("$my_github")) {
                    event.getChannel().sendMessage("https://github.com/deguree2718/deod");
                } else if (event.getMessageContent().startsWith("$nitro")){
                    if (event.getMessageContent().contains("<")){
                        String emote = event.getMessageContent().substring(event.getMessageContent().indexOf("<"), event.getMessageContent().indexOf(">"));
                        List<String> splited = List.of(emote.split(":"));
                        boolean isAnimated = splited.get(0).equals("<a");
                        String idEmote = splited.get(splited.size() - 1).replace(">", "");
                        String url = "https://cdn.discordapp.com/emojis/" + idEmote + (isAnimated ? ".gif" : ".png");
                        event.getChannel().sendMessage(url);
                        if (event.getMessage().canYouDelete()) {
                            event.getMessage().delete();
                        }
                    } else {
                        String nMessage = event.getMessageContent().substring(7);
                    }
                }
                if (event.getMessageContent().startsWith("$")){
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
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Err")
                .setDescription("Something wrong ain't right | Algo de errado não tá certo")
                .setAuthor("degure, the creator")
                .addField("Algo deu errado, manda print pro deguree2.718 ver oq pode ser", "")
                .addField("Something went wrong, send a screenshot to deguree2.718 so they can check on the error", "")
                .addField("Error:", e.getMessage())
                .setColor(Color.CYAN);
        event.getChannel().sendMessage(embed);
    }

}

// https://discord.com/api/oauth2/authorize?client_id=968280354446270464&scope=bot&permissions=8
