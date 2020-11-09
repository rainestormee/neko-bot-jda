package life.nekos.bot.commands.mod;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import static life.nekos.bot.commons.checks.BotChecks.canBan;
import static life.nekos.bot.commons.checks.UserChecks.isAdmin;

@CommandDescription(
        name = "kick",
        triggers = {"kick"},
        attributes = {
                @CommandAttribute(key = "mod"),
                @CommandAttribute(key = "dm", value = "no"),
        },
        description = "Kicks a asshat"
)
public class KickCommand implements Command {
    @Override
    public void execute(Message message, Object... args) {
        if (message.getMentionedMembers().isEmpty()) {

            message.getTextChannel().sendMessage(Formats.error("You must tag a user")).queue();
            return;
        }
        if (!isAdmin(message)) {
            message
                    .getTextChannel()
                    .sendMessage(Formats.error("You lack the `kick_members` permission"))
                    .queue();
            return;
        }
        if (!canBan(message)) {
            message
                    .getTextChannel()
                    .sendMessage(Formats.error("I lack the `kick_members` permission"))
                    .queue();
            return;
        }
        Member m = message.getMentionedMembers().get(0);
        m.getGuild()
                .getController()
                .kick(m)
                .queue(
                        mm -> message
                                .getTextChannel()
                                .sendMessage(Formats.info("Kicked " + m.getEffectiveName() + ", Nya~"))
                                .queue());
    }
}

