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

import java.io.File;

import junit.framework.Assert;

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
import org.ops4j.pax.swissbox.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import org.ow2.chameleon.testing.helpers.OSGiHelper;
import org.ow2.chameleon.testing.tinybundles.ipojo.IPOJOBuilder;
import org.ow2.chameleon.twitter.example.TwitterExample;

@RunWith(JUnit4TestRunner.class)
public class TwitterExampleTest {

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
                            CoreOptions.mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-api").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.twitter").artifactId("twitter-service").versionAsInProject(),
                            CoreOptions.mavenBundle().groupId("org.ow2.chameleon.twitter").artifactId("twitter4j").versionAsInProject()),

                        CoreOptions.bootClasspathLibraries(new String[] {
                                "mvn:org.slf4j/slf4j-api/1.6.0",
                                "mvn:ch.qos.logback/logback-classic/0.9.21",
                                "mvn:ch.qos.logback/logback-core/0.9.21"
                           }
                        ),
                        CoreOptions.systemPackages("org.slf4j; version=1.6.0", "org.slf4j.impl; version=1.6.0"),

                        PaxRunnerOptions.repository("http://maven.ow2.org/maven2"), // Add the ow2 repository to download chameleon-commons

                        CoreOptions.provision(TinyBundles.newBundle().add(
                                TwitterExample.class).set(
                                Constants.IMPORT_PACKAGE, "*").set(
                                Constants.BUNDLE_SYMBOLICNAME,
                                "Twitter-Example").build(
                                IPOJOBuilder.withiPOJO(new File(
                                        "src/main/resources/metadata.xml")))));
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
    public void testActive() throws InterruptedException {
        Bundle[] bundles = context.getBundles();
        for (Bundle b : bundles) {
            Assert.assertTrue(b.getState() == Bundle.ACTIVE);
        }

        Thread.sleep(5000);

    }

}
