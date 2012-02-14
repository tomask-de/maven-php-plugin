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

$suiteFile = $_SERVER['argv'][1];
require_once 'PHPUnit/TextUI/TestRunner.php';
require_once 'PHPUnit/Util/Log/PMD.php';
require_once 'PHPUnit/Util/Log/TAP.php';
require_once 'PHPUnit/Util/Configuration.php';
require_once 'PHPUnit/Util/Fileloader.php';
require_once 'PHPUnit/Util/Filter.php';
require_once 'PHPUnit/Util/Getopt.php';
require_once 'PHPUnit/Util/Skeleton.php';
require_once 'PHPUnit/Util/TestDox/ResultPrinter/Text.php';
PHPUnit_Util_Filter::addFileToFilter(__FILE__, 'PHPUNIT');

// fix command line arguments; sets the class name
array_splice($_SERVER['argv'], 1, 1);

$_SERVER['argv'][] = 'MavenCoverageTestSuite';
$_SERVER['argv'][] = $suiteFile;

require_once 'PHPUnit/TextUI/Command.php';
