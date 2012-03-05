<?php 

require_once "MyClass.php";

/**
 * A simple test class.
 * 
 * @author Martin Eisengardt
 */
class BarTestClass extends MyMavenTestClass
{
	
	/**
	 * Get the string "bar".
	 * 
	 * @return string A string "bar"
	 */
	public function getFoo()
	{
		return "bar";
	}
	
}