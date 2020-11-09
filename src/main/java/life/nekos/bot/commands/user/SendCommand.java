package life.nekos.bot.commands.user;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.Command;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@CommandDescription(
        name = "SendCommand",
        triggers = {"send"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly"),
                @CommandAttribute(key = "dm"),
        },
        description = "SendCommand"
)
public class SendCommand implements Command {

    @Override
    public void execute(Message event, Object... argo) {
        String args = (String) argo[0];
        Models.statsUp("send");
        User author = event.getAuthor();
        TextChannel ch = event.getTextChannel();
        List<User> u = event.getMentionedUsers();
        List<User> users = new ArrayList<>(u);
        String[] arg = args.trim().split(" ");
        if (users.isEmpty()) {
            users.add(author);
        }
        if (args.length() == 0) {
            return;
            // TODO: no args therefore send command help
        }

        String neko;
        String type;
        try {
            if (arg[0].equals("neko")) {
                neko = Nekos.getNeko();
            } else if (arg[0].equals("lewd")) {
                neko = Nekos.getLewd();
            } else {
                return;
            }
            type = arg[0];
            users.forEach(m -> {
                MessageEmbed embed = new EmbedBuilder().setDescription(Formats.NEKO_C_EMOTE)
                        .setTitle(MessageFormat.format("hey {0}, {1} has sent you a {2}", m.getName(), event.getAuthor().getName(), arg[0]), type)
                        .setColor(Colors.getDominantColor(m))
                        .setImage(neko)
                        .build();
                m.openPrivateChannel().queue(pc -> pc.sendMessage(embed).queue(null, (a) -> ch.sendMessage(MessageFormat.format("hey, This {0} {1} has me blocked or there filter turned on \uD83D\uDD95", "whore", m.getName())).queue()), Throwable::printStackTrace);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
