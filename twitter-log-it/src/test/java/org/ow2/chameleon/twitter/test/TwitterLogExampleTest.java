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
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import org.ow2.chameleon.testing.helpers.OSGiHelper;
import org.ow2.chameleon.twitter.TwitterService;

@RunWith(JUnit4TestRunner.class)
public class TwitterLogExampleTest {

    @Inject
    private BundleContext context;

    private OSGiHelper osgi;

    private IPOJOHelper ipojo;

    @Configuration
    public static Option[] configure() throws Exception {

        Option[] opt = CoreOptions
                .options(
                        CoreOptions.felix(),

                        CoreOptions.provision(
                            CoreOptions.mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.configadmin").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.testing").artifactId("osgi-helpers").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-api").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-service").version("1.4"),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.twitter").artifactId("twitter-service").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.twitter").artifactId("twitter4j").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.twitter").artifactId("twitter-log").versionAsInProject()
                        ),

                        PaxRunnerOptions
                                .repository("https://repository.apache.org/content/groups/snapshots-group/"));
        return opt;
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
    public void testLog() throws InterruptedException,
            UnacceptableConfiguration, MissingHandlerException,
            ConfigurationException {
        LogService log = (LogService) osgi.getServiceObject(LogService.class
                .getName(), null);
        Factory factory = ipojo
                .getFactory("org.ow2.chameleon.twitter.twitter4j");
        Assert.assertNotNull(factory);
        Properties props = new Properties();
        props.put("twitter.user", "ow2_chameleon_t");
        props.put("twitter.password", "chameleon");
        factory.createComponentInstance(props);

        osgi.waitForService(TwitterService.class.getName(),
                "(org.ow2.chameleon.twitter.user=ow2_chameleon_t)", 5000);
        osgi.waitForService(LogReaderService.class.getName(), null, 5000);

        log.log(LogService.LOG_INFO, "Tweet your log ! "
                + System.currentTimeMillis());

    }

}
