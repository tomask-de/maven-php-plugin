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
		$this->assertEquals("foo", MyMavenTestClass::getFoo());
	}
	
}