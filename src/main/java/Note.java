import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;
import java.util.UUID;

/**
 * Note klasse repræsenterer en enkelt note med titel, indhold og metadata
 */
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private String id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    
    /**
     * Opretter en ny note med automatisk genereret ID og tidsstempler
     */
    public Note(String title, String content) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }
    
    /**
     * Konstruktør til at genskabe eksisterende noter (fx fra fil)
     */
    public Note(String id, String title, String content, LocalDateTime createdAt, LocalDateTime lastModified) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
    }
    
    // Getters og Setters
    public String getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        this.lastModified = LocalDateTime.now();
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.lastModified = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getLastModified() {
        return lastModified;
    }
    
    public void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
    
    /**
     * Returnerer formateret oprettelsesdato
     */
    public String getFormattedCreatedAt() {
        return createdAt.format(DATE_FORMATTER);
    }
    
    /**
     * Returnerer formateret sidste ændring dato
     */
    public String getFormattedLastModified() {
        return lastModified.format(DATE_FORMATTER);
    }
    
    /**
     * Returnerer et kort preview af noteens indhold (første 100 tegn)
     */
    public String getContentPreview() {
        if (content == null || content.trim().isEmpty()) {
            return "(Tom note)";
        }
        String preview = content.trim();
        if (preview.length() > 100) {
            preview = preview.substring(0, 97) + "...";
        }
        return preview;
    }
    
    @Override
    public String toString() {
        return title + " - " + getFormattedLastModified();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Note note = (Note) obj;
        return id.equals(note.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}