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
		include __DIR__."/MyClass.php";
		$this->assertEquals("foo", MyMavenTestClass::getFoo());
	}
	
}