package life.nekos.bot.commons.checks;

import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.text.MessageFormat;
import java.util.stream.LongStream;

import static life.nekos.bot.commons.Constants.OWNERS;

public class MiscChecks {

    public static boolean isOwner(Message msg) {
        NekoBot.log.info(
                MessageFormat.format(
                        "Owner com used by {0}#{1}({2}) on {3} was {4}",
                        msg.getAuthor().getName(),
                        msg.getAuthor().getDiscriminator(),
                        msg.getAuthor().getId(),
                        msg.getChannel().getName(),
                        LongStream.of(OWNERS).anyMatch(x -> x == msg.getAuthor().getIdLong())));
        return LongStream.of(OWNERS).anyMatch(x -> x == msg.getAuthor().getIdLong());
    }

    public static boolean twoWeeks(Message message) {
        long twoWeeksAgo =
                ((System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000)) - MiscUtil.DISCORD_EPOCH)
                        << MiscUtil.TIMESTAMP_OFFSET;
        return MiscUtil.parseSnowflake(message.getId()) < twoWeeksAgo;
    }

    public static boolean isSpam(Message message) {
        return message.getJDA().getSelfUser() == message.getAuthor()
                || message.getContentDisplay().startsWith(Models.getPrefix(message));
    }
}