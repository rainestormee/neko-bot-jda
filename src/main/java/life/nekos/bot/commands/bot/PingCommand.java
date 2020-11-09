package life.nekos.bot.commands.bot;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import life.nekos.bot.Command;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static life.nekos.bot.NekoBot.waiter;
import static life.nekos.bot.handlers.EventHandler.getShards;

@CommandDescription(
        name = "ping",
        triggers = "ping",
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "bot")},
        description = "Pong!"
)
public class PingCommand implements Command {

    private final Paginator.Builder builder = new Paginator.Builder().setColumns(1).setItemsPerPage(10).showPageNumbers(true).waitOnSinglePage(false).useNumberedItems(true).setFinalAction(
            m -> {
                try {
                    m.clearReactions().queue();
                } catch (PermissionException ex) {
                    m.delete().queue();
                }
            })
            .setEventWaiter(waiter)
            .setTimeout(1, TimeUnit.MINUTES);

    @Override
    public void execute(Message trigger, Object... argo) {
        Models.statsUp("ping");
        String args = (String) argo[0];
        if (args.equalsIgnoreCase("--all")) {
            Map<JDA, JDA.Status> s = getShards().getStatuses();
            builder.clearItems();
            s.forEach((k, v) ->
                    builder.addItems(MessageFormat.format("Shard: {0}, Ping: {1}ms, Status: {2} {3}\n",
                            k.getShardInfo().getShardId(), k.getPing(), v,
                            trigger.getJDA().getShardInfo().getShardId() == k.getShardInfo().getShardId() ? "(This guild)" : "")
                    )
            );

            Paginator p = builder.setColor(Colors.getEffectiveColor(trigger)).setText(Formats.MAGIC_EMOTE + " **Global Pings** " + Formats.NEKO_C_EMOTE)
                    .setUsers(trigger.getAuthor()).build();
            p.paginate(trigger.getChannel(), 1);
            return;
        }
        trigger.getChannel().sendMessage("⏳ Ping: " + trigger.getJDA().getPing() + "ms").queue();
    }
}
