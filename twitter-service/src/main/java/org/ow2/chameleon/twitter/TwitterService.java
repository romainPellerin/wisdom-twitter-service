/*
 * Copyright 2009 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ow2.chameleon.twitter;

import java.util.List;

/**
 * Twitter Service allowing to easily deal with twitter. Providers should
 * publish the <tt>org.ow2.chameleon.twitter.user</tt> property indicating the
 * connected user.
 * @author <a href="mailto:chameleon-dev@ow2.org">Chameleon Project Team</a>
 * @version 1.0.0
 */
public interface TwitterService {

    /**
     * User property.
     */
    public static final String USER_PROPERTY = "org.ow2.chameleon.twitter.user";

    /**
     * Updates the status of the connected user.
     * @param message the new status
     * @throws Exception if the message cannot be published.
     */
    public void publishTweet(String message) throws Exception;

    /**
     * Gets public time line.
     * @return the 20 first items from the public time line.
     */
    public List<Tweet> getPublicTimeLine();

    /**
     * Gets followed 'friends' time line.
     * @return the 20 first items from the followed time line.
     */
    public List<Tweet> getFollowedTimeLine();

    /**
     * Gets time line of a specific user.
     * @param user the user screen name
     * @return the 20 first items from the given user feed.
     */
    public List<Tweet> getUserTimeLine(String user);

    /**
     * Sends a private message to a specific user
     * @param to the user screen name
     * @param message the message
     * @throws Exception if the message cannot be sent
     */
    public void sendDirectMessage(String to, String message) throws Exception;

    /**
     * Gets the 20 last received private messages.
     * @return the 20 last received private messages
     */
    public List<Tweet> getDirectMessages();

}
