import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * NotesStorage håndterer gemning og indlæsning af krypterede notes til/fra lokal fil
 * Bruger Java objektserialisering og krypterer hele datasættet
 */
public class NotesStorage {
    private final String dataDirectory;
    private final String notesFile;
    private final String saltFile;
    private final CryptoManager cryptoManager;
    
    public NotesStorage(CryptoManager cryptoManager) {
        this.cryptoManager = cryptoManager;
        this.dataDirectory = System.getProperty("user.home") + File.separator + ".notesapp";
        this.notesFile = dataDirectory + File.separator + "notes.dat";
        this.saltFile = dataDirectory + File.separator + "salt.dat";
        
        // Opret data mappe hvis den ikke findes
        createDataDirectory();
    }
    
    /**
     * Opretter data mappen hvis den ikke findes
     */
    private void createDataDirectory() {
        try {
            Files.createDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            System.err.println("Kunne ikke oprette data mappe: " + e.getMessage());
        }
    }
    
    /**
     * Gemmer salt til fil
     */
    public void saveSalt(byte[] salt) throws IOException {
        Files.write(Paths.get(saltFile), salt);
    }
    
    /**
     * Indlæser salt fra fil
     */
    public byte[] loadSalt() throws IOException {
        if (!Files.exists(Paths.get(saltFile))) {
            return null;
        }
        return Files.readAllBytes(Paths.get(saltFile));
    }
    
    /**
     * Tjekker om der findes eksisterende data
     */
    public boolean hasExistingData() {
        return Files.exists(Paths.get(notesFile)) && Files.exists(Paths.get(saltFile));
    }
    
    /**
     * Gemmer liste af noter til krypteret fil
     */
    public void saveNotes(List<Note> notes) throws Exception {
        if (!cryptoManager.isInitialized()) {
            throw new IllegalStateException("CryptoManager er ikke initialiseret");
        }
        
        // Serialiser noter til bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(notes);
        }
        
        // Konverter til string for kryptering
        String serializedData = Base64.getEncoder().encodeToString(baos.toByteArray());
        
        // Krypter data
        String encryptedData = cryptoManager.encrypt(serializedData);
        
        // Gem til fil
        Files.write(Paths.get(notesFile), encryptedData.getBytes("UTF-8"));
    }
    
    /**
     * Indlæser og dekrypterer noter fra fil
     */
    @SuppressWarnings("unchecked")
    public List<Note> loadNotes() throws Exception {
        if (!Files.exists(Paths.get(notesFile))) {
            return new ArrayList<>();
        }
        
        if (!cryptoManager.isInitialized()) {
            throw new IllegalStateException("CryptoManager er ikke initialiseret");
        }
        
        // Læs krypteret data fra fil
        String encryptedData = new String(Files.readAllBytes(Paths.get(notesFile)), "UTF-8");
        
        // Dekrypter data
        String serializedData = cryptoManager.decrypt(encryptedData);
        
        // Deserialiser noter fra bytes
        byte[] data = Base64.getDecoder().decode(serializedData);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                return (List<Note>) obj;
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Sletter alle data filer (bruges til at nulstille applikationen)
     */
    public void deleteAllData() throws IOException {
        Files.deleteIfExists(Paths.get(notesFile));
        Files.deleteIfExists(Paths.get(saltFile));
    }
    
    /**
     * Returnerer stien til data mappen
     */
    public String getDataDirectory() {
        return dataDirectory;
    }
}