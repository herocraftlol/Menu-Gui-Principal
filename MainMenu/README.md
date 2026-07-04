# 🎮 MainMenu Plugin — Guide d'installation

Plugin Minecraft **BungeeCord + Spigot** qui affiche un **menu GUI configurable** via `/menu`.

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
        ├── gui/MenuGui.java
        ├── listeners/GuiClickListener.java
        ├── listeners/PlayerJoinListener.java
        ├── managers/ConfigManager.java
        ├── managers/PlayerCountManager.java
        ├── messaging/PluginMessageReceiver.java
        └── utils/ItemBuilder.java
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

## 🔧 Commandes

| Commande | Description | Permission |
|----------|-------------|-----------|
| `/menu` | Ouvre le menu principal | `mainmenu.use` |
| `/menu reload` | Recharge la configuration | `mainmenu.reload` |
| `/hub` | Retourne au hub | `mainmenu.hub` |
| `/lobby` | Retourne au lobby | `mainmenu.hub` |

---

## 🔐 Permissions

| Permission | Description | Défaut |
|-----------|-------------|--------|
| `mainmenu.use` | Ouvrir le menu | Tous |
| `mainmenu.reload` | Recharger la config | OP |
| `mainmenu.hub` | Utiliser /hub et /lobby | Tous |

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
