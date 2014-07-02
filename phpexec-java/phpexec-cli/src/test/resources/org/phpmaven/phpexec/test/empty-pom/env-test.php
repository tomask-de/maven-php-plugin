<?php 
if (getenv('JUNIT_ENV_TEST') !== false) {
	echo "success: ".getenv('JUNIT_ENV_TEST');
}
else {
	echo "failure";
}
