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

package org.phpmaven.lint.impl;

import java.io.Serializable;

import org.phpmaven.phpexec.library.PhpException;

/**
 * The lint file state
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.3
 */
public class LintFileState implements Serializable {
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The file timestamp
	 */
	private long fileDate;
	
	/**
	 * The php exception if there is any
	 */
	private PhpException exception;

	/**
	 * @return the fileDate
	 */
	public long getFileDate() {
		return this.fileDate;
	}

	/**
	 * @param fileDate the fileDate to set
	 */
	public void setFileDate(long fileDate) {
		this.fileDate = fileDate;
	}

	/**
	 * @return the exception
	 */
	public PhpException getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(PhpException exception) {
		this.exception = exception;
	}

}
