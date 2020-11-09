package life.nekos.bot.commands.owner;

import life.nekos.bot.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.SendNeko;
import life.nekos.bot.commons.SendPoke;
import net.dv8tion.jda.core.entities.Message;


@CommandDescription(
        name = "coin",
        triggers = {"coin"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly"),
        },
        description = "force send a neko"
)
public class CoinCommand implements Command {

    @Override
    public void execute(Message trigger, Object... args) {
        if (args.length > 0){
            SendPoke.send(trigger, false);
            trigger.delete().queue();
            return;
        }
        SendNeko.send(trigger, false);
        trigger.delete().queue();
    }
}
