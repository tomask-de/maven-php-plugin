<?php 

ob_start();
mkdir($mavenDependencies['targetDir']);
var_dump($mavenDependencies);
file_put_contents($mavenDependencies['targetDir'].'/foo.txt', ob_get_contents());
ob_end_clean();