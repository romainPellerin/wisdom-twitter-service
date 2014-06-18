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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Simple tool generating configuration for the twitter4j implementation
 * of the twitter service.
 * This tool manage the OAuth.
 * It gives to the user the authorization url and process the PIN code
 * to retrieve the token and tokenSecret.
 * The tool's output is both in the console and files: a metadata file
 * containing the iPOJO instance and a 'cfg' file.
 */
public class Registration {

	/**
	 * Entry Point.
	 * @param args not used.
	 * @throws Exception
	 */
    public static void main(String args[]) throws Exception {
        String consumerKey = System.getProperty("consumerKey");
        String consumerSecret = System.getProperty("consumerSecret");

        if (consumerKey == null && consumerSecret == null) {
            System.err.println("Using internal consumer data");
            consumerKey = "If0hIte3e7oZNIE2bksA";
            consumerSecret = "tdaMUrqfnN1xFvOLqgQJDAsoI2q6rZyL0digOYE";
        } else if (consumerKey == null) {
            usage();
            return;
        } else if (consumerSecret == null) {
            usage();
            return;
        }


        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("[consumer key]", "[consumer secret]");
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                } else {
                    te.printStackTrace();
                }
            }

        }

	    long id = twitter.verifyCredentials().getId();
	    String token = accessToken.getToken();
	    String tokenSecret = accessToken.getTokenSecret();
	    String screenName = accessToken.getScreenName();

	    System.out.println("id : " + id);
	    System.out.println("token : " + token);
	    System.out.println("token secret : " + tokenSecret);
	    System.out.println("screen name : " + screenName);

	    createXMLInstance(consumerKey, consumerSecret, token, tokenSecret, id, screenName);
	    createCFGInstance(consumerKey, consumerSecret, token, tokenSecret, id, screenName);

	    System.exit(0);
	}

	/**
	 * Creates the CFG file to create an iPOJO instance.
	 * @param consumerKey the consumer key
	 * @param consumerSecret the consumer secret
	 * @param token the token
	 * @param tokenSecret the token secret
	 * @param id the user id
	 * @param screenName the screen name
	 * @throws FileNotFoundException cannot happen
	 * @throws IOException if the CFG file cannot be created.
	 */
	private static void createCFGInstance(String consumerKey,
			String consumerSecret, String token, String tokenSecret, long id,
			String screenName) throws FileNotFoundException, IOException {
		File file = new File("org.ow2.chameleon.twitter.twitter4j-" + screenName + ".cfg");
		file.createNewFile();
		Properties props = new Properties();
		props.put("twitter.consumer-key", consumerKey);
		props.put("twitter.consumer-key-secret", consumerSecret);
		props.put("twitter.token", token);
		props.put("twitter.token-secret", tokenSecret);
		props.put("twitter.userId", "" + id);
		props.put("twitter.consumer-key", consumerKey);
		props.put("twitter.screenName", screenName);
		props.store(new FileOutputStream(file), "Twitter configuration for " + screenName);
		System.out.println(file.getName() + " created");
	}

	/**
	 * Creates the XML file to create an iPOJO instance.
	 * @param consumerKey the consumer key
	 * @param consumerSecret the consumer secret
	 * @param token the token
	 * @param tokenSecret the token secret
	 * @param id the user id
	 * @param screenName the screen name
	 * @throws IOException if the metadata file cannot be created.
	 */
	private static void createXMLInstance(String consumerKey,
			String consumerSecret, String token, String tokenSecret, long id,
			String screenName) throws IOException {
		File metadata = new File("metadata.xml");
		FileWriter writer = new FileWriter(metadata);
		writer.append("<ipojo>\n");
		writer.append("  <instance component=\"" + "org.ow2.chameleon.twitter.twitter4j" + "\">\n");
		writer.append("    <property name=\"twitter.consumer-key\" value=\"" + consumerKey + "\"/>\n");
		writer.append("    <property name=\"twitter.consumer-key-secret\" value=\"" + consumerSecret + "\"/>\n");
		writer.append("    <property name=\"twitter.token\" value=\"" + token + "\"/>\n");
		writer.append("    <property name=\"twitter.token-secret\" value=\"" + tokenSecret + "\"/>\n");
		writer.append("    <property name=\"twitter.userId\" value=\"" + id + "\"/>\n");
		writer.append("    <property name=\"twitter.screenName\" value=\"" + screenName + "\"/>\n");
		writer.append("  </instance>\n");
		writer.append("</ipojo>\n");
		writer.flush();
		writer.close();
		System.out.println(metadata.getName() + " created");
	}

	/**
	 * Prints the usage.
	 */
	private static void usage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("java -cp twitter4j-${VERSION}.jar " +
				"-DconsumerKey=xxx -DconsumerSecret=xxx " +
				"org.ow2.chameleon.twitter.impl.Registration");
		System.out.println(buffer);
	}

}
