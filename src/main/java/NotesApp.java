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
     * Viser password dialog til autentifikation eller oprettelse
     */
    private void showPasswordDialog(boolean isNewUser) {
        JDialog passwordDialog = new JDialog((Frame) null, "🔒 Krypteret Notes App", true);
        passwordDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        passwordDialog.setSize(500, isNewUser ? 350 : 280);
        passwordDialog.setLocationRelativeTo(null);
        passwordDialog.setResizable(false);
        
        // Hovedpanel med padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Top panel med ikon og titel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(248, 249, 250));
        
        // Sikkerhedsikon
        JLabel iconLabel = new JLabel("🔐", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        topPanel.add(iconLabel, BorderLayout.NORTH);
        
        // Titel
        String titleText = isNewUser ? "Opret Master Password" : "Velkommen Tilbage";
        JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 37, 41));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Beskrivelse
        String descText = isNewUser ? 
            "<html><div style='text-align: center; line-height: 1.4;'>Vælg et stærkt password til at kryptere dine noter.<br><span style='color: #6c757d; font-size: 12px;'>Minimum 6 tegn. Husk det godt - det kan ikke gendannes!</span></div></html>" :
            "<html><div style='text-align: center; color: #6c757d;'>Indtast dit master password for at få adgang til dine krypterede noter</div></html>";
        JLabel descLabel = new JLabel(descText, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        topPanel.add(descLabel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel med form felter
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Password felt
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(new Color(33, 37, 41));
        JPasswordField passwordField = new JPasswordField(22);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);
        
        // Bekræft password felt (kun for nye brugere)
        JLabel confirmLabel = null;
        JPasswordField confirmField = null;
        if (isNewUser) {
            confirmLabel = new JLabel("Bekræft Password:");
            confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            confirmLabel.setForeground(new Color(33, 37, 41));
            confirmField = new JPasswordField(22);
            confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            confirmField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            gbc.gridy = 2; gbc.insets = new Insets(15, 5, 8, 5);
            formPanel.add(confirmLabel, gbc);
            gbc.gridy = 3; gbc.insets = new Insets(8, 5, 8, 5);
            formPanel.add(confirmField, gbc);
        }
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Bottom panel med knapper
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setBackground(new Color(248, 249, 250));
        
        JButton okButton = new JButton(isNewUser ? "🔒 Opret Password" : "🔓 Log Ind");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(new Color(0, 123, 255));
        okButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton cancelButton = new JButton("❌ Annuller");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setForeground(new Color(108, 117, 125));
        cancelButton.setBackground(new Color(248, 249, 250));
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        passwordDialog.add(mainPanel);
        
        // Håndter vindue lukning
        passwordDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        
        // Hover effekter for knapper
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                okButton.setBackground(new Color(0, 86, 179));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                okButton.setBackground(new Color(0, 123, 255));
            }
        });
        
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                cancelButton.setBackground(new Color(233, 236, 239));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                cancelButton.setBackground(new Color(248, 249, 250));
            }
        });
        
        // Sæt focus på password felt
        passwordField.requestFocusInWindow();
        
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
        
        cancelButton.addActionListener(e -> System.exit(0));
        
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