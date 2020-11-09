package life.nekos.bot;



import com.github.rainestormee.jdacommand.AbstractCommand;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;

public interface Command extends AbstractCommand<Message> {


     default boolean checkAttribute(String key, String value) {
        return this.getAttribute(key) != null && Arrays.asList(this.getAttribute(key).split(" ")).contains(value);
    }
}
