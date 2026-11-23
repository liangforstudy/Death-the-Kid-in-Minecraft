# Death the Kid — in-game test (for opencode / Claude Code)

You are testing the Death the Kid mod in the running dev client through the `pendulum` MCP
tools. Work through every test below, then write a report. Read `AGENTS.md` first for how the
tools and coordinates work.

Covers what the mod has today (Phase A + B): items and creative tab, the `/dtk` command,
player data (mana, max mana, ranks, Sanzu lines, symmetry, Liz/Patty), mana regeneration,
Kishin Soul drops, tiers and absorbing, and data kept across death, save/reload and dimensions.

## Rules
- Do not change any code or config. Only observe, and report.
- Never call `window/raise`; the user is working in other windows.
- If a test fails, record what you saw and keep going with the next test. Mark a test
  BLOCKED (with the reason) if you could not run it at all.
- Expected numbers come from the code (`KidBalance`, `SoulTier`, `KidData`). Report the actual
  value whenever it differs, even if it looks harmless.
- Take a screenshot (`gui/screenshot` with a `path`) as evidence for every visual check, saved
  under the run folder (see Report). Name them after the test, e.g. `T02-tab.png`.

## How to do things
- **Run a command:** `script/eval` with `mc.executeCommand('/dtk show')`. It runs as the player.
- **Read command output:** chat lines are written to `run/logs/latest.log` as
  `[System] [CHAT] <text>`. After a command, wait 1 second and read the last few `[CHAT]` lines
  of that file. `/dtk show` prints two lines:
  `Mana <m>/<max> | Souls <n> | Sanzu <l>/3 | Rank <r> (combo <c>) | Symmetry <s>` and
  `Liz <h>% | Patty <h>% | Meister <on|off> | Contracted <yes|no>`.
  Command errors show as a red chat line ("Incorrect argument", "Unknown or incomplete").
- **Action-bar messages** (soul absorbed, cap reached) are not in the log: take a screenshot
  within 1 second of the action and read the text above the hotbar.
- **Count items in your inventory:** `/clear @s <item> 0` prints
  `Found <n> matching item(s) on player <name>` without removing anything.
- **Use the held item once:** `script/eval` with `mc.player.use()` (one right-click).
  Do not use `mc.player.useItem()`: it holds right-click for 32 ticks and uses the item
  several times. To sneak-use: `mc.player.sneak(true)`, `wait` 0.5 s, `mc.player.use()`,
  then `mc.player.sneak(false)`. Put items in hand with
  `/item replace entity @s weapon.mainhand with <item> <n>` (or `mc.inv.selectHotbar(1-9)`).
- **Kill a mob as the player** (souls only drop for player kills):
  `/damage @e[type=<mob>,limit=1,sort=nearest] 1000 minecraft:player_attack by @s`.
- Errors from the mod: search `run/logs/latest.log` for `deaththekid` together with
  `ERROR`, `Exception` or `Caused by`.

## Setup
1. `health`. If it fails, the game is not running: report BLOCKED for everything and stop.
2. Note the mod version (Mods screen or `run/logs/latest.log`: `Death the Kid`) and the
   project commit (`git rev-parse --short HEAD`) for the report.
3. Get into a fresh test world: if on the title screen, Singleplayer → Create New World →
   name it `DTK Test` → click `Game Mode` until it says Creative → `Allow Cheats: ON` →
   Create New World. If already in a world, tell the user in the report which world you used.
4. Run `/gamerule doDaylightCycle false`, `/gamerule doMobSpawning false`,
   `/gamerule keepInventory true`, `/time set 1000`, `/weather clear`, `/kill @e[type=!player]`.
5. `/dtk reset`, then `/dtk show`.

## Tests

### T01 Mod is loaded
Pause menu (`ESC`) → `Mods` → type `death` in the search box → select the entry.
Expect "Death the Kid", version 0.1.0, mod id `deaththekid`. Screenshot. Go back to the game
(`Done`, then `Back to Game`).

