<?php

function fooAutoload($class) {
	require_once $class.'.php';
}

spl_autoload_register('fooAutoload');
