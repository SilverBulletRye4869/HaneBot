package net.serveron.hane.ranking;

import net.dv8tion.jda.api.EmbedBuilder;
import net.serveron.hane.MainSystem;

import java.util.LinkedHashMap;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public record Sender() {

    public static void send(String type, LinkedHashMap<String, Long> data, String unit) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**" + type + "ランキング！**");
        StringJoiner sj = new StringJoiner("\n");
        AtomicInteger rank = new AtomicInteger(0);
        AtomicLong lastScore = new AtomicLong(-1);
        data.forEach((id, value) -> {
            if (value != lastScore.get()) {
                rank.incrementAndGet();
                lastScore.set(value);
            }
            if(type.equals("loginTime"))sj.add("**" + rank + "位: <@" + id + "> (" + String.format("%.1f",value/60.0)+ unit+")**");
            else sj.add("**" + rank + "位: <@" + id + "> (" + value+ unit+")**");
        });
        eb.addField("", sj.toString(), false);
        MainSystem.getTextChannel().sendMessageEmbeds(eb.build()).queue();
    }

}
