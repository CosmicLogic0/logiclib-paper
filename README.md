# LogicLib for Paper

Shared code library for CosmicLogic's Paper plugins. Adds no gameplay on its own.
You only need it if one of CosmicLogic's plugins lists it as required.

- Plugin name: `LogicLib` · Package: `com.cosmiclogic.logiclib.paper` · License: MIT
- **One jar for Paper 1.12.2 → 26.3** (Java 8 bytecode, compiled against the 1.13.2 API, version-specific code is guarded)
- The Forge/NeoForge library is in `..\mod\` (a separate project and jar)

## Building
Run `gradlew build` (Windows: `gradlew.bat build`). The jar is in `build/libs/`. Needs Java 17 to build; the jar runs on Java 8+.

## Using it in a plugin
`plugin.yml`: `depend: [LogicLib]` (only if the plugin really uses it).
During development: run `gradlew publishToMavenLocal` here (add the `maven-publish` plugin if needed), or reference the jar directly with `compileOnly files(...)`.

| Class | What it does |
|---|---|
| `LogicVersion` | `LogicVersion.current().isAtLeast(1, 20, 5)` |
| `LogicConfigs` | `LogicConfigs.load(this)` loads config.yml and adds new keys |
| `LogicText` | `LogicText.send(player, "&8[&bMy Plugin&8]", "&aHello")` |
| `LogicParticles` | `box(...)`, `circle(...)`, `line(...)`: colored particles for one player, every version |

## Testing
Every version it's tagged for must pass the Testing Protocol: start a Paper server of that version with LogicLib + a plugin that uses it, and check the console has no errors.
