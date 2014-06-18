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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class TwitterServiceImplTest {

	@Test
	public void testTwitterLogin() throws TwitterException {
		String consumerKey = "PUT_YOURS";
		String consumerSecret = "PUT_YOURS";
		String token = "PUT_YOURS";
		String tokenSecret = "PUT_YOURS";

        TwitterFactory factory = new TwitterFactory();
        AccessToken authToken = new AccessToken(token, tokenSecret);
        Twitter m_twitter = factory.getInstance();

        // Enable OAuth authentification
        m_twitter.setOAuthAccessToken(authToken);
        m_twitter.setOAuthConsumer(consumerKey, consumerSecret);

        // Check that we're correctly authentified.
        m_twitter.verifyCredentials();

	    Trends trends = m_twitter.getPlaceTrends(1);
	    for (int i = 0; i < trends.getTrends().length; i++) {
	    	System.out.println(trends.getTrends()[i].getName());
	    }

	}

	private Properties getChameleonProperties() {
		File cfg = new File("src/test/resources/org.ow2.chameleon.twitter.twitter4j-ow2_chameleon_t.cfg");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(cfg));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		return props;
	}

	private Properties getWrongChameleonProperties() {
		File cfg = new File("src/test/resources/org.ow2.chameleon.twitter.twitter4j-ow2_chameleon_t_wrong.cfg");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(cfg));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		return props;
	}

    @Test
    public void testConnection() throws TwitterException {
        TwitterServiceImpl svc = new TwitterServiceImpl(getChameleonProperties());
        svc.start();
    }

    @Test(expected=Exception.class)
    public void testBadConnection() throws TwitterException {
        TwitterServiceImpl svc = new TwitterServiceImpl(getWrongChameleonProperties());
        svc.start();
    }


    @Test
    public void sendStatus() throws Exception {
        TwitterServiceImpl svc = new TwitterServiceImpl(getChameleonProperties());
        svc.start();
        svc.publishTweet("chameleon-test " + System.currentTimeMillis());
    }

    @Test
    public void sendPrivateMessage() throws Exception {
        TwitterServiceImpl svc = new TwitterServiceImpl(getChameleonProperties());
        svc.start();
        svc.sendDirectMessage("clementplop", "chameleon tweets therefore chameleon is (" + System.currentTimeMillis() + ")");
    }

    @Test(expected=Exception.class)
    public void sendPrivateMessageToUnknown() throws Exception {
        TwitterServiceImpl svc = new TwitterServiceImpl(getChameleonProperties());
        svc.start();
        svc.sendDirectMessage("clementplopX", "chameleon tweets therefore chameleon is");
    }

    @Test
    public void testPublicTimeLine() throws TwitterException {
        TwitterServiceImpl svc = new TwitterServiceImpl(getChameleonProperties());
        svc.start();
        java.util.List<org.ow2.chameleon.twitter.Tweet> list = svc.getPublicTimeLine();
        Assert.assertNotNull(list);
        //Assert.assertEquals(20, list.size());
        for (org.ow2.chameleon.twitter.Tweet t : list) {
            Assert.assertNotNull(t.getAuthor());
            Assert.assertNotNull(t.getMessage());
            Assert.assertNotNull(t.getPublicationDate());
            Assert.assertNotNull(t.getProperties());
            System.out.println(t.getAuthor() + " - " + t.getMessage());
            System.out.println(t.getProperties());
        }
    }

    @Test
    public void testFollowingTimeLine() throws TwitterException {
        TwitterServiceImpl svc = new TwitterServiceImpl(getChameleonProperties());
        svc.start();
        java.util.List<org.ow2.chameleon.twitter.Tweet> list = svc.getFollowedTimeLine();
        Assert.assertNotNull(list);
        Assert.assertTrue(10 <= list.size());
        for (org.ow2.chameleon.twitter.Tweet t : list) {
            Assert.assertNotNull(t.getAuthor());
            Assert.assertNotNull(t.getMessage());
            Assert.assertNotNull(t.getPublicationDate());
            Assert.assertNotNull(t.getProperties());
            System.out.println(t.getAuthor() + " - " + t.getMessage());
            System.out.println(t.getProperties());
        }
    }

    @Test
    public void testUserTimeLine() throws TwitterException {
        TwitterServiceImpl svc = new TwitterServiceImpl(getChameleonProperties());
        svc.start();
        java.util.List<org.ow2.chameleon.twitter.Tweet> list = svc.getUserTimeLine("clementplop");
        Assert.assertNotNull(list);
        Assert.assertEquals(20, list.size());
        for (org.ow2.chameleon.twitter.Tweet t : list) {
            Assert.assertNotNull(t.getAuthor());
            Assert.assertNotNull(t.getMessage());
            Assert.assertNotNull(t.getPublicationDate());
            Assert.assertNotNull(t.getProperties());
            System.out.println(t.getAuthor() + " - " + t.getMessage());
            System.out.println(t.getProperties());
        }
    }

    @Test
    public void testDirectMessage() throws TwitterException {
        TwitterServiceImpl svc = new TwitterServiceImpl(getChameleonProperties());
        svc.start();
        java.util.List<org.ow2.chameleon.twitter.Tweet> list = svc.getDirectMessages();
        Assert.assertNotNull(list);
        Assert.assertTrue(0 != list.size());
        for (org.ow2.chameleon.twitter.Tweet t : list) {
            Assert.assertNotNull(t.getAuthor());
            Assert.assertNotNull(t.getMessage());
            Assert.assertNotNull(t.getPublicationDate());
            Assert.assertNotNull(t.getProperties());
            System.out.println(t.getAuthor() + " - " + t.getMessage());
            System.out.println(t.getProperties());
        }
    }
}
