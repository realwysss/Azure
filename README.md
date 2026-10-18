# azure client

a custom client for minecraft 1.21.11 (fabric), made by wysss. version 1.0.1.

- custom title screen with your skin, falling stars and an animated logo. your skin plays 25 emotes (click it to start one)
- skin changer for your real account (title screen button or the Skin Changer module): pick a png or copy a player, classic or slim
- an intro animation when the game finishes loading (purple lines, about 5 seconds, any key skips it, can be turned off)
- scoreboard module: hide it, remove the background, black and white, scale and position
- custom loading, pause and multiplayer screens, buttons and menus in a purple glass look
- the azure menu on right shift (the key can be changed in settings): modules, settings, profiles
- hud: fps, ping, cps, coordinates, clock, keystrokes (wasd up to a full keyboard), armor, totem counter, inventory overlay, potions, spotify (cover, progress, live sound bars), all draggable in the hud editor with text effects and shadows
- modules you can switch on and off: discord presence with editable lines, server namer, tuff bright, custom sky (aurora, stars, falling stars in your color), low end modes, crystal and anchor optimizers, no place delay, no swap delay, hit glow and the other wysss mods
- nuit and nuit interoperability are built in for custom skies (mit license, see `licenses`)
- one fps boost module with presets and many settings, freelook, time changer, custom pause and multiplayer screens (favourites, drag to reorder, list or tiles)
- zoom, custom crosshair, toggle sprint, weather changer, hitboxes, reach and combo displays, auto text, chat keybinds, keybind search, snippet (screenshot buttons and gallery), no tooltips, hide skin in inventory
- a bell for update notifications and messages from the developer, an optional updater (verified with sha-256) and a config backup before updates
- an optional online players count (anonymous random id through ntfy, can be turned off in settings)

## loader (easiest way)

put `azure-loader` and fabric api in your mods folder. on every start the loader downloads the newest azure release (sha-256 checked), keeps it in `config/azure/core` and starts the game with it. if a new version crashes during startup the loader skips it for a few hours and runs the previous one. it works offline with the last download. set updates to off in the azure settings to stop the check. the loader only needs updating if this page says so.

if the modrinth app shows its own discord status next to azure, turn off discord rpc in the modrinth app settings under appearance.

you can also use the full `azure-mc1.21.11` jar directly instead, then the loader does nothing.

needs fabric api. client only.

`1.21.11` has the client sources, `loader` the loader sources, the jars are in `builds`. build with `./gradlew build`. java 21.

all rights reserved, made by wysss. see LICENSE. nuit is mit licensed by its authors.
