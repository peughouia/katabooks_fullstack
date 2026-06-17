# Documentation et explication de certains concepts du developpement logiciel

---

##  SOLID
* **S** - **Single Responsibility Principle :** consiste comme on peut le 
constater dans son sigle, à déléguer une seule responsabilité 
a une classe pour qu'elle soit facilement maintenable, modifiable 
et utilisable sans gêner d'autre classe par exemple dans le projet ou j'ai créé une class 
**OrderMapper** et **OrderRepository**, on a une classe qui traduit une autre qui fait la sauvegade
  * clé à retenir - **Responsabilité unique**


* **O** - **Open/Closed Principle :** est un principe de la programmation qui conciste 
de pouvoir contruire des classes reutilisable pour obtenir un nouveau comportement dans systeme 
sans modifier cette class en elle meme : c'est le cas avec l'usage des Interfaces.
  * clé à retenir - **Etendre sans modifier**


* **L** - **Liskov Substitution Principle :** le L de solid qui est le principe 
de Liskov est utilisé en programation pour amener à respecter le principe d'heritage et faire en sorte que toutes les classes enfants 
soient en mesure de faire tout ce que cette classe fait et plus encore.
  * clé à retenir - **Capacité à pouvoir remplacer**


* **I** - **Interface Segregation Principle :** Le "I" est un principe qui demande de créer des interfaces très 
spécifiques, regroupant uniquement des fonctions d'une même famille.L'objectif est de  
ne jamais forcer une classe qui l'implémente à s'encombrer de méthodes dont elle ne se servira jamais. 
Il est donc toujours préférable d'avoir plusieurs petites interfaces ciblées plutôt qu'une seule grande interface générale
  * clé à retenir - **Eviter les fonctions dont l'implementation serait inutile**


* **D** - **Dependency Inversion Principle :** recommande de ne pas créer de dépendances 
fortes entre les composants du système. Pour que ces composants soient facilement 
interchangeables sans avoir besoin de modifier le code, ils ne doivent pas dépendre 
directement les uns des autres, mais dépendre d'une abstraction ou prise commune 
permettant de remplacer une implémentation par une autre en toute sécurité.
  * clé à retenir - **Eviter les couplages fort**


---

##  Clean Code

* **Le Nommage :** La première étape est d'utiliser un nommage significatif pour les variables, 
les fonctions et les classes. L'objectif est que la simple lecture d'un nom permette 
de comprendre immédiatement a quoi il sert, sans avoir besoin de lire le code à l'intérieur 
ou de rajouter des commentaires. Le code doit certes être exécutable par la machine, 
mais il doit avant tout être lisible et compréhensible par des humains.


* **taille et le rôle des fonctions :** La deuxième étape cruciale du Clean Code concerne 
la taille et le rôle des fonctions. Une fonction doit être aussi petite que possible, 
idéalement quelques lignes. et surtout, une fonction ne doit accomplir qu'une seule 
et unique tâche, pour pouvoir respecter ce principe. si une logique metier est très grande, 
il faut la decouper en plusieurs taches


* **Les Commentaires :** Troisièmement, le Clean Code exige de réduire au maximum 
l'utilisation des commentaires. Le code doit être écrit de manière si claire, 
avec des noms de variables et des fonctions explicites, qu'il puisse être compris 
sans aucune explication supplémentaire pour cela, il faut bien respecter le premier 
principe te comprendre ce que l'on veut faire.


* **La gestion des erreurs :** la quatrieme rèle à respecter lorsqu'on pratique du clean code 
est de bien faire la gestion des érreurs ne pas faire le melange entre le code qui gère 
les érreurs avec le code de la logique metier, et il est impératif de générer 
des messages clairs, nets et explicites pour faciliter le débogage lorsqu'une erreur survient.


* **Le Nettoyage :** Cinquièmement, pour l'application du Clean Code, il faut toujours 
laisser le code dans un meilleur état que celui dans lequel on l'a trouvé. Ce qui veut dire, 
que lorsqu'un développeur intervient sur un fichier pour ajouter une fonctionnalité ou faire 
une mise à jour, et qu'il remarque un élément mal nommé ou mal indenté par exemple, il doit 
prendre le temps de le nettoyer et de l'améliorer immédiatement.


C'est cet ensemble qui pourrait permettre de reduire les dettes techniques et contribuer à une 
maintenabilité et amelioration continu d'un projet informatique.


---

##  Test Unitaire
* Concept : Les tests unitaires sont conçus pour vérifier la fiabilité et le bon fonctionnement 
d'une méthode de manière totalement isolée. 
* Comment : Pour y parvenir, le test ne se connecte jamais à une véritable base de données 
ou à un service externe. On utilise à la place des objets simulés et de fausses données 
pour imiter l'environnement de la fonction. 
* Pourquoi : L'objectif est double : s'assurer que la logique métier de la fonction 
renvoie le résultat attendu, et surtout créer un filet de sécurité pour l'avenir. 
Ainsi, si une modification ultérieure vient "casser" la fonction, le test échouera et 
alertera immédiatement le développeur afin de garantir la non régréssion.


---

##  Test D'integration
* Concept : les tests d'intégration ne se concentrent plus sur une méthode isolée, mais 
sur l'assemblage de plusieurs couches techniques (Contrôleur, Service, Repository) pour 
valider une fonctionnalité métier dans son ensemble. 
* Comment : Pour ce faire, on n'utilise plus des simulés (Mocks), on exécute de véritables 
requêtes et on interagit avec une vraie base de données, généralement une base dédiée et 
volatile pour les tests, comme une base en mémoire. 
* Pourquoi : L'objectif principal est de vérifier la bonne communication, l'intégration 
et la cohérence entre les différentes classes et couches architecturales du programme.

---

##  Qu'est-ce qu'un Bean sur Spring

Dans Spring, un "Bean" est tout simplement un objet Java dont la création et le cycle 
de vie sont entièrement gérés par le framework, et le developpeur n'a plus besoin de 
faire de l'instanciation lui meme. Cela illustre le principe d'"Inversion de Contrôle" 
c'est Spring qui prend le contrôle de l'instanciation des classes. Le framework crée 
ces objets au démarrage de l'application et les rend disponibles partout où cela est 
nécessaire. Lorsqu'une classe a besoin d'utiliser un Bean, Spring le lui fournit 
automatiquement via le mécanisme d'"Injection de Dépendances", évitant ainsi d'avoir 
à recréer l'objet à chaque appel.