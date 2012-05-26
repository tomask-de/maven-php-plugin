<?php
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

require_once 'PHPUnit/Framework/TestSuite.php';

// TODO
// if (PHPUnit_Util_Filesystem::fileExistsInIncludePath('PHP/CodeCoverage/Filter.php')) {
// 	PHP_CodeCoverage_Filter::getInstance()->addFileToBlacklist(__FILE__, 'PHPUNIT');
// 	PHP_CodeCoverage_Filter::getInstance()->addDirectoryToWhitelist('$:{PHPUNIT_SRC_DIR}', 'PHPUNIT_PHP_FILE_SUFFIX');
// }
// else {
// 	PHPUnit_Util_Filter::addFileToFilter(__FILE__, 'PHPUNIT');
// 	PHPUnit_Util_Filter::addDirectoryToWhitelist('$:{PHPUNIT_SRC_DIR}', 'PHPUNIT_PHP_FILE_SUFFIX');
// }

class MavenCoverageTestSuite extends PHPUnit_Framework_TestSuite {
	public static function suite() {
		$result = new self();
		$testFiles = array(
			$:{PHPUNIT_COVERAGE_TEST_FILES}
		);
		$result->addTestFiles($testFiles, false);
		return $result;
	}
}
