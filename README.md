# Power Grid Tycoon 🔌⚡

## Simulation de Gestion de Réseau Électrique

**Projet de Fin d'Année - Master S3**

---

## 👥 Équipe de Développement

| Membre | Rôle |
|--------|------|
| **AKAKPO Kodjovi Denis-Papin** | Développeur |
| **Donko Akowe Raphael** | Développeur |

---

## 📖 Notre Approche et Répartition des Tâches

### Comment nous avons abordé ce projet

Lorsque nous avons reçu ce projet, nous avons d'abord pris le temps de bien comprendre ce qu'on attendait de nous : créer un jeu de simulation de gestion de réseau électrique. Nous nous sommes inspirés de jeux comme SimCity pour l'aspect gestion de ville, mais aussi de Universal Paperclips pour son interface minimaliste et son gameplay addictif.

Avant de coder quoi que ce soit, nous nous sommes assis ensemble pour discuter de l'architecture. Nous avons fait le choix d'une architecture MVC (Modèle-Vue-Contrôleur) enrichie d'une couche Service, car c'est un pattern que nous avions étudié en cours et qui nous semblait parfaitement adapté à ce type d'application. Cette décision s'est avérée judicieuse : elle nous a permis de travailler sur des parties différentes du code sans nous marcher sur les pieds.

### Comment nous avons réparti le travail

Plutôt que de diviser le projet de manière arbitraire, nous avons joué sur nos forces respectives et travaillé de manière complémentaire.

**Raphael** a pris en charge les fondations du projet : l'architecture globale et le cœur du système. C'est lui qui a :
- Conçu l'architecture MVC (Modèle-Vue-Contrôleur) avec couche Service
- Créé les entités de données (PowerPlant, Residence, Building, City)
- Défini les interfaces de services pour structurer la logique métier
- Implémenté les services : calcul de production, gestion de l'énergie, finances
- Mis en place le système de persistance pour sauvegarder/charger les parties
- Développé le pattern Observer pour la communication entre couches
- Géré les énumérations et les constantes du jeu

**Denis-Papin** a travaillé sur l'intégration et l'expérience utilisateur. C'est lui qui a :
- Créé l'interface graphique Swing (GridPanel, StatusPanel, ControlPanel, InfoPanel, BottomStatsPanel)
- Développé la vue console/terminal pour avoir deux modes de jeu
- Adapté et connecté les vues aux services pour que tout fonctionne ensemble
- Implémenté le système d'événements aléatoires (tempêtes, incendies, canicules...)
- Travaillé sur l'équilibrage du gameplay (seuils, mécaniques de bonheur, conditions de game over)
- Modernisé l'interface (numérotation des axes, animation de l'horloge, thème visuel)
- Créé les scripts de lancement (run.bat, run.sh)

En résumé, Raphael a fourni le moteur, et Denis-Papin a construit la carrosserie et fait rouler la voiture. Les deux parties étaient indispensables : sans le modèle et les services, il n'y aurait rien à afficher ; sans l'interface et l'intégration, le jeu ne serait pas jouable.

Cette répartition nous a permis de travailler en parallèle efficacement. L'intégration s'est faite naturellement grâce aux interfaces bien définies par Raphael, que Denis-Papin a pu implémenter côté vue.

### Les défis que nous avons rencontrés

Le plus gros défi a été l'équilibrage du jeu. Au début, le bonheur des habitants chutait beaucoup trop vite, rendant le jeu frustrant. Nous avons dû ajuster les formules plusieurs fois, passer le seuil de "game over" de 20% à 5%, et modifier la façon dont la demande énergétique est calculée. C'est un processus itératif : on teste, on ajuste, on reteste.

Un autre défi technique a été la gestion des caractères Unicode dans Swing sous Windows. Nous voulions utiliser des emojis pour rendre l'interface plus vivante, mais l'encodage Windows-1252 ne les supporte pas. Nous avons dû nous rabattre sur des symboles ASCII, ce qui nous a appris une leçon importante sur la compatibilité multiplateforme.

---

## 🎮 Présentation du Jeu

### De quoi s'agit-il ?

Power Grid Tycoon est un jeu de simulation où vous gérez le réseau électrique d'une ville. Votre objectif est simple en apparence : construire des centrales pour alimenter les maisons de vos habitants. Mais comme dans la vraie vie, tout est question d'équilibre.

Construisez trop peu ? Vos habitants manqueront d'électricité et seront mécontents. Construisez trop de centrales polluantes ? La pollution fera fuir les gens. Fixez des prix trop élevés ? Même chose. Et attention à votre budget : chaque centrale a des coûts d'opération !

### Comment jouer ?

Le jeu se déroule heure par heure. À chaque heure :
- Vos centrales produisent de l'électricité
- Vos résidences en consomment (plus le soir, moins la nuit)
- Vous gagnez de l'argent sur l'énergie vendue
- Vous payez les coûts d'opération
- Des événements peuvent survenir (tempête qui endommage une éolienne, canicule qui réduit le rendement...)

Vous contrôlez le rythme avec le bouton "Heure Suivante". Entre chaque heure, vous pouvez construire de nouvelles installations, consulter vos statistiques, et planifier votre stratégie.

### 🔋 Les Types de Centrales

| Type | Icône | Puissance | Coût | Pollution | Particularité |
|------|-------|-----------|------|-----------|---------------|
| Solaire | ☀️ | Faible | 800 | Aucune | Fonctionne mieux le jour |
| Éolienne | 💨 | Variable | 1000 | Aucune | Dépend du vent |
| Charbon | ⚫ | Élevée | 1500 | Très élevée | Production stable |
| Gaz | 🔥 | Moyenne | 1200 | Modérée | Bon compromis |
| Hydraulique | 💧 | Élevée | 2000 | Aucune | Stable mais chère |
| Nucléaire | ⚛️ | Très élevée | 5000 | Faible | Meilleur rendement |

