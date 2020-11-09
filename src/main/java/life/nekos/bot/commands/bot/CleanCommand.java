package life.nekos.bot.commands.bot;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static life.nekos.bot.commons.checks.BotChecks.canDelete;
import static life.nekos.bot.commons.checks.MiscChecks.isSpam;
import static life.nekos.bot.commons.checks.MiscChecks.twoWeeks;

@CommandDescription(
        name = "Clean",
        triggers = {"clean", "cleanup", "purge", "del"},
        attributes = {@CommandAttribute(key = "bot")},
        description = "Cleans up all the bot and trigger messages"
)
public class CleanCommand implements Command {

    @Override
    public void execute(Message message, Object... args) {
        if (!canDelete(message)) {
            message
                    .getChannel()
                    .sendMessage(Formats.error("I Can't delete messages, check my permissions nya~"))
                    .queue();
            return;
        }
        message.delete().queue();
        message.getChannel().getHistory().retrievePast(100).queue(ms -> {
            List<Message> spam = ms.stream().filter(m -> !twoWeeks(m) && isSpam(m)).collect(Collectors.toList());
            if (spam.isEmpty()) {
                return;
            }
            if (spam.size() <= 2) {
                spam.get(0).delete().queue();
                return;
            }
            try {
                message.getTextChannel().deleteMessages(spam).queue();
                message
                        .getChannel()
                        .sendMessage(Formats.info(MessageFormat.format("I deleted {0} messages \\o/", spam.size())))
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            } catch (Exception e) {
                NekoBot.log.error("some clean error ", e);
                message.getChannel().sendMessage(Formats.error("some error sry nya~")).queue();
            }
        });
    }
}
