<?php 

ob_start();
var_dump($mavenDependencies);
file_put_contents($mavenDependencies['targetDir'].'/foo.txt', ob_get_contents());
ob_end_clean();