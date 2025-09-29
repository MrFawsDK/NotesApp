# Kør Notes App Online

Da JDK ikke er installeret på denne computer, kan du teste programmet ved at bruge en online Java compiler.

## Option 1: Brug Online Compiler (Anbefalet for test)

1. Gå til https://www.jdoodle.com/online-java-compiler/

2. Kopier hver Java fil ind i editoren og kør dem separat eller samlet:

### Start med denne simple test version (kopier alt til online compiler):

```java
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Simple console version for testing
public class NotesAppSimple {
    private static List<String> notes = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== KRYPTERET NOTES APP (Test Version) ===");
        System.out.println("Velkommen til din personlige notes app!");
        
        while (true) {
            showMenu();
            int choice = getChoice();
            
            switch (choice) {
                case 1: addNote(); break;
                case 2: viewNotes(); break;
                case 3: deleteNote(); break;
                case 4: 
                    System.out.println("Farvel!");
                    return;
                default:
                    System.out.println("Ugyldigt valg!");
            }
        }
    }
    
    private static void showMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1. Tilføj note");
        System.out.println("2. Vis alle noter");
        System.out.println("3. Slet note");
        System.out.println("4. Afslut");
        System.out.print("Vælg (1-4): ");
    }
    
    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void addNote() {
        System.out.print("Indtast din note: ");
        String note = scanner.nextLine();
        if (!note.trim().isEmpty()) {
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            notes.add("[" + timestamp + "] " + note);
            System.out.println("Note tilføjet!");
        }
    }
    
    private static void viewNotes() {
        if (notes.isEmpty()) {
            System.out.println("Ingen noter endnu.");
            return;
        }
        
        System.out.println("\n=== DINE NOTER ===");
        for (int i = 0; i < notes.size(); i++) {
            System.out.println((i + 1) + ". " + notes.get(i));
        }
    }
    
    private static void deleteNote() {
        if (notes.isEmpty()) {
            System.out.println("Ingen noter at slette.");
            return;
        }
        
        viewNotes();
        System.out.print("Indtast nummer på note der skal slettes: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < notes.size()) {
                notes.remove(index);
                System.out.println("Note slettet!");
            } else {
                System.out.println("Ugyldigt nummer!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Indtast kun tal!");
        }
    }
}
```

## Option 2: Installer JDK og kompiler lokalt

1. Download og installer JDK fra:
   - https://www.oracle.com/java/technologies/downloads/
   - Eller Eclipse Temurin: https://adoptium.net/

2. Efter installation, genstart computeren

3. Kør compile.bat i NotesApp mappen

4. Kør run.bat for at starte programmet

## Option 3: Brug IDE som IntelliJ eller Eclipse

1. Download IntelliJ IDEA Community (gratis): https://www.jetbrains.com/idea/
2. Åbn projektet i IntelliJ
3. IntelliJ vil automatisk downloade JDK hvis nødvendigt
4. Kør programmet direkte fra IDE'et