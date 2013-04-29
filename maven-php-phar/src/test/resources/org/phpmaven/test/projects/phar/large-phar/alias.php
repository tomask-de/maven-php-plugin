<?php

Phar::loadPhar(__DIR__.'/phar1.phar');
require_once 'phar://p.phar/includes1/file.php';
