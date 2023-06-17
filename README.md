# pgservice-jetbrains-plugin

![Build](https://github.com/miagilepner/pgservice-jetbrains-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.miagilepner.pgservice.svg)](https://plugins.jetbrains.com/plugin/20275-pgservice)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.miagilepner.pgservice.svg)](https://plugins.jetbrains.com/plugin/20275-pgservice)

<!-- Plugin description -->
Import PostgreSQL databases from pg_services.conf. Supports:
- overwriting existing datasources
- using credentials from pgpass

Existing datasources are overwritten based on the service name - the service name must match the name of the datasource.

The service must be present in the pgservices file to be imported.  

![PGService full import](resources/import.gif)
<img src="https://github.com/miagilepner/pgservice-jetbrains-plugin/blob/59d78e21de8d70763935878b14ece5c78a302281/resources/menu.png" height=600px>

<img src="https://github.com/miagilepner/pgservice-jetbrains-plugin/blob/59d78e21de8d70763935878b14ece5c78a302281/resources/dialog.png" width=50%>
<img src="https://github.com/miagilepner/pgservice-jetbrains-plugin/blob/59d78e21de8d70763935878b14ece5c78a302281/resources/dialog2.png" width=50%>

<img src="https://github.com/miagilepner/pgservice-jetbrains-plugin/blob/59d78e21de8d70763935878b14ece5c78a302281/resources/sources.png" height=600px>

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "pgservice-jetbrains-plugin"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/miagilepner/pgservice-jetbrains-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
