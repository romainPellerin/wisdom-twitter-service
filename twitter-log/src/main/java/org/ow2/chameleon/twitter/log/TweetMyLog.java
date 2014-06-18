/*
 * Copyright 2009 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *ÊÊÊhttp://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ow2.chameleon.twitter.log;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.ow2.chameleon.twitter.TwitterService;

@Component
public class TweetMyLog implements LogListener {

    @Requires
    private LogReaderService m_reader;

    @Requires
    private TwitterService m_twitter;

    @Validate
    public void start() {
        m_reader.addLogListener(this);
    }

    @Invalidate
    public void stop() {
        if (m_reader != null) {
            m_reader.removeLogListener(this);
        }
    }

    public synchronized void logged(LogEntry entry) {
        String message = "[";
        message += entry.getBundle().getBundleId();
        message += "] " + entry.getMessage() + " (" + System.currentTimeMillis() + ")";

        try {
            System.out.println(">>>" + message);
            m_twitter.publishTweet(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
