package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.entities.Message;

import java.util.Collections;
import java.util.List;

import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPerms;

@CommandDescription(
        name = "Shuffle",
        triggers = {"shuffle", "mix"},
        attributes = {@CommandAttribute(key = "music", value = "requiresAudioPerms requiresSameVoiceChannel")},
        description = "shuffles the current queue."
)
public class ShuffleCommand implements Command {

    @Override
    public void execute(Message event, Object... args) {
        if (AudioHandler.getMusicManager(event.getGuild()).scheduler.queue.isEmpty()) {
            event
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
                            m -> {
                                if (canReact(m))
                                    m.addReaction(m.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                            });
            return;
        }
        Collections.shuffle((List<?>) AudioHandler.getMusicManager(event.getGuild()).scheduler.queue);
        event
                .getChannel()
                .sendMessage("owo i mixed them all up " + Formats.getCat())
                .queue(
                        message -> {
                            if (canReact(message)) {
                                message
                                        .addReaction(
                                                message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.SHUFFLE_EMOTE)))
                                        .queue();
                            }
                        });
    }
}
