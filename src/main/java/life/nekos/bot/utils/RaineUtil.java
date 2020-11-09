package life.nekos.bot.utils;

import club.minnced.discord.webhook.send.WebhookEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.stream.Collectors;

public class RaineUtil {

    public static WebhookEmbed convertToWebhookEmbed(MessageEmbed embed) {
        return new WebhookEmbed(embed.getTimestamp(), embed.getColorRaw(), embed.getDescription(),
                embed.getThumbnail() == null ? null : embed.getThumbnail().getUrl(),
                embed.getImage() == null ? null : embed.getImage().getUrl(),
                embed.getFooter() == null ? null : new WebhookEmbed.EmbedFooter(embed.getFooter().getText(), embed.getFooter().getIconUrl()),
                embed.getTitle() == null ? null : new WebhookEmbed.EmbedTitle(embed.getTitle(), embed.getUrl()),
                embed.getAuthor() == null ? null : new WebhookEmbed.EmbedAuthor(embed.getAuthor().getName(), embed.getAuthor().getIconUrl(), embed.getAuthor().getUrl()),
                embed.getFields().stream().map(f -> new WebhookEmbed.EmbedField(f.isInline(), f.getName(), f.getValue())).collect(Collectors.toList()));
    }
}
