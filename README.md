# 📚 KataBooks - E-commerce Full-Stack Application

Ce dépôt contient le code source complet (Backend et Frontend) du projet KataBooks, une application de gestion d'inventaire et de vente de livres.

## 🏗️ Architecture du Projet (Monorepo)

Ce projet est structuré en deux parties distinctes et indépendantes :

- ☕ **`/backend`** : L'API RESTful développée avec **Spring Boot 3** (Java 21) et **PostgreSQL**. Conçue avec une approche "Package by Feature" et respectant les principes de la Clean Architecture.
# 📚 katabooks API

API REST de gestion d'inventaire et de vente de livres pour une librairie.

## Stack technique
- **Java 21**
- **Spring Boot 4..**
- **Gradle**
- **PostgreSQL 15** (via Docker)
- **GitHub Actions** (CI)

## Lancer le projet

### Prérequis
- Java 21
- Docker

### Démarrage

1. Lancer la base de données :
```bash
   docker-compose up -d
```

2. Démarrer l'application :
```bash
   ./gradlew bootRun
```

L'API est accessible sur `http://localhost:8080`

## Structure du projet
- `customer/` — Gestion des comptes clients
- `book/` — Catalogue de livres
- `order/` — Commandes (panier/historique achat)
- `payment/` — Paiement



- ⚛️ **`/frontend`** : L'interface utilisateur développée avec **React** (via Vite), permettant de consommer l'API de manière fluide.

Une pipeline **CI/CD via GitHub Actions** est configurée pour exécuter automatiquement les tests (unitaires et d'intégration) ainsi que la validation du build à chaque modification du code.