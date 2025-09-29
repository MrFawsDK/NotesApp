import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * NotesApp er hovedklassen for den krypterede notes applikation
 * Håndterer password autentifikation og starter GUI
 */
public class NotesApp {
    private CryptoManager cryptoManager;
    private NotesStorage notesStorage;
    
    public static void main(String[] args) {
        // Sæt Swing Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Brug standard Look and Feel hvis system L&F ikke er tilgængelig
        }
        
        SwingUtilities.invokeLater(() -> {
            new NotesApp().start();
        });
    }
    
    /**
     * Starter applikationen
     */
    public void start() {
        cryptoManager = new CryptoManager();
        notesStorage = new NotesStorage(cryptoManager);
        
        // Tjek om der findes eksisterende data
        if (notesStorage.hasExistingData()) {
            // Eksisterende bruger - bed om password
            showPasswordDialog(false);
        } else {
            // Ny bruger - opret password
            showPasswordDialog(true);
        }
    }
    
    /**
     * Viser password dialog til autentifikation eller oprettelse - SIMPEL VERSION
     */
    private void showPasswordDialog(boolean isNewUser) {
        JDialog passwordDialog = new JDialog((Frame) null, "Krypteret Notes App", true);
        passwordDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        passwordDialog.setSize(500, isNewUser ? 400 : 300);
        passwordDialog.setLocationRelativeTo(null);
        passwordDialog.setResizable(false);
        
        // Hovedpanel med gradient baggrund
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient baggrund fra lys blå til hvid
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(240, 248, 255), // Alice Blue
                    0, getHeight(), new Color(255, 255, 255) // White
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(13, 110, 253), 3),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Titel
        String titleText = isNewUser ? "🔐 Opret Master Password" : "🏠 Velkommen Tilbage!";
        JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(isNewUser ? new Color(25, 135, 84) : new Color(13, 110, 253));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        
        // Undertitel for velkomst besked
        if (!isNewUser) {
            JLabel subtitleLabel = new JLabel("Indtast dit password for at få adgang til dine notes", SwingConstants.CENTER);
            subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            subtitleLabel.setForeground(new Color(108, 117, 125));
            subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setOpaque(false); // Transparent for gradient
            titlePanel.add(titleLabel);
            titlePanel.add(subtitleLabel);
            mainPanel.add(titlePanel, BorderLayout.NORTH);
        } else {
            titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));
            mainPanel.add(titleLabel, BorderLayout.NORTH);
        }

        
        // Center panel med password felter
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false); // Transparent for at vise gradient
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        // Password felt 1 - ALTID synligt
        JLabel passwordLabel = new JLabel(isNewUser ? "🔑 Vælg dit sikre password:" : "🔑 Dit password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 15));
        passwordLabel.setForeground(new Color(52, 58, 64));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(passwordLabel);
        
        centerPanel.add(Box.createVerticalStrut(8));
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(320, 40));
        passwordField.setMaximumSize(new Dimension(320, 40));
        passwordField.setBackground(new Color(248, 249, 250));
        passwordField.setForeground(new Color(33, 37, 41));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(13, 110, 253), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        passwordField.setCaretColor(new Color(13, 110, 253));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(passwordField);
        
        // Bekræftelsesfelt kun for nye brugere
        JPasswordField confirmField = null;
        if (isNewUser) {
            centerPanel.add(Box.createVerticalStrut(15));
            
            JLabel confirmLabel = new JLabel("✅ Bekræft dit password:");
            confirmLabel.setFont(new Font("Arial", Font.BOLD, 15));
            confirmLabel.setForeground(new Color(52, 58, 64));
            confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(confirmLabel);
            
            centerPanel.add(Box.createVerticalStrut(8));
            
            confirmField = new JPasswordField(20);
            confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            confirmField.setPreferredSize(new Dimension(320, 40));
            confirmField.setMaximumSize(new Dimension(320, 40));
            confirmField.setBackground(new Color(248, 249, 250));
            confirmField.setForeground(new Color(33, 37, 41));
            confirmField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 135, 84), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
            confirmField.setCaretColor(new Color(25, 135, 84));
            confirmField.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(confirmField);
        }
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Knapper med moderne styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        // OK knap med gradient og ikoner
        JButton okButton = new JButton(isNewUser ? "🔐 Opret Password" : "🚀 Log Ind");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setBackground(isNewUser ? new Color(25, 135, 84) : new Color(13, 110, 253));
        okButton.setForeground(Color.WHITE);
        okButton.setPreferredSize(new Dimension(150, 40));
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Cancel knap
        JButton cancelButton = new JButton("❌ Annuller");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(130, 40));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        passwordDialog.add(mainPanel);
        
        // Window close handler
        passwordDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        
        // Action listeners
        final JPasswordField finalConfirmField = confirmField;
        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(passwordField.getPassword());
                
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(passwordDialog, 
                        "Password må ikke være tomt!", "Fejl", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (isNewUser) {
                    String confirmPassword = new String(finalConfirmField.getPassword());
                    if (!password.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(passwordDialog,
                            "Passwords matcher ikke!", "Fejl", JOptionPane.ERROR_MESSAGE);
                        finalConfirmField.setText("");
                        return;
                    }
                    
                    if (password.length() < 6) {
                        JOptionPane.showMessageDialog(passwordDialog,
                            "Password skal være mindst 6 tegn langt!", "Fejl", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                try {
                    if (isNewUser) {
                        // Opret ny bruger
                        byte[] salt = cryptoManager.generateSalt();
                        cryptoManager.generateKeyFromPassword(password, salt);
                        notesStorage.saveSalt(salt);
                    } else {
                        // Login eksisterende bruger
                        byte[] salt = notesStorage.loadSalt();
                        if (salt == null) {
                            throw new Exception("Salt fil ikke fundet");
                        }
                        cryptoManager.generateKeyFromPassword(password, salt);
                        
                        // Test om password er korrekt ved at prøve at indlæse data
                        try {
                            notesStorage.loadNotes();
                        } catch (Exception decryptException) {
                            throw new Exception("Forkert password eller beskadigede data");
                        }
                    }
                    
                    // Clear password felter
                    passwordField.setText("");
                    if (finalConfirmField != null) {
                        finalConfirmField.setText("");
                    }
                    
                    passwordDialog.dispose();
                    startMainApplication();
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(passwordDialog,
                        "Fejl: " + ex.getMessage(), "Autentifikationsfejl", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    if (finalConfirmField != null) {
                        finalConfirmField.setText("");
                    }
                }
            }
        };
        
        okButton.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
        if (confirmField != null) {
            confirmField.addActionListener(loginAction);
        }
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Sæt focus på password felt
        passwordField.requestFocusInWindow();
        
        passwordDialog.setVisible(true);
    }
    
    /**
     * Starter hovedapplikationen efter succesfuld autentifikation
     */
    private void startMainApplication() {
        SwingUtilities.invokeLater(() -> {
            NotesAppGUI mainWindow = new NotesAppGUI(cryptoManager, notesStorage);
            mainWindow.setVisible(true);
        });
    }
}