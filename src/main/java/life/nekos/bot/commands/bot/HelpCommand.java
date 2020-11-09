package life.nekos.bot.commands.bot;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.Misc;
import life.nekos.bot.commons.checks.MiscChecks;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@CommandDescription(
        name = "help",
        triggers = {"help", "halp", "halllp", "coms", "commands"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "bot")},
        description = "help, --dm for dm"
)
public class HelpCommand implements Command {

    private final HashMap<String, String> categories =
            new HashMap<String, String>() {
                {
                    put("music", Formats.NOTES_EMOTE);
                    put("bot", Formats.BOT_EMOTE);
                    put("user", Formats.USER_EMOTE);
                    put("fun", Formats.MAGIC_EMOTE);
                    put("nsfw", Formats.LEWD_EMOTE);
                    put("neko", Formats.NEKO_C_EMOTE);
                    put("owner", Formats.INFO_EMOTE);
                    put("guild", Formats.DISCORD_EMOTE);
                    put("mod", Formats.NO_EMOTE);
                }
            };

    private static MessageEmbed comHelp(Message msg, Command command, String prefix) {
        CommandDescription description = command.getDescription();
        return new EmbedBuilder()
                .setAuthor(
                        msg.getJDA().getSelfUser().getName() + " life.nekos.bot.Command info",
                        msg.getJDA().getInviteUrl(Permission.ADMINISTRATOR),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .addField(
                        Formats.info("Info"),
                        MessageFormat.format(
                                "life.nekos.bot.Command:\n**{0}{1}**\nAliases:\n**{2}**\nDescription:\n**{3}**",
                                prefix,
                                description.name(),
                                String.join(", ", description.triggers()),
                                command.getDescription().description()),
                        false)
                .setColor(Colors.getRndColor())
                .setFooter(
                        MessageFormat.format(
                                "Help requested by {0} | {1}", msg.getAuthor().getName(), Misc.now()),
                        msg.getAuthor().getEffectiveAvatarUrl())
                .build();
    }

    private static boolean isCom(Command command) {
        return command.getDescription() != null || command.hasAttribute("description");
    }

    private String titleCase(String word) {
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }

    @Override
    public void execute(Message trigger, Object... argo) {
        String args = (String) argo[0];
        Models.statsUp("help");
        trigger.getChannel().sendTyping().queue();

        String prefix = Models.getPrefix(trigger);
        String gp = "\\" + prefix;
        Map<String, Long> stats = Models.getStats();
        EmbedBuilder em = new EmbedBuilder();

        HashMap<String, StringBuilder> builders = new HashMap<>();
        categories.forEach((key, value) -> builders.put(key, new StringBuilder()));

        NekoBot.commandHandler.getCommands().forEach(c -> {
            String category = c.hasAttribute("OwnerOnly") ? "owner" : builders.keySet().stream().filter(c::hasAttribute).findFirst().orElse("");
            builders.get(category).append(String.format(
                    "**%s%s**: %s\n***Triggers***: `%s`\n\n",
                    gp,
                    c.getDescription().name(),
                    c.getDescription().description(),
                    String.join(", ", c.getDescription().triggers())));
        });

        builders.forEach((k, v) -> {
            if (v.toString().equals("owner") && !MiscChecks.isOwner(trigger)) return;
            if ((trigger.getTextChannel() != null) && !trigger.getTextChannel().isNSFW() && v.toString().equals("nsfw")) {
                v.setLength(0);
                v.append("Use help in an NSFW channel to see these commands");
            }
            em.addField(k + " " + titleCase(k), v.toString(), false);
        });

        em.setColor(Colors.getDominantColor(trigger.getAuthor()));
        em.addField(Formats.LINK_EMOTE + " Links", Formats.LING_MSG, false);
        em.addField("Times help used\n", stats.get("help").toString(), false);
        em.setFooter(MessageFormat.format("Help requested by {0} | {1}", trigger.getAuthor().getName(), Misc.now()), trigger.getAuthor().getEffectiveAvatarUrl());
        em.setAuthor(
                trigger.getJDA().getSelfUser().getName() + " help " + Formats.MAGIC_EMOTE,
                trigger.getJDA().getInviteUrl(Permission.ADMINISTRATOR),
                trigger.getJDA().getSelfUser().getEffectiveAvatarUrl());
        MessageEmbed embed = em.build();

        if (args.length() != 0) {
            Command command = (Command) NekoBot.commandHandler.findCommand(args.split(" ")[0]);
            if (args.toLowerCase().endsWith("--dm")) {
                trigger.getAuthor().openPrivateChannel().queue(pm -> pm.sendMessage(command == null ? embed : comHelp(trigger, command, gp)).queue());
                return;
            }
            if (command == null || !isCom(command)) {
                trigger.getChannel().sendMessage("That command does not exist.").queue();
                return;
            }
            trigger.getChannel().sendMessage(comHelp(trigger, command, gp)).queue();
        } else {
            trigger.getChannel().sendMessage(embed).queue();
        }
    }
}

