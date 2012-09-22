/**
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

import java.io.File;

import org.phpmaven.lint.ILintExecution;
import org.phpmaven.phpexec.library.PhpException;

public class LintExecution implements ILintExecution {
    
    private File fileToCheck;
    
    private PhpException exception;

    public LintExecution(File file) {
        this.fileToCheck = file;
    }

    public File getFile() {
        return this.fileToCheck;
    }

    /**
     * Sets execution
     * @param e
     */
    public void setException(PhpException e) {
        this.exception = e;
    }
    
    /**
     * Returns the exception
     * @return exception
     */
    public PhpException getException() {
        return this.exception;
    }
    
}
