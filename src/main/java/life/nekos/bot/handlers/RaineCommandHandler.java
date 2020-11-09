package life.nekos.bot.handlers;

import com.github.rainestormee.jdacommand.CommandHandler;
import life.nekos.bot.Command;
import life.nekos.bot.NekoBot;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.audio.VoiceHandler;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.checks.MiscChecks;
import life.nekos.bot.commons.checks.UserChecks;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static life.nekos.bot.commons.checks.UserChecks.isDonor;

public class RaineCommandHandler extends ListenerAdapter {

    private static final ThreadGroup threadGroup = new ThreadGroup("life.nekos.bot.Command Executor");
    private static final Executor commandsExecutor =
            Executors.newCachedThreadPool(r -> new Thread(threadGroup, r, "life.nekos.bot.Command Pool"));

    static {
        threadGroup.setMaxPriority(Thread.MAX_PRIORITY);
    }

    private final CommandHandler<Message> handler;

    public RaineCommandHandler(CommandHandler<Message> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!EventHandler.getREADY() || !BotChecks.noBot(event.getMessage())) return;
        commandsExecutor.execute(() -> {
            Message message = event.getMessage();

            // We should only do this logic if the message sender has an account, they shouldn't need an account until they interact with the bot.
            // I think this is best due to Discords new rules on data we store.

            if (Models.hasUser(event.getMessage())) {
                if (!BotChecks.isDm(event.getMessage())) {
                    if (!Models.hasGuild(event.getGuild())) {
                        Models.newGuild(event.getGuild());
                    }
                    Models.updateUser(message);
                    Models.spwNeko(event);
                }
            }

            String messageContents = message.getContentRaw().trim();
            String prefix = Models.getPrefix(event.getMessage());
            JDA jda = event.getJDA();
            if (messageContents.equals(jda.getSelfUser().getAsMention())) {
                try {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", My prefix is `" + prefix + "`\n`" + prefix + "help` to see my commands. Mew!!").queue();
                } catch (Exception e) {
                    NekoBot.log.error("Error Occurred when sending prompt about bot's prefix", e);
                }
                return;
            }
            if (!Models.hasPrefix(event.getMessage())) return;

            // Now we are only processing events meant for the bot, we can now add users to model.
            if (!Models.hasUser(event.getMessage())) {
                Models.newUser(event.getMessage());
                // TODO: Add a disclaimer? I don't know what to put here -- Raine
                // event.getMessage().getChannel().sendMessage("By using this bot you agree to have some information stored... read more about this here.").queue();
            }

            String[] args = messageContents.split("\\s+");
            String trigger = args[0];

            if (trigger.startsWith(prefix)) {
                trigger = trigger.replaceFirst(prefix, "");
            } else if (trigger.equalsIgnoreCase(jda.getSelfUser().getAsMention())) {
                if (args.length > 1) {
                    trigger = args[1];
                } else {
                    // This case should already be handled on line 38.
                    return;
                }
            }
            Command command = (Command) handler.findCommand(trigger.toLowerCase());

            // If the command is not found.
            if (command == null) {
                return;
            }

            if (command.hasAttribute("OwnerOnly") && !MiscChecks.isOwner(message)) {
                return;
            }

            if (command.hasAttribute("PayWall") && !isDonor(event.getAuthor())) {
                event.getChannel().sendMessage(
                        new EmbedBuilder().setAuthor(event.getJDA().getSelfUser().getName(), event.getJDA()
                                .getInviteUrl(Permission.ADMINISTRATOR), event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                .setColor(Colors.getEffectiveColor(message))
                                .setDescription(
                                        Formats.error("Sorry this command is only available to our Patrons.\n" +
                                                event.getJDA().getShardManager().getEmoteById(475801484282429450L).getAsMention() +
                                                "[Join today](https://www.patreon.com/bePatron?c=1830314&rid=2826101)")
                                ).build())
                        .queue();
                return;
            }

            if (BotChecks.isDm(message)) {
                if (!command.hasAttribute("dm")) {
                    event.getChannel().sendMessage(Formats.error("Nu! nya, you can only use this in a guild")).queue();
                    return;
                }
            } else {
                if (!BotChecks.canSend(message)) {
                    return;
                } else if (!BotChecks.canEmbed(event.getMessage())) {
                    event.getChannel().sendMessage(Formats.error("Nu! nya, I can't do this i do not have permission to use embeds")).queue();
                    return;
                } else if (command.hasAttribute("nsfw") && !event.getTextChannel().isNSFW()) {
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(Colors.getEffectiveColor(event.getMessage())).setDescription("Lewd nekos are shy nya, They can only be found in Discord nsfw channels").build()).queue();
                    return;
                }
            }

            if (command.hasAttribute("music")) {
                boolean sameChannel = command.checkAttribute("music", "requiresSameVoiceChannel");
                if (command.checkAttribute("music", "requiresVoiceChannel") || sameChannel) {
                    if (!message.getMember().getVoiceState().inVoiceChannel()) {
                        message.getChannel().sendMessage(Formats.error("nu nya!~, You must join a voice channel to use the command. " + Formats.NEKO_C_EMOTE)).queue();
                        return;
                    }
                    if (sameChannel && !VoiceHandler.sameVoice(message)) {
                        message.getChannel().sendMessage(Formats.error("nu nya!~, You must join the channel im in to use this command. " + Formats.NEKO_C_EMOTE)).queue();
                        return;
                    }
                } else if (command.checkAttribute("music", "requiresAudioPerms") && !UserChecks.audioPerms(event.getMessage(), AudioHandler.getMusicManager(event.getGuild()).player.getPlayingTrack())) {
                    event.getChannel().sendMessage(Formats.error("nu nya!~, You don't have permission to do this. " + Formats.NEKO_C_EMOTE)).queue();
                    return;
                }
            }

            try {
                Formats.logCommand(event.getMessage());
                handler.execute(command, event.getMessage(), args.length > 1 ? args[1] : "");
            } catch (Exception e) {
                NekoBot.log.error("Error on command", e);
                event.getMessage().addReaction("\uD83D\uDEAB").queue();
            }
        });
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        this.onMessageReceived(new MessageReceivedEvent(event.getJDA(), event.getResponseNumber(), event.getMessage()));
    }
}
