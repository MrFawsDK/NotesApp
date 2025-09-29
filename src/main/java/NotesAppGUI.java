import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * NotesAppGUI er hovedvinduet for notes applikationen med moderne dark theme
 * Håndterer al brugerinteraktion og koordinerer med backend klasserne
 */
public class NotesAppGUI extends JFrame {
    private final CryptoManager cryptoManager;
    private final NotesStorage notesStorage;
    private List<Note> notes;
    
    // Dark Theme Farver
    private static final Color DARK_BACKGROUND = new Color(32, 33, 36);
    private static final Color DARKER_BACKGROUND = new Color(24, 25, 26);
    private static final Color SIDEBAR_BACKGROUND = new Color(41, 42, 45);
    private static final Color TEXT_PRIMARY = new Color(232, 234, 237);
    private static final Color TEXT_SECONDARY = new Color(154, 160, 166);
    private static final Color ACCENT_BLUE = new Color(66, 133, 244);
    private static final Color ACCENT_GREEN = new Color(52, 168, 83);
    private static final Color ACCENT_RED = new Color(234, 67, 53);
    private static final Color BUTTON_HOVER = new Color(60, 64, 67);
    private static final Color INPUT_BACKGROUND = new Color(48, 49, 52);
    private static final Color BORDER_COLOR = new Color(95, 99, 104);
    
    // GUI komponenter
    private DefaultListModel<Note> listModel;
    private JList<Note> notesList;
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton newNoteButton;
    private JLabel statusLabel;
    private JLabel noteCountLabel;
    
    private Note currentNote;
    private boolean hasUnsavedChanges = false;
    
    public NotesAppGUI(CryptoManager cryptoManager, NotesStorage notesStorage) {
        this.cryptoManager = cryptoManager;
        this.notesStorage = notesStorage;
        this.notes = new ArrayList<>();
        
        // Sæt dark theme Look and Feel
        setDarkTheme();
        
        initializeGUI();
        loadNotes();
        updateUI();
    }
    
    /**
     * Sætter dark theme for hele applikationen
     */
    private void setDarkTheme() {
        try {
            // Sæt dark system properties
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            
            // Custom UI defaults
            UIManager.put("Panel.background", DARK_BACKGROUND);
            UIManager.put("OptionPane.background", DARK_BACKGROUND);
            UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        } catch (Exception e) {
            // Fortsæt med standard styling hvis der er problemer
        }
    }
    
    /**
     * Initialiserer den grafiske brugergrænseflade med dark theme
     */
    private void initializeGUI() {
        setTitle("🌙 Krypteret Notes App - Dark Mode");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Sæt frame baggrund til dark
        getContentPane().setBackground(DARK_BACKGROUND);
        
        // Hovedpanel med gradient baggrund
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Subtil gradient baggrund
                GradientPaint gradient = new GradientPaint(
                    0, 0, DARKER_BACKGROUND,
                    0, getHeight(), DARK_BACKGROUND
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        // Venstre sidebar med notes liste
        JPanel leftPanel = createModernSidebar();
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Højre panel med note editor
        JPanel rightPanel = createModernEditor();
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        // Top toolbar
        JPanel topPanel = createModernToolbar();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Bottom status bar
        JPanel bottomPanel = createModernStatusBar();
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
     * Opretter moderne sidebar med notes liste
     */
    private JPanel createModernSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 0));
        sidebar.setBackground(SIDEBAR_BACKGROUND);
        sidebar.setPreferredSize(new Dimension(350, 0));
        sidebar.setBorder(new LineBorder(BORDER_COLOR, 1, false));
        
