import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
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
    
    // Dark Theme Farver - Opdaterede for bedre synlighed
    private static final Color DARK_BACKGROUND = new Color(32, 33, 36);
    private static final Color DARKER_BACKGROUND = new Color(24, 25, 26);
    private static final Color SIDEBAR_BACKGROUND = new Color(41, 42, 45);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255); // Helt hvid for bedre læsbarhed
    private static final Color TEXT_SECONDARY = new Color(180, 185, 190); // Lysere grå
    private static final Color ACCENT_BLUE = new Color(100, 150, 255); // Lysere blå
    private static final Color ACCENT_GREEN = new Color(80, 200, 120); // Lysere grøn
    private static final Color ACCENT_RED = new Color(255, 90, 90); // Lysere rød
    private static final Color BUTTON_HOVER = new Color(70, 75, 80);
    private static final Color INPUT_BACKGROUND = new Color(48, 49, 52);
    private static final Color BORDER_COLOR = new Color(95, 99, 104);
    
    // GUI komponenter
    private DefaultListModel<Note> listModel;
    private DefaultListModel<Note> filteredListModel;
    private JList<Note> notesList;
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton newNoteButton;
    private JButton exportButton;
    private JButton favoriteButton;
    private JLabel statusLabel;
    private JLabel noteCountLabel;
    private JLabel wordCountLabel;
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> sortComboBox;
    private JCheckBox favoriteFilter;
    
    // Nye features
    private Note currentNote;
    private boolean hasUnsavedChanges = false;
    private java.util.Set<String> categories = new java.util.HashSet<>();
    
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
        
        // Tilføj keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    /**
     * Opretter moderne sidebar med notes liste
     */
    private JPanel createModernSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 0));
        sidebar.setBackground(SIDEBAR_BACKGROUND);
        sidebar.setPreferredSize(new Dimension(350, 0));
        sidebar.setBorder(new LineBorder(BORDER_COLOR, 1, false));
        
        // Header sektion med titel, søgning og filtre
        JPanel headerSection = new JPanel();
        headerSection.setLayout(new BoxLayout(headerSection, BoxLayout.Y_AXIS));
        headerSection.setBackground(SIDEBAR_BACKGROUND);
        headerSection.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        // Titel og note count
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("[NOTER] Mine Noter");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        noteCountLabel = new JLabel("0 noter");
        noteCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noteCountLabel.setForeground(TEXT_SECONDARY);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(noteCountLabel, BorderLayout.EAST);
        headerSection.add(titlePanel);
        
        headerSection.add(Box.createVerticalStrut(15));
        
        // Søgefelt
        searchField = new JTextField();
        searchField.setBackground(INPUT_BACKGROUND);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(ACCENT_BLUE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel searchLabel = new JLabel("[SOG] Soeg i noter:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(TEXT_SECONDARY);
        searchLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        headerSection.add(searchLabel);
        headerSection.add(searchField);
        
        // Tilføj search event listener
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                SwingUtilities.invokeLater(() -> filterNotes()); 
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { 
                SwingUtilities.invokeLater(() -> filterNotes()); 
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { 
                SwingUtilities.invokeLater(() -> filterNotes()); 
            }
        });
        
        headerSection.add(Box.createVerticalStrut(10));
        
        // Kategori filter og favorit filter
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        filtersPanel.setOpaque(false);
        
        categoryComboBox = new JComboBox<>(new String[]{"Alle kategorier", "Generel", "Arbejde", "Personligt", "Ideer"});
        categoryComboBox.setBackground(INPUT_BACKGROUND);
        categoryComboBox.setForeground(TEXT_PRIMARY);
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        categoryComboBox.setPreferredSize(new Dimension(130, 28));
        categoryComboBox.addActionListener(evt -> filterNotes());
        
        // Sortering dropdown
        sortComboBox = new JComboBox<>(new String[]{"Nyeste først", "Ældste først", "A-Z", "Z-A", "Favoritter først", "Mest ordrig"});
        sortComboBox.setBackground(INPUT_BACKGROUND);
        sortComboBox.setForeground(TEXT_PRIMARY);
        sortComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sortComboBox.setPreferredSize(new Dimension(110, 28));
        sortComboBox.addActionListener(evt -> filterNotes());
        
        favoriteFilter = new JCheckBox("[*] Favoritter");
        favoriteFilter.setOpaque(false);
        favoriteFilter.setForeground(TEXT_SECONDARY);
        favoriteFilter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        favoriteFilter.addActionListener(evt -> filterNotes());
        
        filtersPanel.add(categoryComboBox);
        filtersPanel.add(Box.createHorizontalStrut(8));
        filtersPanel.add(sortComboBox);
        filtersPanel.add(Box.createHorizontalStrut(10));
        filtersPanel.add(favoriteFilter);
        
        headerSection.add(filtersPanel);
        
        sidebar.add(headerSection, BorderLayout.NORTH);
        
        // Notes liste med custom styling
        listModel = new DefaultListModel<>();
        notesList = new JList<>(listModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notesList.setBackground(SIDEBAR_BACKGROUND);
        notesList.setForeground(TEXT_PRIMARY);
        notesList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesList.setFixedCellHeight(60);
        notesList.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        // Avanceret custom cell renderer for notes med favorit status
        notesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                         boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                Note note = (Note) value;
                
                // Baggrundsfarver baseret på favorit status
                Color backgroundColor;
                if (isSelected) {
                    backgroundColor = note.isFavorite() ? new Color(255, 193, 7, 150) : ACCENT_BLUE;
                } else {
                    backgroundColor = note.isFavorite() ? new Color(255, 248, 220) : SIDEBAR_BACKGROUND;
                }
                
                setBackground(backgroundColor);
                setForeground(isSelected ? Color.WHITE : TEXT_PRIMARY);
                setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(note.isFavorite() ? new Color(255, 193, 7) : BORDER_COLOR, 1),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                
                // Avanceret HTML display med favorit status, kategori og statistik
                String favoriteIcon = note.isFavorite() ? "[*] " : "";
                String categoryInfo = note.getCategory().equals("Generel") ? "" : 
                    " <span style='color: " + (isSelected ? "#E0E0E0" : "#6C757D") + 
                    "; font-size: 9px;'>[" + note.getCategory() + "]</span>";
                
                String titleDisplay = favoriteIcon + (note.getTitle().isEmpty() ? "Untitled" : note.getTitle());
                String statsDisplay = note.getWordCount() + " ord";
                
                String displayText = "<html>" +
                    "<div style='font-weight: bold; color: " + 
                    (note.isFavorite() && !isSelected ? "#B8860B" : "inherit") + ";'>" + 
                    titleDisplay + categoryInfo + "</div>" +
                    "<div style='font-size: 10px; color: " + 
                    (isSelected ? "#E0E0E0" : "#9AA0A6") + ";'>" +
                    note.getFormattedDate() + " • " + statsDisplay + "</div>" +
                    "</html>";
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
        
        // Action buttons panel - nu med flere knapper
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(SIDEBAR_BACKGROUND);
        buttonsPanel.setBorder(new EmptyBorder(10, 15, 20, 15));
        
        // Første række knapper
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        row1.setOpaque(false);
        
        newNoteButton = createStyledButton("[+] Ny", ACCENT_GREEN);
        newNoteButton.addActionListener(evt -> createNewNote());
        newNoteButton.setPreferredSize(new Dimension(110, 35));
        
        deleteButton = createStyledButton("[X] Slet", ACCENT_RED);  
        deleteButton.addActionListener(evt -> deleteCurrentNote());
        deleteButton.setEnabled(false);
        deleteButton.setPreferredSize(new Dimension(110, 35));
        
        row1.add(newNoteButton);
        row1.add(deleteButton);
        
        // Anden række knapper  
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        row2.setOpaque(false);
        
        favoriteButton = createStyledButton("[*] Favorit", new Color(255, 193, 7));
        favoriteButton.addActionListener(evt -> toggleFavorite());
        favoriteButton.setEnabled(false);
        favoriteButton.setPreferredSize(new Dimension(110, 35));
        
        exportButton = createStyledButton("[>>] Export", new Color(108, 117, 125));
        exportButton.addActionListener(evt -> exportNote());  
        exportButton.setEnabled(false);
        exportButton.setPreferredSize(new Dimension(110, 35));
        
        row2.add(favoriteButton);
        row2.add(exportButton);
        
        buttonsPanel.add(row1);
        buttonsPanel.add(row2);
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
        
        JLabel titleLabel = new JLabel("[TITEL] Titel");
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
        
        // Indhold sektion med word count
        JPanel contentSection = new JPanel(new BorderLayout(0, 10));
        contentSection.setOpaque(false);
        contentSection.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Content header med label og word count
        JPanel contentHeader = new JPanel(new BorderLayout());
        contentHeader.setOpaque(false);
        
        JLabel contentLabel = new JLabel("[TEKST] Indhold");
        contentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contentLabel.setForeground(TEXT_SECONDARY);
        
        wordCountLabel = new JLabel("0 ord, 0 tegn");
        wordCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        wordCountLabel.setForeground(TEXT_SECONDARY);
        
        contentHeader.add(contentLabel, BorderLayout.WEST);
        contentHeader.add(wordCountLabel, BorderLayout.EAST);
        
        contentArea = new JTextArea();
        styleTextArea(contentArea);
        contentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                markAsChanged(); 
                SwingUtilities.invokeLater(() -> updateWordCount());
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { 
                markAsChanged(); 
                SwingUtilities.invokeLater(() -> updateWordCount());
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { 
                markAsChanged(); 
                SwingUtilities.invokeLater(() -> updateWordCount());
            }
        });
        
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBackground(INPUT_BACKGROUND);
        contentScrollPane.setBorder(new LineBorder(BORDER_COLOR, 2));
        contentScrollPane.getVerticalScrollBar().setBackground(INPUT_BACKGROUND);
        
        contentSection.add(contentHeader, BorderLayout.NORTH);
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
        
        JLabel appTitle = new JLabel("[LOCK] Krypteret Notes - Sikker & Privat");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(TEXT_PRIMARY);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        saveButton = createStyledButton("[SAVE] Gem", ACCENT_BLUE);
        saveButton.addActionListener(e -> saveCurrentNote());
        saveButton.setEnabled(false);
        
        rightPanel.add(saveButton);
        
        toolbar.add(appTitle, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);
        
        return toolbar;
    }
    
    /**
     * Opretter avanceret status bar med detaljeret information
     */
    private JPanel createModernStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(DARKER_BACKGROUND);
        statusBar.setBorder(new EmptyBorder(8, 15, 8, 15));
        
        // Venstre panel med status og note count
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        statusLabel = new JLabel("Klar til brug");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_SECONDARY);
        
        noteCountLabel = new JLabel(" • 0 noter");
        noteCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        noteCountLabel.setForeground(TEXT_SECONDARY);
        
        wordCountLabel = new JLabel("");
        wordCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        wordCountLabel.setForeground(TEXT_SECONDARY);
        
        leftPanel.add(statusLabel);
        leftPanel.add(noteCountLabel);
        leftPanel.add(wordCountLabel);
        
        // Højre panel med sikkerhed og shortcuts
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JLabel shortcutsLabel = new JLabel("Ctrl+N: Ny | Ctrl+S: Gem | Ctrl+F: Søg | F2: Favorit");
        shortcutsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        shortcutsLabel.setForeground(new Color(140, 145, 150));
        
        JLabel encryptionLabel = new JLabel("[SECURE] AES-256");
        encryptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        encryptionLabel.setForeground(ACCENT_GREEN);
        
        rightPanel.add(shortcutsLabel);
        rightPanel.add(encryptionLabel);
        
        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Opretter ultra-pæne knapper med gradients, skygger og animationer
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private float shadowOpacity = 0.3f;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int shadowOffset = isHovered ? 4 : 2;
                int borderRadius = 12;
                
                // Tegn skygge
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, shadowOpacity));
                g2d.setColor(Color.BLACK);
                g2d.fillRoundRect(shadowOffset, shadowOffset, width - shadowOffset, height - shadowOffset, borderRadius, borderRadius);
                
                // Reset alpha for hovedknap
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                
                // Gradient baggrund
                Color topColor = isHovered ? backgroundColor.brighter() : backgroundColor;
                Color bottomColor = isHovered ? backgroundColor : backgroundColor.darker();
                GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, width - shadowOffset, height - shadowOffset, borderRadius, borderRadius);
                
                // Glossy overlay effect
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                GradientPaint glossy = new GradientPaint(0, 0, Color.WHITE, 0, height/2, new Color(255, 255, 255, 0));
                g2d.setPaint(glossy);
                g2d.fillRoundRect(0, 0, width - shadowOffset, height/2, borderRadius, borderRadius);
                
                // Kant/border
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.setColor(backgroundColor.darker().darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, width - shadowOffset - 2, height - shadowOffset - 2, borderRadius, borderRadius);
                
                // Tegn tekst med skygge
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (width - shadowOffset - fm.stringWidth(getText())) / 2;
                int textY = (height - shadowOffset + fm.getAscent() - fm.getDescent()) / 2;
                
                // Tekst skygge
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), textX + 1, textY + 1);
                
                // Hovedtekst
                g2d.setColor(getForeground());
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                return new Dimension(size.width + 6, size.height + 6); // Plads til skygge
            }
        };
        
        // Sæt farver og styling
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(140, 42));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        
        // Avancerede hover-effekter med animationer
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    // Trigger hover state i custom paint
                    try {
                        java.lang.reflect.Field hoverField = button.getClass().getDeclaredField("isHovered");
                        hoverField.setAccessible(true);
                        hoverField.setBoolean(button, true);
                    } catch (Exception e) {
                        // Fallback hvis reflection fejler
                    }
                    button.repaint();
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    try {
                        java.lang.reflect.Field hoverField = button.getClass().getDeclaredField("isHovered");
                        hoverField.setAccessible(true);
                        hoverField.setBoolean(button, false);
                    } catch (Exception e) {
                        // Fallback hvis reflection fejler
                    }
                    button.repaint();
                }
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    // Pressed effect - mindre skygge
                    try {
                        java.lang.reflect.Field shadowField = button.getClass().getDeclaredField("shadowOpacity");
                        shadowField.setAccessible(true);
                        shadowField.setFloat(button, 0.6f);
                    } catch (Exception e) {
                        // Fallback
                    }
                    button.repaint();
                }
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    // Reset shadow
                    try {
                        java.lang.reflect.Field shadowField = button.getClass().getDeclaredField("shadowOpacity");
                        shadowField.setAccessible(true);
                        shadowField.setFloat(button, 0.3f);
                    } catch (Exception e) {
                        // Fallback
                    }
                    button.repaint();
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
        filterNotes(); // Opdater filteret liste
        notesList.setSelectedValue(newNote, true);
        
        titleField.requestFocus();
        titleField.selectAll();
        
        statusLabel.setText("Ny note oprettet");
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
            filterNotes(); // Brug filter i stedet for direkte liste update
            statusLabel.setText("Noter indlæst succesfuldt");
        } catch (Exception e) {
            statusLabel.setText("Fejl ved indlæsning: " + e.getMessage());
        }
    }
    
    /**
     * Opdaterer note tæller og statistik
     */
    private void updateNoteCount() {
        int totalNotes = notes.size();
        int displayedNotes = listModel.getSize();
        long favorites = notes.stream().filter(Note::isFavorite).count();
        
        // Opdater note count
        String noteText = " • " + displayedNotes;
        if (displayedNotes != totalNotes) {
            noteText += "/" + totalNotes;
        }
        noteText += " note" + (totalNotes != 1 ? "r" : "");
        if (favorites > 0) {
            noteText += " (" + favorites + " favoritter)";
        }
        noteCountLabel.setText(noteText);
        
        // Opdater word count hvis der er en valgt note
        if (currentNote != null) {
            wordCountLabel.setText(" • " + currentNote.getStats());
        } else {
            wordCountLabel.setText("");
        }
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
        
        // Nye knapper
        favoriteButton.setEnabled(hasSelection);
        exportButton.setEnabled(hasSelection);
        
        // Opdater favorit knap baseret på note status
        if (hasSelection) {
            favoriteButton.setText(currentNote.isFavorite() ? "[*] Favorit" : "[ ] Favorit");
            favoriteButton.setBackground(currentNote.isFavorite() ? 
                new Color(255, 193, 7) : new Color(108, 117, 125));
        }
        
        // Opdater word count
        updateWordCount();
        
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
    
    /**
     * Opdaterer word count display
     */
    private void updateWordCount() {
        if (currentNote != null) {
            String content = contentArea.getText();
            int words = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;
            int chars = content.length();
            wordCountLabel.setText(words + " ord, " + chars + " tegn");
        } else {
            wordCountLabel.setText("0 ord, 0 tegn");
        }
    }
    
    /**
     * Toggle favorit status for nuværende note
     */
    private void toggleFavorite() {
        if (currentNote != null) {
            currentNote.setFavorite(!currentNote.isFavorite());
            favoriteButton.setText(currentNote.isFavorite() ? "[*] Favorit" : "[ ] Favorit");
            favoriteButton.setBackground(currentNote.isFavorite() ? 
                new Color(255, 193, 7) : new Color(108, 117, 125));
            markAsChanged();
            
            // Opdater liste visning
            int selectedIndex = notesList.getSelectedIndex();
            if (selectedIndex >= 0) {
                listModel.setElementAt(currentNote, selectedIndex);
            }
        }
    }
    
    /**
     * Eksporter nuværende note til tekstfil
     */
    private void exportNote() {
        if (currentNote == null) return;
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Eksporter Note");
            fileChooser.setSelectedFile(new java.io.File(currentNote.getTitle() + ".txt"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                
                try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                    writer.println("Titel: " + currentNote.getTitle());
                    writer.println("Kategori: " + currentNote.getCategory());
                    writer.println("Oprettet: " + currentNote.getFormattedCreatedAt());
                    writer.println("Sidst ændret: " + currentNote.getFormattedLastModified());
                    writer.println("Favorit: " + (currentNote.isFavorite() ? "Ja" : "Nej"));
                    writer.println("Statistik: " + currentNote.getStats());
                    writer.println("\\n" + "=".repeat(50));
                    writer.println("\\n" + currentNote.getContent());
                }
                
                statusLabel.setText("Note eksporteret til: " + file.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Fejl ved eksport: " + e.getMessage(),
                "Eksport Fejl",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Filtrerer noter baseret på søgning, kategori og favorit filter
     */
    private void filterNotes() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        boolean showOnlyFavorites = favoriteFilter.isSelected();
        String sortOption = (String) sortComboBox.getSelectedItem();
        
        // Samle alle matchende noter
        java.util.List<Note> filteredNotes = new ArrayList<>();
        
        for (Note note : notes) {
            boolean matches = true;
            
            // Søg filter
            if (!searchText.isEmpty()) {
                boolean titleMatch = note.getTitle().toLowerCase().contains(searchText);
                boolean contentMatch = note.getContent().toLowerCase().contains(searchText);
                if (!titleMatch && !contentMatch) {
                    matches = false;
                }
            }
            
            // Kategori filter
            if (matches && !selectedCategory.equals("Alle kategorier")) {
                if (!note.getCategory().equals(selectedCategory)) {
                    matches = false;
                }
            }
            
            // Favorit filter
            if (matches && showOnlyFavorites) {
                if (!note.isFavorite()) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredNotes.add(note);
            }
        }
        
        // Sortér notes baseret på valgt sortering
        switch (sortOption) {
            case "Ældste først":
                filteredNotes.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                break;
            case "A-Z":
                filteredNotes.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "Z-A":
                filteredNotes.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
            case "Favoritter først":
                filteredNotes.sort((a, b) -> {
                    if (a.isFavorite() && !b.isFavorite()) return -1;
                    if (!a.isFavorite() && b.isFavorite()) return 1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                });
                break;
            case "Mest ordrig":
                filteredNotes.sort((a, b) -> Integer.compare(b.getWordCount(), a.getWordCount()));
                break;
            default: // "Nyeste først"
                filteredNotes.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                break;
        }
        
        // Opdater list model
        listModel.clear();
        for (Note note : filteredNotes) {
            listModel.addElement(note);
        }
        
        updateNoteCount();
    }
    
    /**
     * Sætter keyboard shortcuts op for hurtigere navigation
     */
    private void setupKeyboardShortcuts() {
        // Ctrl+N = Ny note
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlN, "newNote");
        getRootPane().getActionMap().put("newNote", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewNote();
            }
        });
        
        // Ctrl+S = Gem note
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlS, "saveNote");
        getRootPane().getActionMap().put("saveNote", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCurrentNote();
            }
        });
        
        // Ctrl+D = Slet note
        KeyStroke ctrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlD, "deleteNote");
        getRootPane().getActionMap().put("deleteNote", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCurrentNote();
            }
        });
        
        // Ctrl+F = Fokus på søgefelt
        KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlF, "focusSearch");
        getRootPane().getActionMap().put("focusSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.requestFocus();
                searchField.selectAll();
            }
        });
        
        // F2 = Toggle favorit
        KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f2, "toggleFavorite");
        getRootPane().getActionMap().put("toggleFavorite", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFavorite();
            }
        });
    }
}