<?php

require_once "bootstrap.php";

if (array_key_exists(1, $argv) && $argv[1] === 'add' && array_key_exists(2, $argv)) {
    // add new entry
    $newModelName = $argv[2];

    $model = new \MyApp\Entities\MyModel;
    $model->setName($newModelName);

    $entityManager->persist($model);
    $entityManager->flush();

echo "Created MyModel with id: " . $model->getId() . "\n";

} else {
    // dump all MyModel entries
    $query = $entityManager->createQuery('SELECT m FROM MyApp\Entities\MyModel m');
    $models = $query->getResult();

    print('id - name'."\n");

    foreach ($models as $model) {
        print($model->getId().' - '.$model->getName()."\n");
    }

    print("\n".'Use my-app.php add <some_name> to add entries.'."\n");

}