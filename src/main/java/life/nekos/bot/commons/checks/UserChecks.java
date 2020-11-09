package life.nekos.bot.commons.checks;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.commons.Constants;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class UserChecks {
    public static boolean isAdmin(Message msg) {
        return MiscChecks.isOwner(msg)
                || PermissionUtil.checkPermission(msg.getMember(), Permission.BAN_MEMBERS);
    }

    public static boolean isMod(Message msg) {
        return MiscChecks.isOwner(msg)
                || PermissionUtil.checkPermission(msg.getMember(), Permission.MESSAGE_MANAGE);
    }

    public static boolean isDJ(Member member) {
        return member.getRoles().stream().allMatch(x -> x.getName().equalsIgnoreCase("dj"));
    }

    public static boolean isDonor(User user) {
        Member member = user.getJDA().getShardManager().getGuildById(Constants.HOME_GUILD).getMember(user);
        if (member != null) {
            return member.getRoles().parallelStream().anyMatch(x -> x.getIdLong() == 392350099331743765L);
        } else return false;
    }

    public static boolean isDonor_plus(User user) {
        Member member = user.getJDA().getShardManager().getGuildById(Constants.HOME_GUILD).getMember(user);
        if (member != null) {
            return member.getRoles().parallelStream().anyMatch(x -> x.getIdLong() == 475508839266123786L);
        } else return false;
    }

    public static boolean audioPerms(Message msg, AudioTrack track) {
        User user = (User) track.getUserData();
        if (isMod(msg)) {
            return true;
        }
        if (msg.getAuthor() == user) {
            return true;
        }
        if (isDJ(msg.getMember())) {
            return true;
        }
        return msg.getMember()
                .getVoiceState()
                .getChannel()
                .getMembers()
                .stream()
                .filter(member -> !member.getUser().isBot())
                .toArray()
                .length
                == 1
                || MiscChecks.isOwner(msg);
    }
}
