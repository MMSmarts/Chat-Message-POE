Project test libraries (not committed)
-----------------------------------

This project expects the JUnit Platform Console standalone JAR in the local `lib/` folder so IDE/Ant can run tests locally.

Place the file named:

  junit-platform-console-standalone-1.10.2.jar

into this folder.

You can download it from Maven Central. Example PowerShell command (run from the project root):

```powershell
mkdir -Force lib; 
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar -OutFile lib\junit-platform-console-standalone-1.10.2.jar
```

If you prefer a different version (the project was configured for 1.10.2), update the filename in `nbproject/project.properties` accordingly.

Notes:
- We can't commit the binary JAR on your behalf here. Adding the jar locally into `lib/` will let NetBeans/Ant find it and run tests.
- The CI workflow already downloads test jars for GitHub Actions.

