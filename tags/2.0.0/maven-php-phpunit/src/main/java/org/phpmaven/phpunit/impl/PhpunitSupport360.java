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

package org.phpmaven.phpunit.impl;

import org.codehaus.plexus.component.annotations.Component;
import org.phpmaven.phpunit.IPhpunitSupport;

/**
 * Phpunit support for > 3.6.0.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpunitSupport.class, instantiationStrategy = "per-lookup", hint = "PHP_EXE_V3.6.0")
public class PhpunitSupport360 extends AbstractPhpunitExeSupport {
    
    private static final String TEMPLATE =
            "require_once 'PHPUnit/Autoload.php';\n" +
            "PHPUnit_TextUI_Command::main();";
    
    private static final String SUITE_TEMPLATE =
            "<?php\n" +
            "\n" +
            "class MavenTestSuite extends PHPUnit_Framework_TestSuite {\n" +
            "  public static function suite() {\n" +
            "    $result = new self();\n" +
            "    $testFiles = array(\n" +
            "$:{PHPUNIT_TEST_FILES}\n" +
            "    );\n" +
            "    $result->addTestFiles($testFiles, false);\n" +
            "    return $result;\n" +
            "  }\n" +
            "}\n";

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSuiteTemplate() {
        return SUITE_TEMPLATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLogXmlArgument() {
        return "--log-junit";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExtraArguments() {
        if (getTestRequest().getPhpunitXml() != null) {
            return "--configuration \"" + getTestRequest().getPhpunitXml() + "\"";
        } else {
            return "";
        }
    }

}
