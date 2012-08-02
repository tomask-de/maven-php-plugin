<?php 

require_once "folderA/MyClassA.php";
require_once "folderB/MyClassB.php";

/**
 * A simple test class.
 * 
 * @author Martin Eisengardt
 */
class BarTestClass extends folderA\MyMavenTestClassA
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