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
package org.ow2.chameleon.twitter.test;

import java.util.Properties;

import junit.framework.Assert;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.container.def.PaxRunnerOptions;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import org.ow2.chameleon.testing.helpers.OSGiHelper;
import org.ow2.chameleon.twitter.TwitterService;

@RunWith(JUnit4TestRunner.class)
public class TwitterServiceTest {

    @Inject
    private BundleContext context;

    private OSGiHelper osgi;

    private IPOJOHelper ipojo;

    @Configuration
    public static Option[] configure() throws Exception {

        Option[] opt = CoreOptions
                .options(
                		PaxRunnerOptions.rawPaxRunnerOption("--definitionURL", "http://chameleon.ow2.org/configurations/platforms/felix/chameleon-0.2.4.xml"),

                        CoreOptions.provision(
                            CoreOptions.mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.configadmin").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.testing").artifactId("osgi-helpers").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.twitter").artifactId("twitter-service").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.twitter").artifactId("twitter4j").versionAsInProject()),

                            CoreOptions.bootClasspathLibraries(new String[] {
                                    "mvn:org.slf4j/slf4j-api/1.6.0",
                                    "mvn:ch.qos.logback/logback-classic/0.9.21",
                                    "mvn:ch.qos.logback/logback-core/0.9.21"
                                }
                            ),
                            CoreOptions.systemPackages("org.slf4j; version=1.6.0", "org.slf4j.impl; version=1.6.0"),

                            PaxRunnerOptions.repository("http://maven.ow2.org/maven2") // Add the ow2 repository to download chameleon-commons
                );
        return opt;
    }

	private Properties getChameleonConfiguration() {
		Properties props = new Properties();
		props.put("twitter.consumer-key-secret",
				"tdaMUrqfnN1xFvOLqgQJDAsoI2q6rZyL0digOYE");
		props.put("twitter.screenName", "ow2_chameleon_t");
		props.put("twitter.token-secret",
				"DIdKA2BXWwcwyUbgST0EXY2j3tji1IK9WuFHAT95g");
		props.put("twitter.token",
				"115698088-Wnq2vGDVRO8CJ4LJTOkTpsPl8cd2o5R3mPIiN5pd");
		props.put("twitter.consumer-key", "If0hIte3e7oZNIE2bksA");
		props.put("twitter.userId", "115698088");
		return props;
	}

    @Before
    public void setup() {
        osgi = new OSGiHelper(context);
        ipojo = new IPOJOHelper(context);
    }

    @After
    public void tearDown() {
        osgi.dispose();
        ipojo.dispose();
    }

    @Test
    public void testConnection() throws UnacceptableConfiguration,
            MissingHandlerException, ConfigurationException {
        // Get the factory
        Factory factory = ipojo
                .getFactory("org.ow2.chameleon.twitter.twitter4j");
        Assert.assertNotNull(factory);
        factory.createComponentInstance(getChameleonConfiguration());

        osgi.waitForService(TwitterService.class.getName(),
                "(org.ow2.chameleon.twitter.user=ow2_chameleon_t)", 5000);

        TwitterService service = (TwitterService) osgi.getServiceObject(
                TwitterService.class.getName(), null);
        Assert.assertNotNull(service);
    }

    @Test
    public void testUpdateStatus() throws Exception {
        // Get the factory
        Factory factory = ipojo
                .getFactory("org.ow2.chameleon.twitter.twitter4j");
        Assert.assertNotNull(factory);
        factory.createComponentInstance(getChameleonConfiguration());

        osgi.waitForService(TwitterService.class.getName(),
                "(org.ow2.chameleon.twitter.user=ow2_chameleon_t)", 5000);

        TwitterService service = (TwitterService) osgi.getServiceObject(
                TwitterService.class.getName(), null);
        Assert.assertNotNull(service);
        service.publishTweet("this message comes from chameleon ("
                + System.currentTimeMillis() + ")");
    }

    @Test
    public void testDirectMessages() throws Exception {
        // Get the factory
        Factory factory = ipojo
                .getFactory("org.ow2.chameleon.twitter.twitter4j");
        Assert.assertNotNull(factory);
        factory.createComponentInstance(getChameleonConfiguration());

        osgi.waitForService(TwitterService.class.getName(),
                "(org.ow2.chameleon.twitter.user=ow2_chameleon_t)", 5000);

        TwitterService service = (TwitterService) osgi.getServiceObject(
                TwitterService.class.getName(), null);
        Assert.assertNotNull(service);
        java.util.List<org.ow2.chameleon.twitter.Tweet> list = service
                .getDirectMessages();
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

        service.sendDirectMessage("clementplop",
                "chameleon tweets therefore chameleon is (" + System.currentTimeMillis() +")");

    }

    @Test
    public void testTimeLines() throws Exception {
        // Get the factory
        Factory factory = ipojo
                .getFactory("org.ow2.chameleon.twitter.twitter4j");
        Assert.assertNotNull(factory);
        factory.createComponentInstance(getChameleonConfiguration());

        osgi.waitForService(TwitterService.class.getName(),
                "(org.ow2.chameleon.twitter.user=ow2_chameleon_t)", 5000);

        TwitterService service = (TwitterService) osgi.getServiceObject(
                TwitterService.class.getName(), null);

        java.util.List<org.ow2.chameleon.twitter.Tweet> list = service
                .getPublicTimeLine();
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

        System.out.println("===================");

        list = service.getFollowedTimeLine();
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

        System.out.println("===================");

        list = service.getUserTimeLine("clementplop");
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

}
