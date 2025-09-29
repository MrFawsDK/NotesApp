# 🚀 Hurtig JDK Installation Guide

## Problem
Du får fejlen: "Could not find or load main class NotesApp"

Dette sker fordi:
1. JDK (Java Development Kit) ikke er installeret
2. Programmet blev ikke kompileret korrekt

## Løsning: Installer JDK

### Trin 1: Download JDK
- Gå til: **https://adoptium.net/temurin/releases/**
- Vælg **Windows x64** og **JDK 17 eller 21**
- Download `.msi` filen

### Trin 2: Installer
1. Dobbeltklik på downloaded `.msi` fil
2. Følg installationsguiden
3. **VIGTIGT**: Sørg for at "Add to PATH" er markeret
4. Gennemfør installationen

### Trin 3: Verificer Installation
1. Åbn **ny** Command Prompt (cmd)
2. Kør: `java -version`
3. Kør: `javac -version`

Begge kommandoer skulle vise version information.

### Trin 4: Kompiler og kør NotesApp
```cmd
cd NotesApp
compile.bat
run.bat
```

## Alternativ: Brug IntelliJ IDEA

Hvis du foretrækker en IDE:

1. Download **IntelliJ IDEA Community** (gratis): https://www.jetbrains.com/idea/
2. Åbn NotesApp mappen som projekt
3. IntelliJ vil automatisk downloade JDK hvis nødvendigt
4. Højreklik på `NotesApp.java` → "Run NotesApp.main()"

## Test Version (Ingen Installation Nødvendig)

Se `ONLINE_TESTING.md` for en simpel console version du kan teste online øjeblikkeligt.

---

**Tip**: Efter JDK installation, genstart din computer for at sikre at PATH er opdateret korrekt.