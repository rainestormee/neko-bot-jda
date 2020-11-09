package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.Command;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.MessageFormat;

import static life.nekos.bot.audio.AudioHandler.getTimestamp;

@CommandDescription(
        name = "Back",
        triggers = {"back", "previous", "b"},
        attributes = {
                @CommandAttribute(key = "music", value = "requiresVoiceChannel requiresAudioPerms"),
        },
        description = "plays previous song or restarts current song"
)
public class BackCommand implements Command {

    @Override
    public void execute(Message message, Object... args) {

        AudioTrack track = AudioHandler.getMusicManager(message.getGuild()).player.getPlayingTrack();
        track = track != null ? track : AudioHandler.getMusicManager(message.getGuild()).scheduler.lastTrack;

        if (track != null) {
            MessageEmbed em =
                    new EmbedBuilder()
                            .setAuthor(
                                    message.getJDA().getSelfUser().getName(),
                                    message.getJDA().getInviteUrl(Permission.ADMINISTRATOR),
                                    message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                            .addField(
                                    Formats.info("Now Playing " + Formats.BACK_EMOTE),
                                    MessageFormat.format(
                                            "Track: {0}\nLength: {1}",
                                            track.getInfo().title, getTimestamp(track.getInfo().length)),
                                    false)
                            .setColor(Colors.getEffectiveColor(message))
                            .build();
            message.getChannel().sendMessage(em).queue();
            AudioTrack newTrack = track.makeClone();
            newTrack.setUserData(track.getUserData());
            AudioHandler.getMusicManager(message.getGuild()).player.playTrack(newTrack);
        } else {
            message.getChannel().sendMessage("nu! nya~, i wasn't playing anything before").queue();
        }
    }
}
