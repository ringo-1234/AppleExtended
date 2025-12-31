package jp.ngt.ngtlib.io;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;

public class TwitterUtil {

    public static class Status {
        private final String text;
        private final String userName;

        public Status(String userName, String text) {
            this.userName = userName;
            this.text = text;
        }

        public String getText() { return text; }
        public User getUser() { return new User(userName); }
    }

    public static class User {
        private final String name;
        public User(String name) { this.name = name; }
        public String getName() { return name; }
    }

    private static final TwitterUtil INSTANCE = new TwitterUtil();

    private TwitterUtil(){}

    public static @Nullable TwitterUtil getInstance() { return INSTANCE; }

    public List<Status> getTweetWithKeyword(String keyword, int count) {
        return new ArrayList<>();
    }

    public List<Status> getTweetWithId(String keyword, int count) {
        return new ArrayList<>();
    }

    public void outputTweetWithTag(String tag, int count, ICommandSender sender) {}
}