package life.nekos.bot.audio.pornhub;

import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream;

import java.net.URI;

public class PornHubPersistentHttpStream extends PersistentHttpStream {

    public PornHubPersistentHttpStream(
            HttpInterface httpInterface, URI contentUrl, Long contentLength) {
        super(httpInterface, contentUrl, contentLength);
    }

    @Override
    protected URI getConnectUrl() {
        return contentUrl;
    }

    @Override
    protected boolean useHeadersForRange() {
        return false;
    }

    @Override
    public boolean canSeekHard() {
        return false;
    }
}
