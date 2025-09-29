# Installation af Java Development Kit (JDK)

For at kompilere Java programmer har du brug for JDK (Java Development Kit), ikke kun JRE (Java Runtime Environment).

## Download og installer JDK

1. Gå til Oracle's website eller OpenJDK:
   - Oracle JDK: https://www.oracle.com/java/technologies/downloads/
   - OpenJDK: https://openjdk.org/install/

2. Download JDK 8 eller nyere for Windows

3. Installer JDK og sørg for at tilføje det til PATH

## Alternativt: Brug online compiler

Du kan også teste programmet ved at kopiere kildekoden til en online Java compiler:
- https://www.programiz.com/java-programming/online-compiler/
- https://replit.com/languages/java
- https://www.jdoodle.com/online-java-compiler/

## Manual kompilering efter JDK installation

```cmd
cd NotesApp\src\main\java
javac -d ..\..\..\bin *.java
cd ..\..\..\bin
java NotesApp
```

## Tjek om JDK er korrekt installeret

Kør disse kommandoer i cmd:
```cmd
java -version
javac -version
```

Begge skulle give version information hvis JDK er korrekt installeret.