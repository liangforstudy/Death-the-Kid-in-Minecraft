# Death the Kid in Minecraft — agent notes

Forge 1.20.1 (47.4.28) mod, `modId` `deaththekid`, package `com.liangforstudy.deaththekid`.
ForgeGradle 6, Gradle 8.8, compiles for Java 17. Versions live in `gradle.properties`.

## Build and run
- Build: `./gradlew build` (jar in `build/libs/`).
- Dev client: `./gradlew runClient` (game dir `run/`, 1280x720 window). It is at the main
  menu once a fresh `run/logs/latest.log` contains "Sound engine started" (check the log's
  first timestamp changed, or you may read the previous run's log). The first run decompiles
  Minecraft and takes several minutes. Run only one Gradle build at a time.

## In-game testing (opencode, or Claude Code, drives the game)
The dev client loads **Pendulum** (an MCP server inside the client, TCP `127.0.0.1:25566`)
plus its libraries Jupiter and Rhino, as `runtimeOnly`: test-only, never in the built jar.
Agents reach it through `tools/pendulum-mcp-bridge.js` (stdio-to-TCP, needs Node),
registered as `pendulum` in `opencode.json` (opencode) and `.mcp.json` (Claude Code, which
asks once to approve the project server). Either agent can test if the other is unavailable.

Pendulum comes from our fork, https://github.com/liangforstudy/Pendulum (branch
`fix/gui-coords-focus-hover`). Its input goes through Minecraft's own mouse and keyboard
handlers, so it works with the game window in the background and never moves the OS cursor.
Build it with `./gradlew :1.20.1-forge:build` in the fork and copy
`versions/1.20.1-forge/build/libs/pendulum-<ver>-1.20.1-forge.jar` to
`libs/pendulum-<pendulum_version>.jar` here (`pendulum_version` is in `gradle.properties`).

Order: start the game first, then the agent (the bridge exits if nothing listens; restart
the agent, or reconnect its MCP server, if it started first). `run/config/pendulum.json` has `mcp_enabled: true`, so the
server starts with the game; the log says "MCP server auto-started on port 25566".

### Full mod test
`tests/ingame/dtk-test.md` is the step-by-step in-game test of the mod; run it with the
`/test-dtk` command (opencode and Claude Code). Reports go to `tests/ingame/reports/`
(gitignored). Update the test whenever a phase adds or changes features.

### Coordinates
Clicks, hover, drag and scroll take **GUI coordinates**, the same numbers
`gui/enumerateWidgets` reports. Screenshots are in window pixels with a pixel grid:
GUI = pixel / `guiScale` (every screenshot reports `guiScale`; it is 3 by default here).

### Tools (verified 2026-11-23 on the main menu, Mods screen and creative inventory)
- Buttons and menus, including Forge's Mods list: `gui/clickButton` (by text),
  `simulate/click` (x, y), `simulate/selectListItem`, `gui/enumerateWidgets`, `gui/guiElements`.
- Hover: `gui/hover` (x, y) moves the in-game cursor without clicking and returns what is
  there: the widget, and in inventories the hovered slot with item id, count, name and the
  full tooltip text. The tooltip also shows in the next screenshot.
- Keyboard: `simulate/pressKey` (e.g. `E` opens the inventory, `ESC` closes it),
  `simulate/typeText` (types into the focused text field), `simulate/hotkey`.
- `simulate/scroll` (optional x, y to scroll at), `simulate/mouseDrag`.
- `gui/screenshot` (pass `path` to save it), `health`, `wait`.
- `window/raise` (optional `auto`: true/false): bring the game window in front of other
  windows without giving it focus; clicking any window puts that one in front again. Only use
  it when the user asks to see the game: never raise the window on your own while testing.
  `auto: true` raises it once when loading finishes. Saved in `run/config/pendulum.json`
  (`raise_window`), also in Pendulum's config screen ("Bring Game Window to Front").
- `script/eval` (JavaScript, sandboxed: no Java access): `mc.gui.getTitle()`,
  `mc.executeCommand('/give @s minecraft:diamond_sword')`, inventory via
  `mc.gui.getAllItems()`, `mc.gui.clickSlot(i)`, `mc.gui.quickMoveItem(i)`.

Admin: create test worlds with "Allow Cheats: ON" (the Create New World screen's
`Game Mode` and `Allow Cheats` buttons cycle by clicking), so commands run as an operator.
Use the `/dtk` operator command to inspect and set the mod's player data.

Mouse trap fix: vanilla Minecraft marks its window as focused at startup even when it opened
in the background, then grabs and clips the mouse to the window when it enters gameplay,
trapping the user's cursor in an invisible box. The fork's `FocusGuard` keeps that flag in line
with real focus, so the game only grabs the mouse after the user clicks into it. If the cursor
is ever trapped anyway: click the game window once, then Alt+Tab away.

### Dev-run settings this relies on
- `build.gradle` client run: `mixin.env.remapRefMap` so PlayerAnimator and GeckoLib mixins
  load in dev; `--width 1280 --height 720`.
- `run/options.txt`: `pauseOnLostFocus:false`, so the game keeps running in the background.
