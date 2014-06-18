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
package org.ow2.chameleon.twitter;

import java.util.Date;
import java.util.Map;

/**
 * Tweet structure wrapping messages.
 * @author <a href="mailto:chameleon-dev@ow2.org">Chameleon Project Team</a>
 * @version 1.0.0
 */
public interface Tweet {

    /**
     * Gets the message author.
     * @return the author
     */
    public String getAuthor();

    /**
     * Gets the message (content).
     * @return the content of the message
     */
    public String getMessage();

    /**
     * Gets the publication date.
     * @return the publication date
     */
    public Date getPublicationDate();

    /**
     * Gets custom properties such as the geolocalisation...
     * @return the custom properties attached to the message.
     */
    public Map<String, Object> getProperties();
}
