<?php

/**
 * The bar test class
 * 
 * @author mepeisen
 */
class BarTest extends PHPUnit_Framework_TestCase
{
	
	/**
	 * tests the bar function
	 */
	public function testBar()
	{
		include "MyClass.php";
		$this->assertEquals("bar", MyMavenTestClass::getBar());
	}
	
}