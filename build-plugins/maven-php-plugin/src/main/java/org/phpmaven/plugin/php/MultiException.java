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

package org.phpmaven.plugin.php;

import java.util.List;


/**
 * Symbolizes multiple exceptions at once.
 *
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 */
public class MultiException extends PhpException {

    private static final long serialVersionUID = 1L;
    
    private final List<Exception> exceptions;

    public MultiException(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public String getMessage() {
        if (exceptions.size() == 1) {
            return exceptions.get(0).getMessage();
        }

        final StringBuilder message = new StringBuilder();
        for (Exception e : exceptions) {
            message.append(e.getMessage());
            message.append("\n\n");
        }

        return message.toString();
    }

}
