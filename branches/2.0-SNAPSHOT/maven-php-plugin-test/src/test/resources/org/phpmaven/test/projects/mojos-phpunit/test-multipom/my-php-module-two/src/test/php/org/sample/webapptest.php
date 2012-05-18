<?php

require_once str_replace('.','/','org.sample').'/webapp.php';

class WebAppTest extends PHPUnit_Framework_TestCase {

    function testDoSomehting() {
    	$webapp = new WebApp();
        $this->assertEquals('something', $webapp->foo());
    }
}
?>
