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
import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPerms;

@CommandDescription(
        name = "skip",
        triggers = {"skip", "s", "next"},
        attributes = {@CommandAttribute(key = "music", value = "requiresSameVoiceChannel requiresAudioPerms")},
        description = "skips the current track."
)
public class SkipCommand implements Command {

    @Override
    public void execute(Message event, Object... args) {
        if (AudioHandler.getMusicManager(event.getGuild()).scheduler.queue.isEmpty()) {
            event
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
                            message -> {
                                if (canReact(message)) {
                                    message
                                            .addReaction(
                                                    message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                                }
                            });
            return;
        }

        AudioHandler.getMusicManager(event.getGuild()).scheduler.nextTrack();
        event
                .getChannel()
                .sendMessage("Next Please! " + Formats.getCat())
                .queue(
                        message -> {
                            if (canReact(message)) {
                                message
                                        .addReaction(
                                                message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.NEXT_EMOTE)))
                                        .queue();
                            }
                        });
        AudioTrack track = AudioHandler.getMusicManager(event.getGuild()).player.getPlayingTrack();
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
                        message -> {
                            if (canReact(message)) {
                                message
                                        .addReaction(
                                                message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                        .queue();
                            }
                        });
    }
}
