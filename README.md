Smarts — Technical README

Project snapshot
----------------
- Name: Smarts
- Language: Java (source compiled with javac; project configured for Java 21 in NetBeans metadata)
- Project type: small educational/POE assignment implementing a simple message manager with in-memory collections, file persistence and reporting

Repository layout (important files)
----------------------------------
- src/mainApp/
  - `Message.java`   — Core domain class: message model, validation, in-memory collections, JSON persistence helpers, reporting and search/delete operations.
  - `Login.java`     — Simple validation utilities used by tests (username/password/cell checks).
  - `Main.java`      — Application entry/console driver (menu-driven behavior).
- Test/
  - `testApp/MessageTest.java` — JUnit tests for `Message` behaviour and persistence.
  - `testApp/LoginTest.java`   — JUnit tests for `Login` validators.
- nbproject/         — NetBeans project metadata; `nbproject/project.properties` references a test runtime JAR.
- lib/               — (not committed by default) expected location for `junit-platform-console-standalone-1.10.2.jar` when running tests locally.
- storedMessages.json — runtime file used to persist stored messages (created by the app when storing messages).

High-level behavior
-------------------
- Messages contain: messageID (10 chars), messageNumber (int), recipient (string, expected in South African international format `+27xxxxxxxxx`), sender, messageText.
- Validation performed: message ID length, recipient format, message length (<= 250).
- Message hash generation: prefix of messageID + messageNumber + (first+last word or single word), cleaned of trailing punctuation.
- Lifecycle operations via `sentMessage(int option)`:
  - 1 => Sent (adds to sent array)
  - 2 => Disregarded (adds to disregarded array)
  - 3 => Stored (writes to JSON file and adds to stored array)
- Persistence: stored messages are written to `storedMessages.json`. The application has helpers to read that file and load messages back into memory.

Data format (storedMessages.json)
--------------------------------
The project writes stored messages as a JSON array of objects. Each object contains these keys: `messageID`, `messageHash`, `sender`, `recipient`, `messageText`, `flag`.

Example element:
{
  "messageID": "1111111111",
  "messageHash": "11:1:DIDYOU",
  "sender": "Developer",
  "recipient": "+27834557896",
  "messageText": "Did you get the cake?",
  "flag": "Stored"
}

Design notes & trade-offs
------------------------
- Manual JSON handling: The code builds and parses JSON strings by hand. This is simple for the assignment but brittle (escaping, formatting and edge cases). Consider using a solid JSON library (Gson/Jackson) for production/robustness.
- Persistence strategy: The app rewrites the entire JSON file on each store/delete operation. That is acceptable for small data sets but inefficient for larger data.
- Deduplication: The code previously returned duplicate search results when the same message appeared both in-memory and on-disk; this was fixed by deduplicating results in `searchByRecipient`.

Build and test (developer instructions)
-------------------------------------
The project was authored with NetBeans/Ant but uses plain Java sources, so you can build/tests with either NetBeans/Ant or the basic `javac` + JUnit Platform Console standalone jar.

1) Recommended: run tests with the JUnit Platform Console standalone JAR.

Download the test runner jar into `lib/` (project root):

PowerShell:
```powershell
mkdir -Force lib
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar -OutFile lib\junit-platform-console-standalone-1.10.2.jar
```

2) Compile sources:
```powershell
mkdir -Force build\classes
javac -d build\classes src\mainApp\*.java
```

3) Compile tests (make sure the jar is present in `lib/`):
```powershell
mkdir -Force build\test\classes
javac -cp "build\classes;lib\junit-platform-console-standalone-1.10.2.jar" -d build\test\classes Test\testApp\*.java
```

4) Run tests:
```powershell
java -jar lib\junit-platform-console-standalone-1.10.2.jar --class-path "build\classes;build\test\classes" --scan-class-path
```

If you prefer Ant/NetBeans
- Ensure `lib/junit-platform-console-standalone-1.10.2.jar` exists, then run `ant` or use NetBeans Build Project. The `nbproject/project.properties` has been updated to reference the `lib/` jar.

Developer notes
---------------
- Important classes and methods to inspect:
  - `Message` (in `src/mainApp/Message.java`): public helpers `sentMessage`, `storeMessage`, `loadStoredMessagesFromJSON`, `loadStoredMessagesIntoArray`, `searchByRecipient`, `searchByMessageID`, `deleteByMessageHash`, `displayFullReport`, `displayStoredMessagesSenderAndRecipient`, `getLongestStoredMessage`.
  - `Login` (in `src/mainApp/Login.java`): username/password/cell validators.
  - Tests (in `Test/testApp/`): show expected behavior and edge cases; use them as a specification when refactoring.

- Suggested improvements (prioritized):
  1) Replace manual JSON handling with Gson or Jackson. Update tests accordingly.
  2) Convert the project to Maven or Gradle to manage JUnit and other dependencies automatically.
  3) Add robust error handling around IO operations and unit tests for IO failure modes.

Contact / contribution
----------------------
If you want me to implement any of the suggested improvements (Maven/Gradle conversion or JSON refactor), say which one and I will implement it and run the test suite.

License
-------
No license provided; add a LICENSE file if you intend to open-source this project.

