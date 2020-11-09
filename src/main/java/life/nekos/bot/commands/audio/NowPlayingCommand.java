package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.Command;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.text.MessageFormat;

import static life.nekos.bot.audio.AudioHandler.getTimestamp;

@CommandDescription(
        name = "Playing",
        triggers = {"np", "playing", "now_playing", "song"},
        attributes = {@CommandAttribute(key = "music")},
        description = "Shows info on the playing track"
)
public class NowPlayingCommand implements Command {

    @Override
    public void execute(Message event, Object... args) {
        AudioTrack track = AudioHandler.getMusicManager(event.getGuild()).player.getPlayingTrack();
        if (track != null) {
            User user = (User) track.getUserData();
            MessageEmbed msg =
                    new EmbedBuilder()
                            .setAuthor(
                                    event.getJDA().getSelfUser().getName(),
                                    event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                    event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                            .addField(
                                    Formats.info("Now Playing " + Formats.PLAY_EMOTE),
                                    MessageFormat.format(
                                            "Track: {0}\nLength: {1}/{2}\nQueued by: {3}",
                                            track.getInfo().title,
                                            getTimestamp(track.getPosition()),
                                            getTimestamp(track.getDuration()),
                                            user.getName()),
                                    false)
                            .build();
            event
                    .getChannel()
                    .sendMessage(msg)
                    .queue(
                            message ->
                                    message
                                            .addReaction(
                                                    message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue());
        } else
            event
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
                            message ->
                                    message
                                            .addReaction(
                                                    message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue());
    }
}