### 😊 Calcul du Bonheur

| Facteur | Impact | Description |
|---------|--------|-------------|
| ⚡ Énergie | Principal | Ratio production/demande |
| 🏭 Pollution | Négatif | -1 point par 1000 PP |
| 💰 Prix | Négatif | Pénalité si prix > 15 |

**Formule simplifiée :**
```
Bonheur = Score_Energie - Pollution/1000 - max(0, (Prix-15) × 2)
```

### 💀 Conditions de Fin de Partie

| Condition | Seuil | Conséquence |
|-----------|-------|-------------|
| 😞 Bonheur trop bas | < 5% | Émeutes ! GAME OVER |
| 💸 Faillite | < -10 pièces | Banqueroute ! GAME OVER |

### 🌪️ Événements Aléatoires

| Événement | Icône | Effet |
|-----------|-------|-------|
| Tempête | 🌧️ | Dégâts aux éoliennes |
| Canicule | 🌡️ | Baisse rendement thermique |
| Incendie | 🔥 | Dégâts aux centrales |
| Tremblement de terre | 🌍 | Dégâts à toutes les structures |
| Tornade | 🌪️ | Destruction possible |
| Orage | ⛈️ | Surcharge électrique |

---

## 🏗️ Architecture Technique

### Vue d'ensemble

```
┌─────────────────────────────────────┐
│         🖥️ Vue (Swing/Console)      │  ← Ce que le joueur voit
├─────────────────────────────────────┤
│           🎮 Contrôleur             │  ← Gère les actions
├─────────────────────────────────────┤
│            ⚙️ Services              │  ← Logique métier
├─────────────────────────────────────┤
│            📦 Modèle                │  ← Données du jeu
└─────────────────────────────────────┘
```

### 📁 Organisation des Fichiers

```
src/
├── Main.java                 # Point d'entrée
├── controller/
│   └── GameController.java   # Orchestre le jeu
├── model/
│   ├── entity/               # Les "choses" du jeu
│   │   ├── Building.java     # Classe mère
│   │   ├── PowerPlant.java   # Centrales
│   │   ├── Residence.java    # Maisons
│   │   └── City.java         # La ville
│   ├── enums/                # Types et états
│   └── GameModel.java        # État global
├── service/
│   └── impl/                 # Toute la logique
├── view/
│   ├── console/              # Mode texte
│   └── swing/                # Mode graphique
└── observer/                 # Pattern Observer
```

### 🎯 Décisions de Conception

| Décision | Pourquoi ? | Bénéfice |
|----------|------------|----------|
| **MVC + Services** | Séparation des responsabilités | Code maintenable et testable |
| **Pattern Observer** | Notification automatique | UI réactive sans couplage fort |
| **Entités simples** | Données pures, logique dans services | Respect du SRP |
| **Swing** | Compatible partout, pas de dépendances | Simplicité de déploiement |
| **Sérialisation Java** | Sauvegarde native | Pas de bibliothèque externe |

### 📊 Historique des Versions

| Version | Description | Contributeur principal |
|---------|-------------|------------------------|
| 1.0 | Architecture de base, entités PowerPlant | Raphael |
| 1.1 | Ajout Residences et système de demande | Raphael |
| 1.2 | Implémentation City et simulation | Raphael |
| 1.3 | Contrôleur et persistance | Raphael |
| 1.4 | Interface Swing et événements aléatoires | Denis-Papin |
| 1.5 | Polissage UI, traduction française | Denis-Papin |

---

## 🚀 Comment Lancer le Jeu

### Prérequis
- Java JDK 11 ou plus récent

### 🪟 Windows
```bash
.\run.bat
```

### 🐧 Linux / 🍎 macOS
```bash
chmod +x run.sh
./run.sh
```

### Modes disponibles
1. **Mode Console** : Interface textuelle simple
2. **Mode Graphique** : Interface Swing complète

---

## 📚 Ce que ce Projet nous a Appris

Au-delà des aspects techniques (Java, Swing, patterns de conception), ce projet nous a appris à :

- **Travailler en équipe** : Se coordonner, communiquer, fusionner notre travail avec Git
- **Faire des compromis** : On ne peut pas tout faire, il faut prioriser
- **Itérer** : La première version n'est jamais la bonne, il faut tester et améliorer
- **Documenter** : Un code sans documentation est un code qu'on oubliera

Nous sommes fiers du résultat. Ce n'est peut-être pas le jeu le plus beau du monde (Swing a ses limites !), mais il fonctionne, il est amusant, et surtout, nous l'avons construit ensemble, de A à Z.

---

## 📝 Récapitulatif de la Répartition

| Tâche | Denis-Papin | Raphael |
|-------|:-----------:|:-------:|
| Architecture MVC globale | | ✅ |
| Entités (PowerPlant, Residence, City) | | ✅ |
| Interfaces de services | | ✅ |
| Implémentation services (logique métier) | | ✅ |
| Persistance (sauvegarde/chargement) | | ✅ |
| Pattern Observer | | ✅ |
| Interface Swing (tous les panels) | ✅ | |
| Interface Console/Terminal | ✅ | |
| Intégration Vue ↔ Services | ✅ | |
| Événements aléatoires | ✅ | |
| Équilibrage gameplay | ✅ | |
| Animation horloge et UI dynamique | ✅ | |
| Traduction française | ✅ | |
| Scripts de lancement | ✅ | |
| Tests et débogage | ✅ | ✅ |

---

**© 2026 - AKAKPO Kodjovi Denis-Papin & Donko Akowe Raphael**

*Projet réalisé dans le cadre du Master S3 - Programmation Orientée Objet Avancée*