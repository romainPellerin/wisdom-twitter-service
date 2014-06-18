/*
 * Copyright 2009 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *���http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ow2.chameleon.twitter.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.chameleon.twitter.Tweet;
import org.ow2.chameleon.twitter.TwitterService;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.PropertyConfiguration;
import twitter4j.auth.AccessToken;

/*
 * Component implementing and providing the twitter service.
 * This component connects a specific user and allows to interact
 * with his twitter account.
 * Configuration must contain:
 * <ul>
 * <li>twitter.consumer-key : the application key</li>
 * <li>twitter.consumer-key-secret : the application secret</li>
 * <li>twitter.token : the user token</li>
 * <li>twitter.token-secret : the user token secret</li>
 * <li>twitter.userId : the user id</li>
 * <li>twitter.screenName : the screen name</li>
 * </ul>
 *
 * The published service also published the <code>twitter.user</code>
 * service property (screen name).
 *
 * To generate configurations and go through the OAuth, use the {@link Registration}
 * class.
 * <code>java -cp twitter4j-${VERSION}.jar -DconsumerKey=xxx -DconsumerSecret=xxx
 * 	org.ow2.chameleon.twitter.impl.Registration</code>
 * @author <a href="mailto:chameleon-dev@ow2.org">Chameleon Project Team</a>
 */
@Component(name="org.ow2.chameleon.twitter.twitter4j")
@Provides
public class TwitterServiceImpl implements TwitterService {

    /**
     * The application consumer key used for the OAuth.
     * The default one should not be used in production.
     * To create a new key, go to <a href="http://twitter.com/oauth_clients/new">
     * the twitter developer web site</a>
     */
    @Property(name="twitter.consumer-key", value="If0hIte3e7oZNIE2bksA")
    private String m_consumerKey;

    /**
     * The application consumer key secret used for the OAuth.
     * The default one should not be used in production.
     */
    @Property(name="twitter.consumer-key-secret", value="tdaMUrqfnN1xFvOLqgQJDAsoI2q6rZyL0digOYE")
    private String m_consumerKeySecret;

    /**
     * The user token.
     * Retrieved from the OAuth.
     */
    @Property(name="twitter.token", mandatory=true)
	private String m_token;

    /**
     * The user token secret.
     * Retrieved from the OAuth.
     */
    @Property(name="twitter.token-secret", mandatory=true)
	private String m_tokenSecret;

    /**
     * User id (int).
     */
    @Property(name="twitter.userId", mandatory=true)
    private long m_userId;

    /**
     * Screen Name.
     */
    @Property(name="twitter.screenName", mandatory=true)
	private String m_screenName;

    /**
     * The user screen name.
     */
    @ServiceProperty(name=TwitterService.USER_PROPERTY)
    private String m_user;

    /**
     * The twitter object.
     */
    private Twitter m_twitter;

    /**
     * Creates a {@link TwitterServiceImpl} settings the
     * user and password (for testing purpose).
     * @param props - Twitter credentials properties
     */
    protected TwitterServiceImpl(Properties props) {
        m_consumerKey = props.getProperty("twitter.consumer-key");
        m_consumerKeySecret = props.getProperty("twitter.consumer-key-secret");
        m_token = props.getProperty("twitter.token");
        m_tokenSecret = props.getProperty("twitter.token-secret");
        m_userId = new Long(props.getProperty("twitter.userId")).longValue();
        m_screenName = props.getProperty("twitter.screenName");
    }

    /**
     * Creates a {@link TwitterServiceImpl}.
     */
    public TwitterServiceImpl() { }

    /**
     * Starts the instance and checks the credentials.
     * @throws TwitterException the user cannot be authentified.
     */
    @Validate
    public void start() throws TwitterException {
    	System.out.println("[Twitter] - Authenticating " + m_userId + " - " + m_screenName);

        TwitterFactory factory = new TwitterFactory();
        AccessToken authToken = new AccessToken(m_token, m_tokenSecret);
        m_twitter = factory.getInstance();

        // Enable OAuth authentification
        m_twitter.setOAuthAccessToken(authToken);
        m_twitter.setOAuthConsumer(m_consumerKey, m_consumerKeySecret);

	    // Check that we're correctly authentified.
	    m_twitter.verifyCredentials();
	    m_user = m_screenName;
    }

