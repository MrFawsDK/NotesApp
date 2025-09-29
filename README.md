# Krypteret Notes App

En sikker notes applikation skrevet i Java, der gemmer alle noter krypteret lokalt på din computer.

## Funktioner

- **Kryptering**: Alle noter krypteres med AES-256 GCM algoritmen
- **Password beskyttelse**: Bruger PBKDF2 med SHA-256 til sikker password-baseret kryptografi
- **Lokal lagring**: Alt gemmes lokalt i din hjemmemappe under `.notesapp`
- **Brugervenlig interface**: Simpel Swing GUI til at oprette, redigere og slette noter
- **Automatisk gemning**: Noter gemmes automatisk når du ændrer dem

## Sikkerhedsfunktioner

- AES-256-GCM kryptering for maksimal sikkerhed
- Password-baseret nøglederivation (PBKDF2) med 100.000 iterationer
- Tilfældige salts for at forhindre rainbow table angreb
- Sicre IV'er (Initialization Vectors) for hver krypteringsoperation

## Sådan bruges programmet

### Første gang
1. Kør programmet
2. Vælg et stærkt master password (mindst 6 tegn)
3. Bekræft passwordet
4. Start med at oprette dine første noter

### Efterfølgende brug
1. Kør programmet
2. Indtast dit master password
3. Dine krypterede noter indlæses automatisk

### Brugergrænseflade
- **Venstre side**: Liste over alle dine noter
- **Højre side**: Editor til at redigere den valgte note
- **Knapper**: 
  - "Ny Note" - Opret en ny note
  - "Slet Note" - Slet den valgte note
  - "Gem Note" - Gem ændringer til den aktuelle note

## Kompilering og kørsel

### VIGTIGT: JDK Installation påkrævet
Først skal du installere Java Development Kit (JDK):

1. **Download JDK**: Gå til https://adoptium.net/ og download Temurin JDK
2. **Installer JDK**: Følg installationsvejledningen
3. **Genstart computer** efter installation
4. **Verificer**: Åbn ny command prompt og kør `javac -version`

### Efter JDK installation:

#### Windows
```cmd
cd NotesApp
compile.bat
run.bat
```

#### Manual kompilering
```cmd
cd src\main\java
javac -d ..\..\..\bin *.java
cd ..\..\..\bin
java NotesApp
```

### Alternativ: Test online
Hvis du ikke kan installere JDK, se `ONLINE_TESTING.md` for at køre en simpel version online.

## Datafiler

Programmet opretter følgende filer i din hjemmemappe under `.notesapp`:
- `notes.dat` - Krypterede noter
- `salt.dat` - Kryptografisk salt til password-derivation

**VIGTIGT**: Glem ikke dit master password! Der er ingen måde at gendanne dine noter uden det.

## Systemkrav

- Java 8 eller nyere
- Windows, macOS eller Linux
- Mindst 50 MB ledig diskplads

## Sikkerhedsanbefalinger

1. Vælg et stærkt, unikt master password
2. Lav backup af `.notesapp` mappen regelmæssigt
3. Gem dit password sikkert (fx i en password manager)
4. Undgå at køre programmet på offentlige computere

## Teknisk information

- **Sprog**: Java
- **GUI Framework**: Swing
- **Kryptering**: AES-256-GCM
- **Password hashing**: PBKDF2WithHmacSHA256
- **Serialisering**: Java ObjectOutputStream (Base64 encoded)