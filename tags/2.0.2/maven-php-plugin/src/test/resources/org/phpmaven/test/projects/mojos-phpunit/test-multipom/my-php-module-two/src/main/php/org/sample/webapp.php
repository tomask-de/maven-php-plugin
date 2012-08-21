<?php

require_once str_replace('.','/','org.sample').'/app.php';

class WebApp {
    var $app;

    function __construct() {
        $this->app  = new App();
    }

    function foo() {
        return $this->app->doSomething();
    }
}
?>