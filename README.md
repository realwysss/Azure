# azure client

a free client for minecraft 1.21.11 (fabric) made for pvp, fps and a clean look. press right shift to open the menu. made by wysss.

- site: [azureclient.dev](https://azureclient.dev)
- discord: [discord.gg/tXW4huVuRv](https://discord.gg/tXW4huVuRv)

## what it has

- custom title, loading, pause and multiplayer screens in a glass look that follows your accent color (blue by default, purple or any color you pick)
- your skin on the title screen, with dances, plus a skin and cape changer for your real account
- an accounts menu: switch with a session id or log in to your saved accounts (in-game account switcher is built in)
- a hud you can drag around: fps, ping, cps, keystrokes, armor, potions, spotify and more
- an fps boost module with presets, low end modes, custom sky, zoom, freelook, crosshair, hit glow and lots of other modules, all off until you turn them on
- friends: add people by name, see who is online and on which server, join them with one click
- a bell with release notes and news, a short setup on first start and an optional updater

## install

put `azure-loader` and fabric api in your mods folder. the loader downloads the newest azure on every start (sha-256 checked), keeps it in `config/azure/core` and falls back to the previous version if a new one crashes. it works offline with the last download.

you can also use the full `azure-mc1.21.11` jar directly instead. client only.

if the modrinth app shows its own discord status next to azure, turn off discord rpc in the modrinth app settings under appearance.

## source

`1.21.11` has the client sources, `loader` the loader sources, the jars are in `builds`. build with `./gradlew build`. java 21.

all rights reserved, made by wysss. see LICENSE. nuit is mit licensed by its authors. in-game account switcher is bundled unmodified and is lgpl-3.0 licensed by its authors, see `licenses`.
