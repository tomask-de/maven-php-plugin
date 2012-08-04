<?php
require_once str_replace('.','/','org.sample').'/app.php';

class AppTest extends PHPUnit_Framework_TestCase {
    function testDoSomehting() {
    	$app = new App();
        $this->assertEquals('something', $app->doSomething());
    }
}
?>
