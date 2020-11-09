package life.nekos.bot.server;

import com.github.rainestormee.jdacommand.AbstractCommand;
import com.google.gson.Gson;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.db.Models;
import life.nekos.bot.handlers.EventHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

/**
 * Created by Tom on 9/29/2017.
 */
public class Server {

    public static void StartServer() {
        port(8989);
        Gson g = new Gson();

        before(
                (request, response) ->
                        NekoBot.log.info(
                                MessageFormat.format("req from {0} for {1}", request.ip(), request.url())));

        after(
                (request, response) -> {
                    response.header("Content-Encoding", "gzip");
                    response.header("Access-Control-Allow-Origin", "*");
                });

        get(
                "/api/stats",
                (req, res) -> {
                    Map stats = Models.getTopStats();
                    res.type("application/json");
                    return g.toJson(stats);
                });

        List<String> comlist = new ArrayList<>();
        Set<AbstractCommand<Message>> comms = NekoBot.commandHandler.getCommands();
        for (AbstractCommand<Message> c : comms) {
            if (!c.hasAttribute("OwnerOnly")) {
                comlist.add(
                        String.format(
                                "%s Aliases[%s] %s",
                                c.getDescription().name(),
                                String.join(", ", c.getDescription().triggers()),
                                c.getDescription().description()));
            }
        }
        get(
                "/api/commands",
                (req, res) -> {
                    res.type("application/json");
                    return g.toJson(comlist);
                });

        get(
                "/api/count",
                (req, res) -> {
                    res.type("application/json");
                    return g.toJson(
                            MessageFormat.format(
                                    "{0},{1},{2}",
                                    EventHandler.getShards().getShardsRunning(),
                                    EventHandler.getShards().getShardsTotal(),
                                    EventHandler.getJDA().getShardManager().getGuilds().size()));
                });

        get(
                "/api/guilds",
                (req, res) -> {
                    List<String> Guilds = new ArrayList<>();
                    List<Guild> guilds = EventHandler.getShards().getGuilds();
                    for (Guild gui : guilds) {
                        Guilds.add(
                                String.format(
                                        "{guild: %s(%s), owner: %s(%s),users: %s}",
                                        gui.getName(),
                                        gui.getId(),
                                        gui.getOwner().getEffectiveName(),
                                        gui.getOwner().getUser().getId(),
                                        gui.getMembers().size()));
                    }
                    res.type("application/json");
                    return "Guilds: " + g.toJson(Guilds) + " }";
                });
    }
}
