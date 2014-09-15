/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * This file is part of pear-java.
 *
 * pear-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pear-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pear-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.pear.library.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
 * @since 0.1.0
 */
final class Helper {
    
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
            br.close();
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
    
    /**
     * Returns the binary file contents.
     * @param uri URI of the resource.
     * @return the files content.
     * @throws IOException thrown on errors.
     */
    public static byte[] getBinaryFileContents(String uri) throws IOException {
        // is it inside the local filesystem?
        if (uri.startsWith("file://")) {
            final File channelFile = new File(uri.substring(7));

            final byte[] result = new byte[(int) channelFile.length()];
            final FileInputStream fis = new FileInputStream(channelFile);
            fis.read(result);
            fis.close();
            return result;
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
        return EntityUtils.toByteArray(entity);
    }

}
