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

package org.phpmaven.pear.library.test;

import java.io.File;
import java.util.Collections;

import org.codehaus.plexus.util.FileUtils;
import org.phpmaven.pear.library.IPearUtility;
import org.phpmaven.pear.library.impl.PearUtility;
import org.phpmaven.phpexec.cli.PhpExecutableConfiguration;

/**
 * test cases for adding a local channel.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class ChannelAddTest extends AbstractTestCase {

    /**
     * Tests if we can add a channel locally.
     *
     * @throws Exception thrown on errors
     */
    @SuppressWarnings("unchecked")
	public void testChannelAdd() throws Exception {
		final File testDir = new File("target/test").getAbsoluteFile();
		FileUtils.deleteDirectory(testDir);
		testDir.mkdirs();
        final IPearUtility util = new PearUtility();
        util.configure(testDir, new PhpExecutableConfiguration(), Collections.EMPTY_LIST);
        
        util.installPear(false);
        util.initChannels(false);
        util.channelAdd("pear-dummy.php-maven.org", null, "Dummy test channel");
    }

}