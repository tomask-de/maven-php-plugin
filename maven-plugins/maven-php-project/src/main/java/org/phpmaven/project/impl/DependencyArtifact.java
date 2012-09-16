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

package org.phpmaven.project.impl;

import java.io.File;
import java.io.Serializable;

/**
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.3
 */
public class DependencyArtifact implements Serializable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The dependency key
	 */
	private String depKey;

	/**
	 * The action statement
	 */
	private String actionStatement;
	
	/**
	 * The repository file
	 */
	private File reposFile;
	
	/**
	 * The file statement
	 */
	private long fileTimestamp;
	
	/**
	 * @return the depKey
	 */
	public String getDepKey() {
		return this.depKey;
	}

	/**
	 * @param depKey the depKey to set
	 */
	public void setDepKey(String depKey) {
		this.depKey = depKey;
	}

	/**
	 * @return the actionStatement
	 */
	public String getActionStatement() {
		return this.actionStatement;
	}

	/**
	 * @param actionStatement the actionStatement to set
	 */
	public void setActionStatement(String actionStatement) {
		this.actionStatement = actionStatement;
	}

	/**
	 * @return the reposFile
	 */
	public File getReposFile() {
		return this.reposFile;
	}

	/**
	 * @param reposFile the reposFile to set
	 */
	public void setReposFile(File reposFile) {
		this.reposFile = reposFile;
	}

	/**
	 * @return the fileTimestamp
	 */
	public long getFileTimestamp() {
		return this.fileTimestamp;
	}

	/**
	 * @param fileTimestamp the fileTimestamp to set
	 */
	public void setFileTimestamp(long fileTimestamp) {
		this.fileTimestamp = fileTimestamp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.actionStatement == null) ? 0 : this.actionStatement
						.hashCode());
		result = prime * result
				+ ((this.depKey == null) ? 0 : this.depKey.hashCode());
		result = prime * result
				+ (int) (this.fileTimestamp ^ (this.fileTimestamp >>> 32));
		result = prime * result
				+ ((this.reposFile == null) ? 0 : this.reposFile.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DependencyArtifact other = (DependencyArtifact) obj;
		if (this.actionStatement == null) {
			if (other.actionStatement != null)
				return false;
		} else if (!this.actionStatement.equals(other.actionStatement))
			return false;
		if (this.depKey == null) {
			if (other.depKey != null)
				return false;
		} else if (!this.depKey.equals(other.depKey))
			return false;
		if (this.fileTimestamp != other.fileTimestamp)
			return false;
		if (this.reposFile == null) {
			if (other.reposFile != null)
				return false;
		} else if (!this.reposFile.equals(other.reposFile))
			return false;
		return true;
	}
	
}
