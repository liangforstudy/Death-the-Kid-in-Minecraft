---
description: Short benchmark run of the DTK in-game test (about 5 minutes), to compare models
---
Run a short subset of the in-game test in `tests/ingame/dtk-test.md`, using the `pendulum`
MCP tools. Read `AGENTS.md` and the "Rules" and "How to do things" sections of the test file
first, and follow them.

If the `pendulum` tools are missing or `health` fails, the game is not running: tell me to
start it with `./gradlew runClient` and stop.

Run only these, in order:
1. Setup steps 1–5. If a world is open, first use the pause menu's `Save and Quit to Title`.
   Always create a fresh world named `DTK Bench` (if that name exists, Minecraft adds a number;
   say which world you used).
2. T01 Mod is loaded.
3. T02 Creative tab and items: only the Boss Kishin Soul row (tooltip text, name colour, glint).
4. T03 Starting values.
5. T04 Mana regeneration: step 1 only.
6. T06 Absorbing souls: steps 1–4.
7. T12 Night of Madness and tiers: step 1, then only the hoglin row.

Write the report as the test file describes, but in
`tests/ingame/reports/bench_<YYYY-MM-DD_HHMM>_<model name>/report.md`, and put the model
you are running as in its first line. For every result, quote the exact evidence you saw
(chat line, action-bar text, tool output). Never mark a check PASS without evidence.
Only test and report: do not change code or config.

$ARGUMENTS