        // Header med titel og note tæller
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(SIDEBAR_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(20, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📝 Mine Noter");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        noteCountLabel = new JLabel("0 noter");
        noteCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noteCountLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(noteCountLabel, BorderLayout.EAST);
        sidebar.add(headerPanel, BorderLayout.NORTH);
        
        // Notes liste med custom styling
        listModel = new DefaultListModel<>();
        notesList = new JList<>(listModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notesList.setBackground(SIDEBAR_BACKGROUND);
        notesList.setForeground(TEXT_PRIMARY);
        notesList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesList.setFixedCellHeight(60);
        notesList.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        // Custom cell renderer for notes
        notesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                         boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                Note note = (Note) value;
                setBackground(isSelected ? ACCENT_BLUE : SIDEBAR_BACKGROUND);
                setForeground(isSelected ? Color.WHITE : TEXT_PRIMARY);
                setBorder(new EmptyBorder(8, 15, 8, 15));
                
                // Vis titel og dato
                String displayText = "<html><div style='font-weight: bold;'>" + 
                    (note.getTitle().isEmpty() ? "Untitled" : note.getTitle()) +
                    "</div><div style='font-size: 10px; color: " + 
                    (isSelected ? "#E0E0E0" : "#9AA0A6") + ";'>" +
                    note.getFormattedDate() + "</div></html>";
                setText(displayText);
                
                return this;
            }
        });
        
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleNoteSelection();
            }
        });
        
        JScrollPane listScrollPane = new JScrollPane(notesList);
        listScrollPane.setBackground(SIDEBAR_BACKGROUND);
        listScrollPane.setBorder(null);
        listScrollPane.getVerticalScrollBar().setBackground(SIDEBAR_BACKGROUND);
        sidebar.add(listScrollPane, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonsPanel.setBackground(SIDEBAR_BACKGROUND);
        
        newNoteButton = createStyledButton("✨ Ny Note", ACCENT_GREEN);
        newNoteButton.addActionListener(e -> createNewNote());
        
        deleteButton = createStyledButton("🗑️ Slet", ACCENT_RED);
        deleteButton.addActionListener(e -> deleteCurrentNote());
        deleteButton.setEnabled(false);
        
        buttonsPanel.add(newNoteButton);
        buttonsPanel.add(deleteButton);
        sidebar.add(buttonsPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }
    
    /**
     * Opretter moderne note editor
     */
    private JPanel createModernEditor() {
        JPanel editor = new JPanel(new BorderLayout(0, 0));
        editor.setBackground(DARK_BACKGROUND);
        editor.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Titel sektion
        JPanel titleSection = new JPanel(new BorderLayout(0, 10));
        titleSection.setOpaque(false);
        
        JLabel titleLabel = new JLabel("📄 Titel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        titleField = new JTextField();
        styleTextField(titleField);
        titleField.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
        });
        
        titleSection.add(titleLabel, BorderLayout.NORTH);
        titleSection.add(titleField, BorderLayout.CENTER);
        editor.add(titleSection, BorderLayout.NORTH);
        
        // Indhold sektion
        JPanel contentSection = new JPanel(new BorderLayout(0, 10));
        contentSection.setOpaque(false);
        contentSection.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel contentLabel = new JLabel("📝 Indhold");
        contentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contentLabel.setForeground(TEXT_SECONDARY);
        
        contentArea = new JTextArea();
        styleTextArea(contentArea);
        contentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markAsChanged(); }
        });
        
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBackground(INPUT_BACKGROUND);
        contentScrollPane.setBorder(new LineBorder(BORDER_COLOR, 2));
        contentScrollPane.getVerticalScrollBar().setBackground(INPUT_BACKGROUND);
        
        contentSection.add(contentLabel, BorderLayout.NORTH);
        contentSection.add(contentScrollPane, BorderLayout.CENTER);
        editor.add(contentSection, BorderLayout.CENTER);
        
        return editor;
    }
    
    /**
     * Opretter moderne toolbar
     */
    private JPanel createModernToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(DARKER_BACKGROUND);
        toolbar.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel appTitle = new JLabel("🔐 Krypteret Notes - Sikker & Privat");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(TEXT_PRIMARY);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        saveButton = createStyledButton("💾 Gem", ACCENT_BLUE);
        saveButton.addActionListener(e -> saveCurrentNote());
        saveButton.setEnabled(false);
        
        rightPanel.add(saveButton);
        
        toolbar.add(appTitle, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);
        
        return toolbar;
    }
    
    /**
     * Opretter moderne status bar
     */
    private JPanel createModernStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(DARKER_BACKGROUND);
        statusBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        statusLabel = new JLabel("Klar til brug");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        
        JLabel encryptionLabel = new JLabel("🔒 AES-256 Krypteret");
        encryptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        encryptionLabel.setForeground(ACCENT_GREEN);
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(encryptionLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Opretter styled button med moderne design
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor);
                }
            }
        });
        
        return button;
    }
    
    /**
     * Styler text field med dark theme
     */
    private void styleTextField(JTextField field) {
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_BLUE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    }
    
    /**
     * Styler text area med dark theme
     */
    private void styleTextArea(JTextArea area) {
        area.setBackground(INPUT_BACKGROUND);
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(ACCENT_BLUE);
        area.setBorder(new EmptyBorder(15, 15, 15, 15));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setTabSize(4);
    }
    
    /**
     * Håndterer valg af note i listen
     */
    private void handleNoteSelection() {
        if (hasUnsavedChanges) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Du har ikke-gemte ændringer. Vil du gemme dem?",
                "Ikke-gemte ændringer",
                JOptionPane.YES_NO_CANCEL_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                saveCurrentNote();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return; // Annuller valg af ny note
            }
        }
        
        Note selectedNote = notesList.getSelectedValue();
        if (selectedNote != null) {
            currentNote = selectedNote;
            titleField.setText(currentNote.getTitle());
            contentArea.setText(currentNote.getContent());
            hasUnsavedChanges = false;
        } else {
            currentNote = null;
            titleField.setText("");
            contentArea.setText("");
        }
        
        updateUI();
    }
    
    /**
     * Markerer at der er ændringer der skal gemmes
     */
    private void markAsChanged() {
        hasUnsavedChanges = true;
        updateUI();
    }
    
    /**
     * Opretter en ny note
     */
    private void createNewNote() {
        if (hasUnsavedChanges) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Du har ikke-gemte ændringer. Vil du gemme dem?",
                "Ikke-gemte ændringer",
                JOptionPane.YES_NO_CANCEL_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                saveCurrentNote();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        Note newNote = new Note("Ny Note", "Skriv dit indhold her...");
        notes.add(newNote);
        listModel.addElement(newNote);
        notesList.setSelectedValue(newNote, true);
        
        titleField.requestFocus();
        titleField.selectAll();
        
        statusLabel.setText("Ny note oprettet");
        updateNoteCount();
    }
    
    /**
     * Gemmer den nuværende note
     */
    private void saveCurrentNote() {
        if (currentNote != null) {
            currentNote.setTitle(titleField.getText().trim());
            currentNote.setContent(contentArea.getText());
            currentNote.updateModifiedDate();
            
            try {
                notesStorage.saveNotes(notes);
                hasUnsavedChanges = false;
                statusLabel.setText("Note gemt: " + java.time.LocalTime.now().toString().substring(0, 5));
                
                // Opdater listen for at vise ændret dato
                int selectedIndex = notesList.getSelectedIndex();
                listModel.setElementAt(currentNote, selectedIndex);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Fejl ved gemning: " + e.getMessage(),
                    "Gem fejl",
                    JOptionPane.ERROR_MESSAGE
                );
                statusLabel.setText("Fejl ved gemning!");
            }
        }
        updateUI();
    }
    
    /**
     * Sletter den nuværende note
     */
    private void deleteCurrentNote() {
        if (currentNote != null) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Er du sikker på at du vil slette denne note?\n\"" + currentNote.getTitle() + "\"",
                "Bekræft sletning",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                notes.remove(currentNote);
                listModel.removeElement(currentNote);
                
                currentNote = null;
                titleField.setText("");
                contentArea.setText("");
                hasUnsavedChanges = false;
                
                try {
                    notesStorage.saveNotes(notes);
                    statusLabel.setText("Note slettet");
                } catch (Exception e) {
                    statusLabel.setText("Fejl ved sletning: " + e.getMessage());
                }
                
                updateNoteCount();
                updateUI();
            }
        }
    }
    
    /**
     * Indlæser noter fra storage
     */
    private void loadNotes() {
        try {
            notes = notesStorage.loadNotes();
            listModel.clear();
            for (Note note : notes) {
                listModel.addElement(note);
            }
            updateNoteCount();
            statusLabel.setText("Noter indlæst succesfuldt");
        } catch (Exception e) {
            statusLabel.setText("Fejl ved indlæsning: " + e.getMessage());
        }
    }
    
    /**
     * Opdaterer note tæller
     */
    private void updateNoteCount() {
        int count = notes.size();
        noteCountLabel.setText(count + " note" + (count != 1 ? "r" : ""));
    }
    
    /**
     * Opdaterer UI tilstand
     */
    private void updateUI() {
        boolean hasSelection = currentNote != null;
        boolean canSave = hasSelection && hasUnsavedChanges;
        
        saveButton.setEnabled(canSave);
        deleteButton.setEnabled(hasSelection);
        titleField.setEnabled(hasSelection);
        contentArea.setEnabled(hasSelection);
        
        // Opdater titel med * hvis der er ændringer
        String baseTitle = "🌙 Krypteret Notes App - Dark Mode";
        if (hasUnsavedChanges) {
            setTitle(baseTitle + " *");
        } else {
            setTitle(baseTitle);
        }
    }
    
    /**
     * Håndterer lukning af applikationen
     */
    private void handleExit() {
        if (hasUnsavedChanges) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Du har ikke-gemte ændringer. Vil du gemme dem før du lukker?",
                "Ikke-gemte ændringer",
                JOptionPane.YES_NO_CANCEL_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                saveCurrentNote();
                System.exit(0);
            } else if (result == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            // CANCEL_OPTION - gør ingenting, bliv i programmet
        } else {
            System.exit(0);
        }
    }
}