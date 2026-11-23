# Death the Kid in Minecraft

A Minecraft Java Edition **1.20.1 Forge** mod that brings Death the Kid from *Soul Eater* into the game: Liz and Patty, Meister Mode skills, the Lines of Sanzu, symmetry-driven combat, and bosses with their own structures.

## Requirements

- Minecraft 1.20.1 with Forge 47.x (built against 47.4.28)
- [GeckoLib 4](https://modrinth.com/mod/geckolib) for Forge 1.20.1
- [PlayerAnimator](https://modrinth.com/mod/playeranimator) for Forge 1.20.1

## Build

Requirements: a JDK 17 or newer and internet access on the first Gradle run. The build compiles for Java 17 regardless of the installed JDK.

```bash
./gradlew build
```

The mod jar is written to `build/libs/`. GitHub Actions builds every push and pull request and attaches the jar to the run as the `death-the-kid` artifact.

## Development

```bash
./gradlew runClient   # dev client with the mod loaded
./gradlew runServer   # dev server
```

Versions for Forge, GeckoLib and PlayerAnimator live in `gradle.properties`.
