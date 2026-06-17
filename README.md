# Katabooks — API Rest de Gestion de Librairie en Ligne

Katabooks est une application backend robuste de commerce électronique spécialisée dans la gestion d'une librairie. Conçue avec une approche orientée **Clean Code**, l'application gère l'intégralité du tunnel d'achat, depuis l'authentification du client jusqu'à la sécurisation du paiement et le suivi de l'inventaire par un administrateur.
Ce projet a été développé dans le respect des exigences de sécurité, de performance et de testabilité attendues (**Test unitair et Test d'interation**) sur des applications critiques.

---

##  Fonctionnalités Principales

### 👤 Gestion des Utilisateurs & Sécurité
* **Inscription & Connexion** : Chiffrement des mots de passe et authentification par jeton sécurisé (Token-based authentication).
* **Sécurité contextuelle** : Injection automatique du client authentifié dans le contexte de la requête (`@RequestAttribute`) éliminant les failles de piratage d'identifiant.
* **Contrôle d'accès (RBAC)** : Restriction stricte des routes d'inventaire aux rôles `CLIENT` et `GESTIONNAIRE`.

### 🛒 Cycle de Vie des Commandes (Panier & Historique)
* **Panier Dynamique** : Gestion d'une commande unique au statut `CART` par client. Ajout, modification des quantités avec vérification stricte des stocks disponibles, et suppression d'articles.
* **Paiement** : Processus de paiement transactionnel (`@Transactional`) simulant la validation d'une passerelle bancaire. En cas de succès, le panier est figé et bascule au statut `PAID/CONFIRMER`.
* **Historique d'Achat** : Consultation sécurisée des commandes passées, triées automatiquement de la plus récente à la plus ancienne.

### 📊 Module Inventaire (Gestionnaire)
* **Suivi des Stocks** : Vision globale des livres disponibles en rayon.
* **Indicateurs de Vente** : Calcul dynamique et performant en mémoire (via l'API Stream Java) des volumes de livres vendus à partir des commandes validées.

---

## 🛠️ Technologies Utilisées

* **Backend** : Java 21 / Spring Boot 4.1.0
* **Moteur de Persistance** : Spring Data JPA / Hibernate
* **Bases de Données** : Base de données relationnelle **PostgreSQL 15** via Docker (et Support H2 pour l'environnement de test)
* **Gestionnaire de Dépendances** : Gradle
* **Mapping & Data Transfer** : DTO Pattern & Mappers personnalisés pour l'isolation des couches
* **Validation** : Spring Boot Starter Validation (`@Valid`, `@NotNull`, `@Email`)
* **Tests** : JUnit 5, Mockito, MockMvc (Architecture de tests d'intégration et unitaires)
* **GitHub Actions** (CI)

---

## 📐 Choix d'Architecture & Robustesse

* **Isolation des Couches (DTO Pattern)** : Les entités de la base de données ne sont jamais exposées à l'extérieur. L'utilisation de Mappers dédiés (`OrderMapper`, etc.) garantit l'étanchéité de l'API et protège les données sensibles.
* **Architecture en couche** : **Repository** couche d'acces a la BD, **Service** couche pour la logique metier et **Controller* couche de presentation ou vues
* **Gestion Globale des Erreurs** : Centralisation des exceptions via un `@RestControllerAdvice` pour renvoyer des réponses HTTP claires et standardisées (`400 Bad Request`, `401 Unauthorized`, `403 Forbidden`) au format JSON.
* **Fiabilité des Tests** : Stratégie de nettoyage systématique de la base de données (`@BeforeEach` ordonné) garantissant l'indépendance totale des tests unitaires et d'intégration sans altérer les performances de la suite de tests.

---

## 🛣️ Aperçu des Points d'Accès de l'API (Endpoints)

### Clients (`/customers`)
* `POST /customers/register` : Créer un nouveau compte client.
* `POST /customers/login` : S'authentifier et récupérer le token.

### Panier & Commandes (`/orders`)
* `GET /orders/cart` : Consulter le panier actif.
* `POST /orders/cart/items` : Ajouter un livre au panier.
* `PUT /orders/cart/items/{orderItemId}` : Modifier la quantité d'une ligne du panier.
* `DELETE /orders/cart/items/{id}` : Supprimer un article du panier.
* `GET /orders/history` : Consulter l'historique des commandes payées du client.
* `POST /order/checkout`: simuler le payement d'un panier

### Administration (`/admin`)
* `GET /books/inventory` : Consulter l'inventaire (Réservé Gestionnaire).

---

## Lancer le projet

### Prérequis
- Java 21 (et ses packages : **Spring web**, 
**Spring Data JPA**,**PostgreSQL Driver**, **Lombok**) 
- Docker (docke compose)

### Démarrage

1. Lancer la base de données :
```bash
   docker-compose up -d
```

2. Compilé l'application :
```bash
   ./gradlew build
```
3. Lancer l'application :
```bash
   ./gradlew bootRun
```

L'API est accessible sur `http://localhost:8080`