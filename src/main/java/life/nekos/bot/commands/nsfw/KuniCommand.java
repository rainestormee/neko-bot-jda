package life.nekos.bot.commands.nsfw;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

@CommandDescription(
        name = "kuni",
        triggers = {"kuni", "Kuni"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "nsfw"), @CommandAttribute(key = "PayWall")},
        description = "random kuni owO"
)
public class KuniCommand implements Command {
    @Override
    public void execute(Message trigger, Object... args) {
        Models.statsUp("kuni");
        if (!trigger.getTextChannel().isNSFW()) {
            trigger
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.getEffectiveColor(trigger))
                                    .setDescription(
                                            "Lewd nekos are shy nya, They can only be found in Discord nsfw channels")
                                    .build())
                    .queue();
            return;
        }
        try {
            trigger
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setDescription("kuni owo")
                                    .setColor(Colors.getEffectiveColor(trigger))
                                    .setImage(Nekos.getKuni())
                                    .build())
                    .queue();
        } catch (Exception e) {
            trigger.getChannel().sendMessage(Formats.error("oh? something broken nya!")).queue();
            NekoBot.log.error("lewd command broken? ", e);
        }
    }
}