### T02 Creative tab and items
Open the inventory (`E`). Find the "Death the Kid" tab (it may be on page 2: use the `>`
arrow). Open it. Expect exactly 5 items, in this order: Shinigami Pact, Minor Kishin Soul,
Corrupted Kishin Soul, Elite Kishin Soul, Boss Kishin Soul. Screenshot.
`gui/hover` over each and record the tooltip text:
| Item | Expected tooltip |
|---|---|
| Shinigami Pact | name only (placeholder item), epic rarity (magenta name) |
| Minor Kishin Soul | `+1 Max Mana`, `Right-click to absorb, sneak to absorb the stack` |
| Corrupted Kishin Soul | `+3 Max Mana`, usage line; uncommon (yellow name) |
| Elite Kishin Soul | `+10 Max Mana`, usage line; rare (aqua name); enchantment glint |
| Boss Kishin Soul | `+50 Max Mana`, usage line; epic (magenta name); enchantment glint |
Also check every item has a texture (no purple/black "missing texture" checkerboard).
Close the inventory.

### T03 Starting values
After `/dtk reset`: `/dtk show` should read
`Mana 100/100 | Souls 0 | Sanzu 0/3 | Rank D (combo 0) | Symmetry 0` and
`Liz 100% | Patty 100% | Meister off | Contracted no`.

### T04 Mana regeneration
1. `/dtk mana 0`, wait exactly 10 seconds (`wait`), `/dtk show`. Expect about 30 mana
   (3 per second; accept 24–36).
2. `/dtk rank S`, `/dtk mana 0`, wait 10 seconds, `/dtk show`. Expect about 40 (4 per second;
   accept 34–46).
3. `/dtk mana 95`, wait 5 seconds, `/dtk show`. Expect exactly 100 (never above max).
4. `/dtk rank D`.

### T05 Command limits
Run each and record the output:
- `/dtk mana 1000` → mana shows 100/100 (clamped to max mana).
- `/dtk maxmana 50` → rejected (below 100). `/dtk maxmana 1001` → rejected.
- `/dtk maxmana 1000` → `.../1000`. Then `/dtk maxmana 100` → mana drops to at most 100.
- `/dtk lines 3` → `Sanzu 3/3`; `/dtk lines 4` → rejected.
- `/dtk symmetry -100` → `Symmetry -100`; `/dtk symmetry 101` → rejected.
- `/dtk pistol liz 30` → Liz 30%, Patty unchanged; `/dtk pistol both 100` → both 100%.
- `/dtk rank s` (lowercase) → rejected; `/dtk rank B` → `Rank B`.
- `/dtk meister true`, `/dtk contracted true` → on / yes.
- `/dtk combo 12` → `(combo 12)`.
Finish with `/dtk reset` and check T03's values again.

### T06 Absorbing souls
1. `/gamemode survival` (in creative the stack is not used up).
2. `/item replace entity @s weapon.mainhand with deaththekid:minor_kishin_soul 5`.
3. Use it once. Screenshot the action bar: expect
   `Absorbed 1 soul(s): +1 Max Mana (now 101)`. `/clear @s deaththekid:minor_kishin_soul 0`
   → 4 left.
4. Sneak and use: expect `Absorbed 4 soul(s): +4 Max Mana (now 105)`; 0 left.
5. `/item replace entity @s weapon.mainhand with deaththekid:boss_kishin_soul 1`, use →
   `+50 Max Mana (now 155)`.
6. `/dtk show` → `Mana .../155 | Souls 6`. Mana itself is not raised by absorbing.
7. Repeat step 5 with one corrupted soul (+3 → 158) and one elite soul (+10 → 168).

### T07 Mana cap
1. `/dtk maxmana 995`, then hold 5 elite souls and sneak-use. Expect
   `Absorbed 1 soul(s): +5 Max Mana (now 1000)` (the cap cuts the 10 short) and 4 elite souls
   left. Record the action-bar text and the `/clear ... 0` count.
2. Use again at 1000: expect `Your Soul Wavelength is already at its limit (1000)` and the
   stack unchanged.
