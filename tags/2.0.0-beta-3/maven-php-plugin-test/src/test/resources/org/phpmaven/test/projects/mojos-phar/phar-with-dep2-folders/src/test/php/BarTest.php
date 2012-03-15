<?php

/**
 * The foo test class
 * 
 * @author mepeisen
 */
class BarTest extends PHPUnit_Framework_TestCase
{
	
	/**
	 * tests the bar function
	 */
	public function testFoo()
	{
		include "BarClass.php";
		$o = new BarTestClass();
		$this->assertEquals("bar", $o->getFoo());
	}
	
}