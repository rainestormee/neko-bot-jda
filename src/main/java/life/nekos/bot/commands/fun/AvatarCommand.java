package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import static life.nekos.bot.commons.Misc.UA;

@CommandDescription(
        name = "avatar",
        triggers = {"avatar", "ava", "pfp", "avi"},
        attributes = {@CommandAttribute(key = "user")},
        description =
                "\nShows your or a @users avatar\nAdd --dm to have the image sent to you in a dm\nAdd --new for a new avatar for you and --new --nsfw for a new nsfw avatar for you"
)
public class AvatarCommand implements Command {

    @Override
    public void execute(Message message, Object... argo) {
        String args = ((String) argo[0]).toLowerCase();
        Models.statsUp("avatar");
        EmbedBuilder response = new EmbedBuilder().setColor(Colors.getEffectiveColor(message));
        boolean nsfw = false;
        try {
            if (args.contains("--new")) {
                if (args.contains("--nsfw")) {
                    nsfw = true;
                    response.setDescription(Formats.info(MessageFormat.format("Hey {0}! Here is a new nsfw Avatar, Nya~ {1}", message.getAuthor().getName(), Formats.getCat())))
                            .setImage(Nekos.getNsfwAvatar());
                } else {
                    response.setDescription(Formats.info(MessageFormat.format("Hey {0}! Here is a new Avatar, Nya~ {1}", message.getAuthor().getName(), Formats.getCat())));
                }
            } else {
                User user = message.getMentionedUsers().stream().findFirst().orElse(message.getAuthor());
                String name = MessageFormat.format("{0}.png", user.getName());
                URL url = new URL(user.getEffectiveAvatarUrl() + "?size=1024");
                URLConnection connection = url.openConnection();
                connection.setRequestProperty(UA[0], UA[1]);
                if (connection.getContentType().equals("image/gif"))
                    name = MessageFormat.format("{0}.gif", user.getName());

                Message msg = new MessageBuilder().append(MessageFormat.format("Avatar for {0} nya~", user.getName())).build();
                if (args.contains("--dm")) {
                    String finalName = name;
                    message.getAuthor().openPrivateChannel().queue(pc -> {
                        try {
                            pc.sendFile(connection.getInputStream(), finalName, msg).queue(r -> message.addReaction("📬").queue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, (f) -> message.getChannel().sendMessage("could not send message!!").queue());
                } else {
                    message.getTextChannel().sendFile(connection.getInputStream(), name, msg).queue();
                }
                return;
            }
            if (args.contains("--dm")) {
                message.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(response.build()).queue(r -> message.addReaction("📬").queue()));
            } else {
                if (nsfw && !message.getTextChannel().isNSFW()) {
                    message.getTextChannel().sendMessage(Formats.error("Nu, nya use this in a nsfw channel or add `--dm` to the end")).queue();
                } else {
                    message.getTextChannel().sendMessage(response.build()).queue();
                }
            }
        } catch (Exception e) {
            NekoBot.log.error("Error obtaining pfp from api", e);
        }
    }
}