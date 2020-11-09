package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.Command;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.audio.GuildMusicManager;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.api.entities.Message;

import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPerms;

@CommandDescription(
        name = "Repeat",
        triggers = {"loop", "repeat"},
        attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "PayWall")},
        description =
                "Set repeat for a track, or --all to loop the entire queue\nThis is a Patreon only command"
)
public class RepeatCommand implements Command {

    @Override
    public void execute(Message message, Object... args) {
        GuildMusicManager musicManager = AudioHandler.getMusicManager(message.getGuild());
        String[] arg = ((String) args[0]).trim().split(" ");
        if (musicManager.scheduler.queue.isEmpty()) {
            message
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
                            m -> {
                                if (canReact(m)) {
                                    m
                                            .addReaction(
                                                    m.getJDA().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                                }
                            });
            return;
        }
        AudioTrack Track = musicManager.player.getPlayingTrack();
        if (!audioPerms(message, Track)) {
            message
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, You don't have permission to do this. " + Formats.NEKO_C_EMOTE))
                    .queue();
            return;
        }

        if (arg.length >= 1) {
            if (arg[0].equalsIgnoreCase("all")) {
                musicManager.scheduler.setLoop(false);
                musicManager.scheduler.setLoopall(
                        !AudioHandler.getMusicManager(message.getGuild()).scheduler.isLoopall());
                message
                        .getChannel()
                        .sendMessage(
                                "Alright nya, I have "
                                        + (musicManager.scheduler.isLoopall()
                                        ? "set the current queue to repeat all tracks "
                                        + Formats.LOOO_ALL_EMOTE
                                        + " "
                                        : "disabled repeat all ")
                                        + Formats.getCat())
                        .queue();
                return;
            }
        }
        {
            musicManager.scheduler.setLoopall(false);
            musicManager.scheduler.setLoop(
                    !AudioHandler.getMusicManager(message.getGuild()).scheduler.isLoop());
            message
                    .getChannel()
                    .sendMessage(
                            "Alright nya, I have "
                                    + (musicManager.scheduler.isLoop()
                                    ? "set the current track to repeat " + Formats.LOOP_EMOTE + " "
                                    : "disabled repeat ")
                                    + Formats.getCat())
                    .queue();
        }
    }
}
