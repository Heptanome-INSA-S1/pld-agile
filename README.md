# Agile

## Compilation
Cloner ou télécharger le projet : https://github.com/Heptanome-INSA-S1/pld-agile

Ouvrir un terminal à la racine du projet. Utiliser la commande ci-dessous pour télécharger ou mettre à jour gradle dans le projet.
```shell
$ chmod +x ./gradlew
$ ./gradlew
```

Pour lancer l'application :
```shell
$ ./gradlew clean build
$ java -jar ./build/libs/agile-1.0-SNAPSHOT.jar
```

Pour lancer les tests :
```
$ ./gradlew test
```