<?php

// Simple doctrine bootstrap
// @author Erik Dannenberg 

// load config from maven filtered properties file
$props = parse_ini_file("config/doctrine.ini");

$classLoaderBasePath = __DIR__;
if ($props['doctrine.classloader.base'] !== ".") {
    $classLoaderBasePath .= $props['doctrine.classloader.base'];
    if (!file_exists($classLoaderBasePath)) {
        print('Error: Could not find classloader base path: ' . $classLoaderBasePath . "\n");
        exit();
    }
}

if ($props['doctrine.directory'] !== 'pear') {
    set_include_path($props['doctrine.directory'] . PATH_SEPARATOR . get_include_path());
}

use Doctrine\ORM\Tools\Setup;
require_once "Doctrine/ORM/Tools/Setup.php";

if ($props['doctrine.directory'] === 'pear') {
    // use pear supplied doctrine
    Setup::registerAutoloadPEAR();
} else {
    // use maven artifacts
    Setup::registerAutoloadDirectory($props['doctrine.directory']);
}

// enable classloading for the app
$classLoader = new \Doctrine\Common\ClassLoader($props['doctrine.classloader.name'], $classLoaderBasePath);
$classLoader->register();

// create a simple "default" Doctrine ORM configuration for annotation mapping
$isDevMode = $props['dev.mode'];
$config = Setup::createAnnotationMetadataConfiguration(array(__DIR__.'/MyApp/Entities'), $isDevMode);
// or if you prefer xml/yaml annotations
//$config = Setup::createXMLMetadataConfiguration(array(__DIR__."/config/xml"), $isDevMode);
//$config = Setup::createYAMLMetadataConfiguration(array(__DIR__."/config/yaml"), $isDevMode);

// database configuration
switch ($props['db.driver']) {
    case "mysql":
        $conn = array(
            'driver' => 'pdo_mysql',
            'host' => $props['db.host'],
            'port' => $props['db.port'],
            'dbname' => $props['db.name'],
            'user' => $props['db.user'],
            'password' => $props['db.password']
            );
        break;
    default:
        $conn = array(
            'driver' => 'pdo_sqlite',
            'path' => $props['db.path']
            );
}

// obtaining the entity manager
$entityManager = \Doctrine\ORM\EntityManager::create($conn, $config);