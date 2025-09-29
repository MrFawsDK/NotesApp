import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * NotesAppGUI er hovedvinduet for notes applikationen
 * Håndterer al brugerinteraktion og koordinerer med backend klasserne
 */
public class NotesAppGUI extends JFrame {
    private final CryptoManager cryptoManager;
    private final NotesStorage notesStorage;
    private List<Note> notes;
    
    // GUI komponenter
    private DefaultListModel<Note> listModel;
    private JList<Note> notesList;
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton newNoteButton;
    private JLabel statusLabel;
    
    private Note currentNote;
    private boolean hasUnsavedChanges = false;
    
    public NotesAppGUI(CryptoManager cryptoManager, NotesStorage notesStorage) {
        this.cryptoManager = cryptoManager;
        this.notesStorage = notesStorage;
        this.notes = new ArrayList<>();
        
        initializeGUI();
        loadNotes();
        updateUI();
    }
    
    /**
     * Initialiserer den grafiske brugergrænseflade
     */
    private void initializeGUI() {
        setTitle("Krypteret Notes App");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Hovedpanel med BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Venstre panel med notes liste
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Højre panel med note editor
        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        // Bundpanel med status
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Håndter window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }
    
    /**
     * Opretter venstre panel med notes liste og knapper
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 0));
        
        // Titel
        JLabel titleLabel = new JLabel("Mine Noter", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Notes liste
        listModel = new DefaultListModel<>();
        notesList = new JList<>(listModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleNoteSelection();
            }
        });
        
        JScrollPane listScrollPane = new JScrollPane(notesList);
        listScrollPane.setPreferredSize(new Dimension(290, 400));
        panel.add(listScrollPane, BorderLayout.CENTER);
        
        // Knapper panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        newNoteButton = new JButton("Ny Note");
        newNoteButton.addActionListener(e -> createNewNote());
        
        deleteButton = new JButton("Slet Note");
        deleteButton.addActionListener(e -> deleteCurrentNote());
        deleteButton.setEnabled(false);
        
        buttonsPanel.add(newNoteButton);
        buttonsPanel.add(deleteButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Opretter højre panel med note editor
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Titel felt
        JLabel titleLabel = new JLabel("Titel:");
        titleField = new JTextField();
        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
        });
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Indhold område
        JLabel contentLabel = new JLabel("Indhold:");
        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
        });
        
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(contentLabel, BorderLayout.NORTH);
        contentPanel.add(contentScrollPane, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Gem knap
        saveButton = new JButton("Gem Note");
        saveButton.addActionListener(e -> saveCurrentNote());
        saveButton.setEnabled(false);
        panel.add(saveButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Opretter bundpanel med statusbar
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Klar");
        statusLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }
    
    /**
     * Indlæser noter fra storage
     */
    private void loadNotes() {
        try {
            notes = notesStorage.loadNotes();
            updateNotesList();
            setStatus("Indlæste " + notes.size() + " noter");
        } catch (Exception e) {
            showError("Fejl ved indlæsning af noter: " + e.getMessage());
        }
    }
    
    /**
     * Opdaterer notes listen i GUI
     */
    private void updateNotesList() {
        listModel.clear();
        for (Note note : notes) {
            listModel.addElement(note);
        }
    }
    
    /**
     * Håndterer valg af note i listen
     */
    private void handleNoteSelection() {
        if (hasUnsavedChanges) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Du har ikke-gemte ændringer. Vil du gemme dem?",
                    "Ikke-gemte ændringer",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                saveCurrentNote();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                // Gendan tidligere valg
                if (currentNote != null) {
                    notesList.setSelectedValue(currentNote, true);
                }
                return;
            }
        }
        
        Note selectedNote = notesList.getSelectedValue();
        if (selectedNote != null) {
            currentNote = selectedNote;
            titleField.setText(currentNote.getTitle());
            contentArea.setText(currentNote.getContent());
            hasUnsavedChanges = false;
            updateUI();
        } else {
            currentNote = null;
            clearEditor();
        }
    }
    
    /**
     * Opretter en ny note
     */
    private void createNewNote() {
        if (hasUnsavedChanges) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Du har ikke-gemte ændringer. Vil du gemme dem?",
                    "Ikke-gemte ændringer",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                saveCurrentNote();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        String title = JOptionPane.showInputDialog(this, "Indtast titel til ny note:");
        if (title != null && !title.trim().isEmpty()) {
            Note newNote = new Note(title.trim(), "");
            notes.add(newNote);
            updateNotesList();
            notesList.setSelectedValue(newNote, true);
            contentArea.requestFocus();
        }
    }
    
    /**
     * Sletter den aktuelt valgte note
     */
    private void deleteCurrentNote() {
        if (currentNote != null) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Er du sikker på at du vil slette noten '" + currentNote.getTitle() + "'?",
                    "Bekræft sletning",
                    JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                notes.remove(currentNote);
                updateNotesList();
                clearEditor();
                currentNote = null;
                hasUnsavedChanges = false;
                saveAllNotes();
            }
        }
    }
    
    /**
     * Gemmer den aktuelle note
     */
    private void saveCurrentNote() {
        if (currentNote != null) {
            currentNote.setTitle(titleField.getText().trim());
            currentNote.setContent(contentArea.getText());
            hasUnsavedChanges = false;
            updateNotesList();
            updateUI();
            saveAllNotes();
        }
    }
    
    /**
     * Gemmer alle noter til disk
     */
    private void saveAllNotes() {
        try {
            notesStorage.saveNotes(notes);
            setStatus("Noter gemt");
        } catch (Exception e) {
            showError("Fejl ved gemning: " + e.getMessage());
        }
    }
    
    /**
     * Markerer at der er ikke-gemte ændringer
     */
    private void markAsChanged() {
        if (currentNote != null && !hasUnsavedChanges) {
            hasUnsavedChanges = true;
            updateUI();
        }
    }
    
    /**
     * Rydder editor felterne
     */
    private void clearEditor() {
        titleField.setText("");
        contentArea.setText("");
        hasUnsavedChanges = false;
        updateUI();
    }
    
    /**
     * Opdaterer UI tilstand
     */
    private void updateUI() {
        boolean hasNote = currentNote != null;
        boolean hasSelection = notesList.getSelectedValue() != null;
        
        titleField.setEnabled(hasNote);
        contentArea.setEnabled(hasNote);
        saveButton.setEnabled(hasNote && hasUnsavedChanges);
        deleteButton.setEnabled(hasSelection);
        
        // Opdater titel for at vise ikke-gemte ændringer
        String title = "Krypteret Notes App";
        if (hasUnsavedChanges) {
            title += " *";
        }
        setTitle(title);
    }
    
    /**
     * Sætter status tekst
     */
    private void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    /**
     * Viser fejlbesked
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Fejl", JOptionPane.ERROR_MESSAGE);
        setStatus("Fejl: " + message);
    }
    
    /**
     * Håndterer applikations afslutning
     */
    private void handleExit() {
        if (hasUnsavedChanges) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Du har ikke-gemte ændringer. Vil du gemme dem før du afslutter?",
                    "Ikke-gemte ændringer",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                saveCurrentNote();
                System.exit(0);
            } else if (result == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            // Cancel - gør ikke noget
        } else {
            System.exit(0);
        }
    }
}