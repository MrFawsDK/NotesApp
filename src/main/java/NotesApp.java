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
        JDialog passwordDialog = new JDialog((Frame) null, "Krypteret Notes App", true);
        passwordDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        passwordDialog.setSize(650, isNewUser ? 550 : 400);
        passwordDialog.setLocationRelativeTo(null);
        passwordDialog.setResizable(false);
        
        // Hovedpanel med gradient baggrund
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 242, 247), 
                                                          0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Top panel med ikon og titel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        // Sikkerhedsikon med cirkel baggrund
        JPanel iconPanel = new JPanel(new FlowLayout());
        iconPanel.setOpaque(false);
        JLabel iconWrapper = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 123, 255, 30));
                g2d.fillOval(5, 5, 60, 60);
                g2d.setColor(new Color(0, 123, 255));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(5, 5, 60, 60);
            }
        };
        iconWrapper.setPreferredSize(new Dimension(70, 70));
        
        JLabel iconLabel = new JLabel("[*]", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        iconLabel.setBounds(0, 0, 70, 70);
        iconWrapper.add(iconLabel);
        
        iconPanel.add(iconWrapper);
        topPanel.add(iconPanel, BorderLayout.NORTH);
        
        // Titel
        String titleText = isNewUser ? "Opret Master Password" : "Velkommen Tilbage";
        JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Beskrivelse
        String descText = isNewUser ? 
            "<html><div style='text-align: center; line-height: 1.6; font-size: 14px;'>Vælg et stærkt password til at kryptere dine noter<br><span style='color: #dc3545; font-weight: bold;'>⚠️ Minimum 6 tegn - Kan IKKE gendannes!</span></div></html>" :
            "<html><div style='text-align: center; color: #6c757d; font-size: 14px;'>Indtast dit master password for at få adgang til dine krypterede noter</div></html>";
        JLabel descLabel = new JLabel(descText, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));
        topPanel.add(descLabel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel med form felter - MEGET synlige
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setOpaque(true);
        formPanel.setBackground(new Color(248, 249, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Container for password felter
        JPanel fieldsContainer = new JPanel();
        fieldsContainer.setLayout(new BoxLayout(fieldsContainer, BoxLayout.Y_AXIS));
        fieldsContainer.setOpaque(true);
        fieldsContainer.setBackground(Color.WHITE);
        fieldsContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // FØRSTE password felt - ALTID synlig for alle brugere  
        JLabel password1Label = new JLabel(isNewUser ? "Vælg Master Password:" : "Indtast dit Master Password:");
        password1Label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        password1Label.setForeground(new Color(33, 37, 41));
        password1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsContainer.add(password1Label);
        
        fieldsContainer.add(Box.createVerticalStrut(12));
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(350, 50));
        passwordField.setMaximumSize(new Dimension(350, 50));
        passwordField.setMinimumSize(new Dimension(350, 50));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 123, 255), 3),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(new Color(33, 37, 41));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsContainer.add(passwordField);
        
        // ANDET password felt - kun for nye brugere
        JPasswordField confirmField = null;
        if (isNewUser) {
            fieldsContainer.add(Box.createVerticalStrut(25));
            
            JLabel password2Label = new JLabel("Bekræft Master Password:");
            password2Label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            password2Label.setForeground(new Color(33, 37, 41));
            password2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
            fieldsContainer.add(password2Label);
            
            fieldsContainer.add(Box.createVerticalStrut(12));
            
            confirmField = new JPasswordField(20);
            confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            confirmField.setPreferredSize(new Dimension(350, 50));
            confirmField.setMaximumSize(new Dimension(350, 50));
            confirmField.setMinimumSize(new Dimension(350, 50));
            confirmField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 3),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
            confirmField.setBackground(Color.WHITE);
            confirmField.setForeground(new Color(33, 37, 41));
            confirmField.setAlignmentX(Component.CENTER_ALIGNMENT);
            fieldsContainer.add(confirmField);
            
            fieldsContainer.add(Box.createVerticalStrut(15));
            
            JLabel helpLabel = new JLabel("Indtast samme password i begge felter");
            helpLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            helpLabel.setForeground(new Color(220, 53, 69));
            helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            fieldsContainer.add(helpLabel);
        }
        
        // Tilføj felterne til formPanel og formPanel til mainPanel
        formPanel.add(fieldsContainer, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Bottom panel med synlige knapper
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setOpaque(false);
        
        // Opret/Login knap - meget synlig
        JButton okButton = createStyledButton(
            isNewUser ? "Opret Password" : "Log Ind", 
            new Color(40, 167, 69), // Grøn baggrund
            Color.WHITE,
            true
        );
        
        // Annuller knap - synlig men sekundær
        JButton cancelButton = createStyledButton(
            "Annuller",
            new Color(220, 53, 69), // Rød baggrund  
            Color.WHITE,
            false
        );
        
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
    
    /**
     * Opretter en flot container til input felter med label
     */
    private JPanel createInputContainer(String labelText) {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        container.setPreferredSize(new Dimension(450, 80)); // Fast størrelse
        container.setMinimumSize(new Dimension(450, 80));
        container.setMaximumSize(new Dimension(450, 80));
        
        // Label med ikon
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(52, 58, 64));
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        container.add(label, BorderLayout.NORTH);
        
        return container;
    }
    
    /**
     * Opretter et flot stylet password felt
     */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Baggrund
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Border
                if (hasFocus()) {
                    g2d.setColor(new Color(0, 123, 255));
                    g2d.setStroke(new BasicStroke(2));
                } else {
                    g2d.setColor(new Color(206, 212, 218));
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                
                // Skygge effekt
                if (!hasFocus()) {
                    g2d.setColor(new Color(0, 0, 0, 10));
                    g2d.fillRoundRect(1, 2, getWidth()-2, getHeight()-2, 8, 8);
                }
                
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Ingen standard border - vi tegner vores egen
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(440, 45)); // Bredere felt
        field.setMinimumSize(new Dimension(440, 45));
        field.setMaximumSize(new Dimension(440, 45));
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(33, 37, 41));
        field.setCaretColor(new Color(0, 123, 255));
        field.setOpaque(false);
        
        // Focus listener til repaint ved focus ændring
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.repaint();
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.repaint();
            }
        });
        
        return field;
    }
    
    /**
     * Opretter en flot stylet knap med hover effekter
     */
    private JButton createStyledButton(String text, Color bgColor, Color textColor, boolean isPrimary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Baggrund med gradient
                Color startColor = getBackground();
                Color endColor = getBackground().darker();
                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Tekst
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
                
                // Skygge/border
                if (isPrimary && hasFocus()) {
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 12, 12);
                }
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setPreferredSize(new Dimension(160, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effekter
        Color originalBg = bgColor;
        Color hoverBg = isPrimary ? bgColor.brighter() : bgColor.darker();
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
                button.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg);
                button.repaint();
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg.darker());
                button.repaint();
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
                button.repaint();
            }
        });
        
        return button;
    }
}