3. `/gamemode creative`, `/dtk reset`.

### T08 Death
1. `/dtk maxmana 300`, `/dtk mana 50`, `/dtk rank A`, `/dtk combo 5`, `/dtk symmetry 40`,
   `/dtk lines 2`, `/dtk meister true`, `/dtk contracted true`, `/dtk pistol liz 30`.
2. `/kill @s`, click `Respawn`.
3. `/dtk show`. Expect kept: max mana 300, Sanzu 2, Contracted yes, Liz 30%, Souls unchanged.
   Expect reset: Rank D, combo 0, Symmetry 0, Meister off, and mana refilled to 300.

### T09 Save and reload
1. `/dtk maxmana 250`, `/dtk lines 1`, `/dtk rank C`.
2. Pause menu → `Save and Quit to Title`. Singleplayer → select `DTK Test` →
   `Play Selected World`.
3. `/dtk show`: max mana 250, Sanzu 1, Rank C (all saved). Note the mana value.

### T10 Changing dimension
1. `/execute in minecraft:the_nether run tp @s 0 120 0`, wait 3 seconds, `/dtk show` →
   same values as before.
2. `/execute in minecraft:overworld run tp @s 0 100 0`, `/dtk show` → same again.
3. `/dtk reset`.

### T11 Soul drops (day)
Stand on open ground. Clear items first: `/kill @e[type=item]`, and empty soul items with
`/clear @s deaththekid:minor_kishin_soul`.
1. `/time set 1000` (day). Summon 20 zombies 2 blocks away one at a time and kill each:
   `/summon zombie ~2 ~ ~ {NoAI:1b}` then the `/damage ... by @s` command above. Wait 3 seconds
   so the drops are picked up (stand still; items are picked up within 2 blocks).
2. `/clear @s deaththekid:minor_kishin_soul 0`. Expect roughly 35% of 20 = 7 souls
   (anything from 2 to 13 is normal luck; 0 or 20 is a failure). Record the number.
3. Not a player kill: summon 5 zombies and `/kill @e[type=zombie]` → no new souls.
4. Not a monster: summon 5 cows and kill them with `/damage ... by @s` → no souls.

### T12 Night of Madness and tiers (full moon)
1. `/time set 18000` (midnight of day 0, which is a full moon). Every player kill of a monster
   should now drop exactly 2 souls.
2. Kill each mob below the same way, one at a time, and count that tier's souls with
   `/clear @s deaththekid:<tier>_kishin_soul 0` before and after:
| Mob | Expected tier and count |
|---|---|
| zombie ×3 | minor, 6 |
| husk ×2 | corrupted, 4 |
| evoker ×1 | elite, 2 |
| hoglin ×1 | corrupted, 2 |
| warden ×1 | boss, 2 |
   Note on hoglin: it has 40 max health but is in the `corrupted_soul_mobs` tag, and tags are
   checked before the 40-health elite rule, so it drops **corrupted** souls, not elite.
3. `/time set 1000`.

### T13 Errors
Search `run/logs/latest.log` for errors and exceptions mentioning `deaththekid`, and for any
crash report in `run/crash-reports/` newer than the test start. Copy the first lines of each.

## Report
Create a run folder `tests/ingame/reports/<YYYY-MM-DD_HHMM>/` and write `report.md` there,
with the screenshots next to it:

```markdown
# DTK in-game test — <date time>
Mod <version>, commit <hash>, world <name>, agent <opencode/Claude Code + model>

## Summary
<n> passed, <n> failed, <n> blocked. One sentence on the most important problem.

## Results
| Test | Result | Evidence | Notes |
|---|---|---|---|
| T01 Mod is loaded | PASS | T01-mods.png | |
| T04 Mana regeneration | FAIL | `Mana 18/100 ...` after 10 s | expected ~30 |
...

## Failures
For each FAIL: steps to reproduce, expected, actual, screenshot.

## Observations
Anything odd that is not a clear failure (wording, visuals, balance, design questions).
```

Finally, tell the user the summary line and the path to the report.
