package org.ow2.chameleon.twitter.example;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.chameleon.twitter.Tweet;
import org.ow2.chameleon.twitter.TwitterService;


@Component
public class TwitterExample {


    @Requires
    private TwitterService twitter;

    @Validate
    public void start() throws Exception {
        displayFirstItem(twitter.getFollowedTimeLine());
        displayFirstItem(twitter.getPublicTimeLine());
        displayFirstItem(twitter.getUserTimeLine("clementplop"));

        displayFirstItem(twitter.getDirectMessages());

        twitter.publishTweet("twitter / chameleon / ipojo ... the ultimate combo (" + System.currentTimeMillis() + ")");
    }

    private void displayFirstItem(List<Tweet> list) {
        if (list != null  && list.size() != 0) {
            Tweet tweet = list.get(0);
            System.out.println("[" + tweet.getPublicationDate() + "] "
                    + tweet.getAuthor() + " > " + tweet.getMessage());
        }
    }
}
