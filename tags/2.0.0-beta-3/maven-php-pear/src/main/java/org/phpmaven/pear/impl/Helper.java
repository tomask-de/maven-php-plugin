/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.pear.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * A helper class.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public final class Helper {
    
    /**
     * Hidden constructor.
     */
    private Helper() {
        // empty
    }
    
    /**
     * Returns the text file contents.
     * @param channelName Name of the channel.
     * @param sub Sub-URI (relative or absolute path of the resource)
     * @return the files content.
     * @throws IOException thrown on errors.
     */
    public static String getTextFileContents(String channelName, String sub) throws IOException {
        // is it inside the local filesystem?
        if (channelName.startsWith("file://")) {
            return getTextFileContents("file://" + new File(channelName.substring(7), sub).getAbsolutePath());
        }
        return getTextFileContents(channelName.startsWith("http://") ?
                (channelName + "/" + sub) :
                ("http://" + channelName + "/" + sub));
    }
    
    /**
     * Returns the text file contents.
     * @param uri URI of the resource.
     * @return the files content.
     * @throws IOException thrown on errors.
     */
    public static String getTextFileContents(String uri) throws IOException {
        // is it inside the local filesystem?
        if (uri.startsWith("file://")) {
            final File channelFile = new File(uri.substring(7));

            final String lineSep = System.getProperty("line.separator");
            final BufferedReader br = new BufferedReader(new FileReader(channelFile));
            String nextLine = "";
            final StringBuffer sb = new StringBuffer();
            while ((nextLine = br.readLine()) != null) {
                sb.append(nextLine);
                sb.append(lineSep);
            }
            return sb.toString();
        }
        
        // try http connection
        final HttpClient client = new DefaultHttpClient();
        final HttpGet httpget = new HttpGet(uri);
        final HttpResponse response = client.execute(httpget);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Invalid http status: " +
                    response.getStatusLine().getStatusCode() +
                    " / " +
                    response.getStatusLine().getReasonPhrase());
        }
        final HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new IOException("Empty response.");
        }
        return EntityUtils.toString(entity);
    }

}
