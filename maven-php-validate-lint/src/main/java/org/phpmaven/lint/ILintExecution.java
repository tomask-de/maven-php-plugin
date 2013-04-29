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

package org.phpmaven.lint;

import java.io.File;

import org.phpmaven.phpexec.library.PhpException;

/**
 * A single lint execution (lint checked file)
 * 
 * @author mepeisen
 * @since 2.0.0
 */
public interface ILintExecution {

    /**
     * Returns the file that was checked.
     * @return checked file.
     */
    File getFile();

    /**
     * Returns the exception during check
     * @return exception exception during check
     */
    PhpException getException();
    
}
