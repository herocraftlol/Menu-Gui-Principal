# 🎮 MainMenu Plugin — Guide d'installation

![Version](https://img.shields.io/badge/version-1.1.0-blue)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20+-green)
![Platform](https://img.shields.io/badge/Platform-BungeeCord%20%2B%20Spigot-orange)

Plugin Minecraft **BungeeCord + Spigot** qui affiche un **menu GUI configurable** via `/menu`.

---

## ⬇️ Téléchargement

📦 **Dernière version: v1.1.0**

| Plugin | Fichier | Description |
|--------|---------|-------------|
| **BungeeCord** | [`MainMenuBungee-1.1.0.jar`](https://github.com/herocraftlol/Menu-Gui-Principal/releases/download/v1.1.0/MainMenuBungee-1.1.0.jar) | Plugin à installer sur votre proxy BungeeCord |
| **Spigot** | [`MainMenuSpigot-1.1.0.jar`](https://github.com/herocraftlol/Menu-Gui-Principal/releases/download/v1.1.0/MainMenuSpigot-1.1.0.jar) | Plugin à installer sur chaque serveur Spigot |

➡️ **[Toutes les releases](https://github.com/herocraftlol/Menu-Gui-Principal/releases)**

---

## 📁 Structure du projet

```
MainMenu/
├── bungee/         → Plugin à installer sur BungeeCord
│   └── src/main/java/fr/mainmenu/bungee/
│       ├── MainMenuBungee.java
│       ├── listeners/PlayerCountListener.java
│       └── messaging/PluginMessageHandler.java
│
└── spigot/         → Plugin à installer sur chaque serveur Spigot
    └── src/main/java/fr/mainmenu/spigot/
        ├── MainMenuSpigot.java
        ├── commands/MenuCommand.java
        ├── commands/HubZoneCommand.java
        ├── gui/MenuGui.java
        ├── listeners/GuiClickListener.java
        ├── listeners/PlayerJoinListener.java
        ├── listeners/ZoneListener.java
        ├── listeners/HotbarInteractListener.java
        ├── managers/ConfigManager.java
        ├── managers/PlayerCountManager.java
        ├── managers/ZoneManager.java
        ├── managers/HotbarManager.java
        ├── messaging/PluginMessageReceiver.java
        └── utils/ItemBuilder.java, ActionHandler.java
```

---

## ⚙️ Compilation (Maven)

### Dépendances requises (pom.xml)

**Plugin BungeeCord :**
```xml
<dependency>
    <groupId>net.md-5</groupId>
    <artifactId>bungeecord-api</artifactId>
    <version>1.20-R0.1</version>
    <scope>provided</scope>
</dependency>
```

**Plugin Spigot :**
```xml
<dependency>
    <groupId>org.spigotmc</groupId>
    <artifactId>spigot-api</artifactId>
    <version>1.20.1-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

Compilez avec : `mvn clean package`

---

## 🚀 Installation

### 1. Plugin BungeeCord
- Copiez `MainMenuBungee.jar` dans le dossier `plugins/` de votre proxy BungeeCord.
- Redémarrez BungeeCord.

### 2. Plugin Spigot
- Copiez `MainMenuSpigot.jar` dans le dossier `plugins/` de **chaque serveur Spigot**.
- Redémarrez chaque serveur Spigot.

### 3. Configuration BungeeCord
Assurez-vous que vos serveurs sont bien déclarés dans `BungeeCord/config.yml` :
```yaml
servers:
  lobby:
    motd: '&1Lobby'
    address: localhost:25566
    restricted: false
  survival:
    motd: '&1Survie'
    address: localhost:25567
    restricted: false
  # etc...
```

### 4. Configurer le plugin Spigot
Éditez `plugins/MainMenuSpigot/config.yml` :

```yaml
bungee:
  hub-server: "lobby"  # ← Nom de votre serveur hub dans BungeeCord

menu:
  title: "&8✦ &bMenu Principal &8✦"
  size: 54
```

---

## 🎛️ Configuration du menu

### Ajouter un item personnalisé

```yaml
menu:
  items:
    mon_item:
      slot: 20           # Position (0 à 53 pour un menu 54 slots)
      material: DIAMOND  # Matériau Bukkit
      name: "&bMon Item" # Nom (codes couleur &)
      glowing: true      # Effet brillant
      lore:
        - ""
        - "&7Description de l'item"
        - ""
      action:
        type: COMMAND    # Type d'action
        value: "spawn"   # Valeur de l'action
```

### Types d'actions disponibles

| Type | Description | Exemple de value |
|------|-------------|-----------------|
| `CONNECT` | Rejoint un serveur BungeeCord | `"survival"` |
| `COMMAND` | Commande exécutée par le joueur | `"friend gui"` |
| `CONSOLE_COMMAND` | Commande exécutée en console | `"give %player% diamond 1"` |
| `URL` | Lien cliquable dans le chat | `"https://monsite.fr"` |
| `MESSAGE` | Message envoyé au joueur | `"&aBonjour %player% !"` |
| `CLOSE` | Ferme le menu | _(vide)_ |
| `NONE` | Aucune action (décoratif) | _(vide)_ |

### Ajouter un serveur BungeeCord

```yaml
servers:
  mon_serveur:
    display-name: "&a✦ Mon Serveur"
    server-name: "mon_serveur"  # ← Nom EXACT dans BungeeCord config.yml
    slot: 15
    material: GRASS_BLOCK
    lore:
      - ""
      - "&7Description du serveur"
      - ""
      - "&a● &f%players% joueur(s) en ligne"
      - ""
      - "&eCliquez pour rejoindre !"
```

### Placeholders disponibles

| Placeholder | Description |
|-------------|-------------|
| `%player%` | Nom du joueur |
| `%displayname%` | Nom affiché |
| `%health%` | Points de vie |
| `%level%` | Niveau XP |
| `%world%` | Monde actuel |
| `%ping%` | Ping en ms |
| `%gamemode%` | Mode de jeu |
| `%food%` | Niveau de faim |
| `%players%` | Joueurs sur un serveur _(items serveurs uniquement)_ |
| `%server%` | Nom du serveur _(items serveurs uniquement)_ |

---

## 🎒 Hotbar personnalisée dans une zone de hub

En plus du GUI `/menu`, le plugin peut afficher une **hotbar personnalisée**
(menu + raccourcis) qui n'apparaît que lorsque le joueur se trouve dans une
zone que vous délimitez vous-même, monde par monde. Dès qu'il quitte la zone,
sa hotbar d'origine (les items qu'il avait en main) lui est automatiquement rendue.

### 1. Définir la zone (une fois par monde)
Placez-vous au premier coin de la zone souhaitée, puis :
```
/hubzone pos1
```
Allez au coin opposé (en diagonale), puis :
```
/hubzone pos2
```
➜ La zone est immédiatement sauvegardée pour **le monde dans lequel vous êtes**
(fichier `zones.yml`, généré automatiquement).

### 2. Autres commandes
| Commande | Description |
|----------|--------------|
| `/hubzone pos1` | Définit le premier coin de la zone |
| `/hubzone pos2` | Définit le second coin et sauvegarde la zone |
| `/hubzone info` | Affiche les coordonnées de la zone du monde actuel |
| `/hubzone remove` | Supprime la zone du monde actuel |

### 3. Configurer le contenu de la hotbar
Tout se règle dans `config.yml`, section `hotbar.items` (slots 0 à 8) :
```yaml
hotbar:
  enabled: true
  items:
    menu:
      slot: 0
      material: NETHER_STAR
      name: "&b✦ &fMenu des Mondes"
      action:
        type: OPEN_MENU   # ← ouvre le GUI /menu avec la liste des serveurs
        value: ""
    shop:
      slot: 1
      material: EMERALD
      name: "&a🛒 &2Boutique"
      action:
        type: URL
        value: "https://boutique.votreserveur.fr"
```
Les mêmes types d'actions que pour le menu (`CONNECT`, `COMMAND`, `URL`, `MESSAGE`, ...)
sont disponibles, plus `OPEN_MENU` qui ouvre le GUI principal. Par défaut, la
hotbar fournie contient : le menu des mondes, la boutique, `/friend`, Discord,
le site et le vote.

⚠️ Cette fonctionnalité est **Spigot uniquement** (pas de zone côté BungeeCord) :
chaque serveur Spigot gère sa propre zone de hub indépendamment.

---

## 🔧 Commandes

| Commande | Description | Permission |
|----------|-------------|-----------|
| `/menu` | Ouvre le menu principal | `mainmenu.use` |
| `/menu reload` | Recharge la configuration | `mainmenu.reload` |
| `/hub` | Retourne au hub | `mainmenu.hub` |
| `/lobby` | Retourne au lobby | `mainmenu.hub` |
| `/hubzone pos1\|pos2\|info\|remove` | Gère la zone de hub (hotbar) | `mainmenu.admin` |

---

## 🔐 Permissions

| Permission | Description | Défaut |
|-----------|-------------|--------|
| `mainmenu.use` | Ouvrir le menu | Tous |
| `mainmenu.reload` | Recharger la config | OP |
| `mainmenu.hub` | Utiliser /hub et /lobby | Tous |
| `mainmenu.admin` | Définir la zone de hub (/hubzone) | OP |

---

## 📡 Canal de communication

Le plugin utilise le canal `mainmenu:data` pour communiquer entre BungeeCord et Spigot :
- **Spigot → BungeeCord** : demande de téléportation, demande des counts
- **BungeeCord → Spigot** : mise à jour du nombre de joueurs par serveur

Assurez-vous qu'aucun pare-feu interne ne bloque les plugin messages.

---

## 💡 Astuces

- Rechargez la config sans redémarrer avec `/menu reload`
- Les vitrages colorés (`GRAY_STAINED_GLASS_PANE`) sont parfaits pour les décorations
- Définissez `glowing: true` pour faire briller les items importants
- Les counts de joueurs sont mis à jour automatiquement à chaque connexion/déconnexion
