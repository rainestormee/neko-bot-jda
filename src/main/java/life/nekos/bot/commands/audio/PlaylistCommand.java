package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.text.MessageFormat;

@CommandDescription(
        name = "playlist",
        triggers = {"playlist", "pl"},
        attributes = {@CommandAttribute(key = "music", value = "requiresVoiceChannel")},
        description = "play some playlist url | search term"
)
public class PlaylistCommand implements Command {

    @Override
    public void execute(Message event, Object... args) {
        if (args.length == 0) {
            event
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setAuthor(
                                            Formats.error("Missing Args"),
                                            event.getJDA().getInviteUrl(Permission.ADMINISTRATOR),
                                            event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .addField(
                                            Formats.info("Info"),
                                            MessageFormat.format(
                                                    "{0}playlist <some playlist url>", Models.getPrefix(event)),
                                            false)
                                    .build())
                    .queue();
        } else {
            AudioHandler.loadAndPlay(event, (String) args[0], true);
            event
                    .addReaction(event.getJDA().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAYLIST_EMOTE)))
                    .queue();
        }
    }
}
