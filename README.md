# pgservice-jetbrains-plugin

![Build](https://github.com/miagilepner/pgservice-jetbrains-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.miagilepner.pgservice.svg)](https://plugins.jetbrains.com/plugin/com.github.miagilepner.pgservice)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.miagilepner.pgservice.svg)](https://plugins.jetbrains.com/plugin/com.github.miagilepner.pgservice)

<!-- Plugin description -->
Import PostgreSQL databases from pg_services.conf. Supports:
- overwriting existing datasources
- using credentials from pgpass

![PGService full import](resources/import.gif)
![PGService option on menu](resources/menu.png)
![PGService dialog](resources/dialog.png)
![PGService dialog with pgpass](resources/dialog2.png)
![Imported sources](resources/sources.png)

Existing datasources are overwritten based on the service name - the service name must match the name of the datasource.

The service must be present in the pgservices file to be imported.  
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "pgservice-jetbrains-plugin"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/miagilepner/pgservice-jetbrains-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
