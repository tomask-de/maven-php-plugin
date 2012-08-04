<?php 
if (ini_get('max_execution_time') !== false) {
	echo "success: ".ini_get('max_execution_time');
}
else {
	echo "failure";
}
