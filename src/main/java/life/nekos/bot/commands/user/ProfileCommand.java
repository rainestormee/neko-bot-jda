package life.nekos.bot.commands.user;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import life.nekos.bot.Command;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.text.MessageFormat;
import java.util.Map;

import static life.nekos.bot.commons.Misc.now;
import static life.nekos.bot.commons.checks.UserChecks.isDonor;
import static life.nekos.bot.commons.checks.UserChecks.isDonor_plus;

@CommandDescription(
        name = "profile",
        triggers = {"profile", "rank", "exp"},
        attributes = {@CommandAttribute(key = "user")},
        description = "Shows your profile or a users profile @user o.-"
)
public class ProfileCommand implements Command {

    @Override
    public void execute(Message message, Object... args) {
        Models.statsUp("profile");
        message.addReaction(message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.USER_EMOTE))).queue();
        if (message.getMentionedMembers().isEmpty()) {
            message.getChannel().sendMessage(createProfileEmbedForUser(message.getMember())).queue();
            return;
        }
        message.getMentionedMembers().forEach(m -> {
            if (m.getUser().isBot()) {
                message.getChannel().sendMessage(new EmbedBuilder().setDescription("Bots dont have profiles ;p").build()).queue();
                return;
            }
            message.getChannel().sendMessage(createProfileEmbedForUser(m)).queue();
        });
    }

    public MessageEmbed createProfileEmbedForUser(Member member) {
        User user = member.getUser();
        Map<String, Object> uinfo = Models.getUser(user.getId());
        EmbedBuilder em =  new EmbedBuilder()
                .addField(
                        Formats.LEVEL_EMOTE + " Level",
                        Formats.bold(String.format("%s", uinfo.get("level"))),
                        false)
                .addField(
                        Formats.MAGIC_EMOTE + " Total Experience",
                        Formats.bold(String.format("%s", uinfo.get("exp"))),
                        false)
                .addField(
                        Formats.NEKO_V_EMOTE + " Total Nekos Caught",
                        Formats.bold(String.format("%s", uinfo.get("nekosall"))),
                        false)
                .addField(
                        Formats.NEKO_C_EMOTE + " Current Nekos",
                        Formats.bold(String.format("%s", uinfo.get("nekos"))),
                        false)
                .addField(
                        Formats.DATE_EMOTE + " Date Registered",
                        Formats.bold(String.format("%s", uinfo.get("regdate"))),
                        false)
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setFooter(
                        MessageFormat.format(
                                "Profile for {0} | Today at {1}", user.getName(), now()),
                        "https://media.discordapp.net/attachments/333742928218554368/374966699524620289/profile.png")
                .setColor(Colors.getEffectiveMemberColor(member))
                .setAuthor(
                        String.format("Profile For %s",user.getName()),
                        user.getEffectiveAvatarUrl(),
                        user.getEffectiveAvatarUrl());

        if (isDonor(user)) {
            if (isDonor_plus(user)) {
                em.addField(
                        Formats.PATRON_EMOTE + "Donor+",
                        Formats.bold("Commands & 2x exp,nekos unlocked"),
                        false);
            } else {
                em.addField(
                        Formats.PATRON_EMOTE + "Donor",
                        Formats.bold("Commands unlocked"),
                        false);
            }
        }
        return em.build();
    }
}
