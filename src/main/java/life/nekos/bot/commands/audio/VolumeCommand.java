package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.audio.GuildMusicManager;
import life.nekos.bot.audio.VoiceHandler;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPerms;
import static life.nekos.bot.commons.checks.UserChecks.isDonor;

@CommandDescription(
        name = "Volume",
        triggers = {"Volume", "v", "vol"},
        attributes = {@CommandAttribute(key = "music", value = "requiresSameVoiceChannel"), @CommandAttribute(key = "PayWall")},
        description = "sets the volume.\nThis is a Patreon only command"
)
public class VolumeCommand implements Command {
    public void execute(Message message, Object... args) {
        Models.statsUp("volume");
        GuildMusicManager musicManager = AudioHandler.getMusicManager(message.getGuild());
        if (musicManager.player.getPlayingTrack() != null) {

            int vol = musicManager.player.getVolume();
            if (args.length > 0) {
                String[] arg = ((String) args[0]).trim().split(" ");
                try {
                    int nvol = Math.min(100, Integer.parseInt(arg[0]));
                    musicManager.player.setVolume(nvol);
                    System.out.println("???");
                    message
                            .getChannel()
                            .sendMessage(
                                    MessageFormat.format(
                                            "I set the volume to **{2}** {0}, nya~ {1}",
                                            Formats.getVolEmote(nvol), Formats.getCat(), nvol))
                            .queue(
                                    message1 -> {
                                        if (canReact(message1)) {
                                            message1
                                                    .addReaction(
                                                            message1
                                                                    .getJDA().asBot().getShardManager()
                                                                    .getEmoteById(Formats.getEmoteID(Formats.getVolEmote(nvol))))
                                                    .queue();
                                        }
                                    });

                } catch (NumberFormatException e) {
                    message
                            .getChannel()
                            .sendMessage(
                                    Formats.error(
                                            MessageFormat.format(
                                                    "oh? {0} dun look like a valid number to me! {1}",
                                                    arg[0], Formats.getCat())))
                            .queue();
                    return;
                }
            }
            if (((String) args[0]).isEmpty()) {
                message
                        .getChannel()
                        .sendMessage(
                                MessageFormat.format(
                                        "My volume is currently {1}**{0}** nya~ {2}",
                                        vol, Formats.getVolEmote(vol), Formats.getCat()))
                        .queue(
                                message2 -> {
                                    if (canReact(message2)) {
                                        message2
                                                .addReaction(
                                                        message2
                                                                .getJDA().asBot().getShardManager()
                                                                .getEmoteById(Formats.getEmoteID(Formats.getVolEmote(vol))))
                                                .queue();
                                    }
                                });
            }

        } else {
            message
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
                            message3 -> {
                                if (canReact(message3)) {
                                    message3
                                            .addReaction(
                                                    message3.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                                }
                            });
        }
    }
}
