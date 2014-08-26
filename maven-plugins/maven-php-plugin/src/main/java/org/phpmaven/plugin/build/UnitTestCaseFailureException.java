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

package org.phpmaven.plugin.build;

/**
 * Result of a failed unit test case.
 *
 * @author Christian Wiedemann
 */
public class UnitTestCaseFailureException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private final int completeFailures;
    private final int completeErrors;

    public UnitTestCaseFailureException(int completeErrors,
                                        int completeFailures) {
        this.completeErrors = completeErrors;
        this.completeFailures = completeFailures;
    }

    @Override
    public String getMessage() {
        return "Unit Test fails with "
            + completeFailures + " failures and "
            + completeErrors + " errors.";
    }

}