	/**
     * Gets the latest received direct messages.
     * @return the list of the latest received direct messages
     * @see org.ow2.chameleon.twitter.TwitterService#getDirectMessages()
     */
    public List<Tweet> getDirectMessages() {
        try {
            ResponseList messages = m_twitter.getDirectMessages();
            List<Tweet> list = new ArrayList<Tweet>(messages.size());
            for (Object message : messages) {

                list.add(new TweetImpl((DirectMessage) message));
            }
            return list;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the latest messages from the followed time line.
     * @return the latest messages from the followed time line.
     * @see org.ow2.chameleon.twitter.TwitterService#getFollowedTimeLine()
     */
    public List<Tweet> getFollowedTimeLine() {
        try {
            ResponseList messages = m_twitter.getHomeTimeline(); // getFriendsTimeline() doest not exist anymore, todo: search equivalent
            List<Tweet> list = new ArrayList<Tweet>(messages.size());
            for (Object message : messages) {
                list.add(new TweetImpl((Status) message));
            }
            return list;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the latest messages from the public time line.
     * @return the latest messages from the public time line.
     * @see org.ow2.chameleon.twitter.TwitterService#getPublicTimeLine()
     */
    public List<Tweet> getPublicTimeLine() {
        try {
            ResponseList messages = m_twitter.getUserTimeline(); // getPublicTimeline() does not exist anymore, todo: search equivalent
            List<Tweet> list = new ArrayList<Tweet>(messages.size());
            for (Object message : messages) {
                list.add(new TweetImpl((Status)message));
            }
            return list;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the latest messages from a specific user.
     * @param user the user screen name
     * @return the latest messages from a specific user.
     * @see org.ow2.chameleon.twitter.TwitterService#getUserTimeLine(java.lang.String)
     */
    public List<Tweet> getUserTimeLine(String user) {
        try {
            ResponseList messages = m_twitter.getUserTimeline(user);
            List<Tweet> list = new ArrayList<Tweet>(messages.size());
            for (Object message : messages) {
                list.add(new TweetImpl((Status) message));
            }
            return list;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Publishes a status.
     * @param message the message
     * @throws Exception if the message cannot be sent
     * @see org.ow2.chameleon.twitter.TwitterService#publishTweet(java.lang.String)
     */
    public void publishTweet(String message) throws Exception {
        m_twitter.updateStatus(message);
    }

    /**
     * Sends a direct message to a specific user.
     * @param to the user screen name
     * @param message the message
     * @throws Exception if the message cannot be sent
     * @see org.ow2.chameleon.twitter.TwitterService#sendDirectMessage(java.lang.String, java.lang.String)
     */
    public void sendDirectMessage(String to, String message) throws Exception {
        m_twitter.sendDirectMessage(to, message);
    }


    /**
     * Tweet object implementation.
     */
    private class TweetImpl implements Tweet {

        /**
         * The message author.
         */
        private String m_author;

        /**
         * The message content.
         */
        private String m_message;

        /**
         * The custom properties.
         */
        private Map<String, Object> m_props;

        /**
         * The publication date.
         */
        private Date m_date;

        /**
         * Creates a TweetImpl object from a Status.
         * @param status the status
         */
        public TweetImpl(Status status) {
            m_date = status.getCreatedAt();
            m_author = status.getUser().getScreenName();
            m_message = status.getText();

            m_props = new HashMap<String, Object>();
            if (status.getCreatedAt() != null) {
                m_props.put("createdAt", status.getCreatedAt());
            }

            if (status.getGeoLocation() != null) {
                m_props.put("latitude", status.getGeoLocation().getLatitude());
                m_props.put("longitude", status.getGeoLocation().getLongitude());
            }

            if (status.getId() != 0) {
                m_props.put("id", status.getId());
            }

            if (status.getInReplyToScreenName() != null) {
                m_props.put("replyTo", status.getInReplyToScreenName());
            }

            if (status.getSource() != null) {
                m_props.put("source", status.getSource());
            }

            m_props.put("favorited", status.isFavorited());
            m_props.put("retweet", status.isRetweet());
            m_props.put("truncated", status.isTruncated());
        }

        /**
         * Creates a TweetImpl object from a Direct Message.
         * @param direct the direct message
         */
        public TweetImpl(DirectMessage direct) {
            m_date = direct.getCreatedAt();
            m_author = direct.getSenderScreenName();
            m_message = direct.getText();

            m_props = new HashMap<String, Object>();
            if (direct.getCreatedAt() != null) {
                m_props.put("createdAt", direct.getCreatedAt());
            }

            if (direct.getId() != 0) {
                m_props.put("id", direct.getId());
            }

            if (direct.getRecipientScreenName() != null) {
                m_props.put("recipient", direct.getRecipientScreenName());
            }
        }

        /**
         * Gets the author.
         * @return the author
         * @see org.ow2.chameleon.twitter.Tweet#getAuthor()
         */
        public String getAuthor() {
           return m_author;
        }

        /**
         * Gets the message content.
         * @return the content
         * @see org.ow2.chameleon.twitter.Tweet#getMessage()
         */
        public String getMessage() {
            return m_message;
        }

        /**
         * Gets the custom properties.
         * @return the custom properties
         * @see org.ow2.chameleon.twitter.Tweet#getProperties()
         */
        public Map<String, Object> getProperties() {
            return m_props;
        }

        /**
         * Gets the publication date.
         * @return the publication date
         * @see org.ow2.chameleon.twitter.Tweet#getPublicationDate()
         */
        public Date getPublicationDate() {
            return m_date;
        }

    }

}
