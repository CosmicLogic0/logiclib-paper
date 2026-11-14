# CI helpers
- `get-paper.sh <version>`: downloads the newest Paper server jar for that Minecraft version.
- `server-test.sh`: starts the server with LogicLib, waits for it to finish starting, stops it, and fails if LogicLib didn't load or logged an error.
Both are used by `.github/workflows/build.yml` for all 10 target versions (1.12.2 → 26.3).
