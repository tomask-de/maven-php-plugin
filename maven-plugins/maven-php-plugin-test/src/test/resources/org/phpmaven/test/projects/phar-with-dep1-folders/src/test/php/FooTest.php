<?php

/**
 * The foo test class
 * 
 * @author mepeisen
 */
class FooTest extends PHPUnit_Framework_TestCase
{
	
	/**
	 * tests the bar function
	 */
	public function testFoo()
	{
		include "folderA/MyClassA.php";
		$o = new folderA\MyMavenTestClassA();
		$this->assertEquals("foo", $o->getFoo());
		include "folderB/MyClassB.php";
		$o = new folderB\MyMavenTestClassB();
		$this->assertEquals("foo", $o->getFoo());
	}
	
}