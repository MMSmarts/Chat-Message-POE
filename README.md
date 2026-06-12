Smarts (Chat Message POE)

Smarts is a small Java assignment that implements a basic message manager (send/store/disregard) with simple persistence and reporting. The project includes unit tests that verify behavior and an automated CI workflow that runs the test suite.

This README provides a short developer quick-start, build/test commands, and example snippets so you can get started quickly.

---

Quick summary
-------------
- Messages have: messageID (10 chars), messageNumber (int), recipient (expected `+27#########`), sender, and messageText.
- Supported operations: Sent, Stored, Disregarded.
- Stored messages are persisted as a JSON array in `storedMessages.json`.
- Tests are under `Test/testApp` and validate the core behaviour.

Project layout
--------------
- `src/mainApp/` — application sources (Message, Login, Main)
- `Test/testApp/` — JUnit tests
- `pom.xml` — Maven POM (manages Gson and JUnit)
- `.github/workflows/test.yml` — GitHub Actions workflow (runs `mvn test`)

What changed recently
---------------------
- Manual JSON handling replaced with Gson for robust serialization/deserialization.
- Added `pom.xml` and updated CI to use Maven so dependencies are declared and managed.
- Improved IO error handling and fixed duplicate search results.

Quick-start (recommended: Maven)
--------------------------------
Prerequisite: Java 21 and Maven installed locally.

1) Build and run tests with Maven (the `custom-src` profile maps the current layout):

```bash
mvn -Pcustom-src -B test
```

This will download dependencies (Gson, JUnit) automatically and run the unit test suite.

Manual compile/run (alternative)
--------------------------------
If you do not have Maven, you can compile and run tests manually (Windows PowerShell example):

```powershell
# from project root
mkdir -Force lib
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar -OutFile lib\junit-platform-console-standalone-1.10.2.jar
# compile (Gson jar required on classpath if using Gson in code)
javac -d build\classes -cp lib\gson-2.10.1.jar src\mainApp\*.java
javac -cp "build\classes;lib\gson-2.10.1.jar;lib\junit-platform-console-standalone-1.10.2.jar" -d build\test\classes Test\testApp\*.java
# run tests
java -jar lib\junit-platform-console-standalone-1.10.2.jar --class-path "build\classes;build\test\classes;lib\gson-2.10.1.jar" --scan-class-path
```

Notes
-----
- Prefer Maven for reproducible builds and dependency management.
- The `custom-src` Maven profile maps the repository's current NetBeans-style layout (`src/mainApp`, `Test/testApp`) so Maven can find sources and tests.

Example stored message (what `storedMessages.json` contains)
---------------------------------------------------------
An element in the array looks like:

```json
{
  "messageID": "1111111111",
  "messageHash": "11:1:DIDYOU",
  "sender": "Developer",
  "recipient": "+27834557896",
  "messageText": "Did you get the cake?",
  "flag": "Stored"
}
```

If CI fails
-----------
- Open the Actions page for the failing run and inspect the log. The updated workflow runs `mvn -Pcustom-src test` which should pass if `pom.xml` is present and correct.
- Paste the failing log here if anything still fails and I will diagnose it immediately.

Contributing and next steps
---------------------------
- I can convert to Gradle if you prefer, or refactor the persistence layer into a separate class. Tell me which change you'd like next and I will implement and test it.
- Add a `LICENSE` file before publishing if you want to pick a specific license.

If you'd like more polish on the README (demo steps, example console outputs, screenshots), tell me which sections you'd like expanded and I will update the file accordingly.
