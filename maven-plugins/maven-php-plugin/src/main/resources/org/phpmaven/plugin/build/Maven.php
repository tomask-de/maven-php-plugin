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

require_once 'PHPUnit/Util/Filesystem.php';
if (PHPUnit_Util_Filesystem::fileExistsInIncludePath('PHPUnit/Autoload.php')) {
	require_once 'PHPUnit/Autoload.php';
}
else {
	require_once 'PHPUnit/TextUI/TestRunner.php';
	require_once 'PHPUnit/Util/Log/PMD.php';
	require_once 'PHPUnit/Util/Log/TAP.php';
	require_once 'PHPUnit/Util/Configuration.php';
	require_once 'PHPUnit/Util/Fileloader.php';
	require_once 'PHPUnit/Util/Filter.php';
	require_once 'PHPUnit/Util/Getopt.php';
	require_once 'PHPUnit/Util/Skeleton.php';
	require_once 'PHPUnit/Util/TestDox/ResultPrinter/Text.php';
	// this indicates a version < 3.6.0
	// --log-xml is replaced by --log-junit by phpunit version 3.6.0
	for ($i = 0; $i < count($_SERVER['argv']); $i++) {
		if ($_SERVER['argv'][$i] == '--log-junit') {
			$_SERVER['argv'][$i] = '--log-xml';
		}
	}
}

// TODO
// if (PHPUnit_Util_Filesystem::fileExistsInIncludePath('PHP/CodeCoverage/Filter.php')) {
// 	PHP_CodeCoverage_Filter::getInstance()->addFileToBlacklist(__FILE__, 'PHPUNIT');
// }
// else {
// 	PHPUnit_Util_Filter::addFileToFilter(__FILE__, 'PHPUNIT');
// }

require_once 'PHPUnit/TextUI/Command.php';

// newer phpunit versions (>3.3.10) do not invoke it directly
if (!defined('PHPUnit_MAIN_METHOD')) {
	PHPUnit_TextUI_Command::main();
}
