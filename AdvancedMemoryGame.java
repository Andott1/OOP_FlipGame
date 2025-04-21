import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

public class AdvancedMemoryGame extends JFrame {
    private final JButton[][] buttons = new JButton[4][4];
    private final String[][] letters = new String[4][4];
    private final boolean[][] matched = new boolean[4][4];
    private JButton first = null, second = null;
    private boolean isChecking = false;
    private int tries = 0;

    private final JLabel triesLabel = new JLabel("Tries: 0");
    private final JButton restartButton = new JButton("Restart");
    private JComboBox<String> themeComboBox;

    private String currentTheme = "Fruits";  // Default theme
    private final String imagesPathPrefix = "Flip Game Task/Assets/Images/";
    private String setDirectoryPath;

    public AdvancedMemoryGame() {
        setWorkingDirectoryToDesktop();

        setTitle("Memory Match Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * 0.60);
        int height = (int) (screenSize.getHeight() * 0.75);
        setSize(width, height);

        // === GRID PANEL (LEFT) ===
        JPanel gridPanel = new JPanel(new GridLayout(4, 4));
        int gridWidth = (int) (width * 0.60);
        gridPanel.setPreferredSize(new Dimension(gridWidth, height));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gridPanel.setBackground(Color.WHITE); // Left panel (card grid)


        // === INFO PANEL (RIGHT) ===
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        int infoWidth =  width - (int) (gridWidth * 1.30);
        infoPanel.setPreferredSize(new Dimension(infoWidth, height));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoPanel.setBackground(Color.WHITE); // Right panel (info area)


        JLabel titleLabel = new JLabel("<html><h2>MEMORY MATCHING GAME</h2></html>");
        JLabel instructions = new JLabel("<html><p>Flip cards to find matching pairs.<br>Match all to win!</p></html>");
        // Center align all components in the infoPanel
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructions.setHorizontalAlignment(SwingConstants.CENTER);
        triesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Vertical alignment to the center as well
        titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        instructions.setAlignmentY(Component.CENTER_ALIGNMENT);
        triesLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        themeComboBox = new JComboBox<>(new String[]{"Fruits", "Garden", "Easter Egg", "Toys", "Vehicles"});
        themeComboBox.setMaximumSize(new Dimension(125, 25));
        themeComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeComboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        themeComboBox.addActionListener(e -> {
        currentTheme = (String) themeComboBox.getSelectedItem();
        startGame(gridPanel);
        });

        restartButton.addActionListener(e -> startGame(gridPanel));

        // Add components to info panel
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(25));
        infoPanel.add(instructions);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(triesLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(new JLabel("Select Card Theme:"));
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(themeComboBox);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(restartButton);

        // Add panels to frame
        add(gridPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);

        startGame(gridPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setWorkingDirectoryToDesktop() {
        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + "\\Desktop";
        File desktopDir = new File(desktopPath);
        if (desktopDir.exists() && desktopDir.isDirectory()) {
            System.setProperty("user.dir", desktopPath);
            setDirectoryPath = desktopPath + "\\";
            System.out.println("Working directory set to: " + System.getProperty("user.dir"));
        } else {
            System.out.println("Desktop folder not found.");
        }
    }

    private void startGame(JPanel grid) {
        List<String> pairList = new ArrayList<>();
        for (char c = 'A'; c < 'A' + 8; c++) {
            pairList.add(String.valueOf(c));
            pairList.add(String.valueOf(c));
        }
        Collections.shuffle(pairList);

        first = second = null;
        isChecking = false;
        tries = 0;
        triesLabel.setText("Number of Tries: 0");
        grid.removeAll();

        Iterator<String> it = pairList.iterator();
        for (int i = 0; i < 4; i++) {
            Arrays.fill(matched[i], false);
            for (int j = 0; j < 4; j++) {
                String letter = it.next();
                letters[i][j] = letter;

                JButton btn = new JButton("");
                btn.setPreferredSize(new Dimension(80, 80));  // Can scale based on screen if needed
                btn.setFont(btn.getFont().deriveFont(Font.BOLD, 24f)); // Allows dynamic font tweaks later if needed
                buttons[i][j] = btn;

                // === Set the BACK image ===
                String backImagePath = setDirectoryPath + imagesPathPrefix + "Back Card" + "/" + "Back Card Designs_" + currentTheme + ".png";
                File backFile = new File(backImagePath);
                if (backFile.exists()) {
                    SwingUtilities.invokeLater(() -> {
                        ImageIcon icon = new ImageIcon(backImagePath);
                        Image img = icon.getImage();
                        Image scaledImg = img.getScaledInstance(btn.getWidth(), btn.getHeight(), Image.SCALE_SMOOTH);
                        btn.setIcon(new ImageIcon(scaledImg));
                        btn.setText("");
                    });
                } else {
                    btn.setText("");
                }

                int row = i, col = j;
                btn.addActionListener(e -> handleClick(row, col));
                grid.add(btn);
            }
        }

        revalidate();
        repaint();
    }

    private void handleClick(int row, int col) {
        if (isChecking || matched[row][col]) return;

        JButton btn = buttons[row][col];
        String frontImagePath = setDirectoryPath + imagesPathPrefix + "Front Card" + "/" + currentTheme + "/" + "Image_" + currentTheme + " " + letters[row][col] + ".png";
        File front = new File(frontImagePath);
        if (front.exists()) {
            ImageIcon icon = new ImageIcon(frontImagePath);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(btn.getWidth(), btn.getHeight(), Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaledImg));
        } else {
            btn.setText(letters[row][col]);
        }

        if (first == null) {
            first = btn;
        } else if (second == null && btn != first) {
            second = btn;
            isChecking = true;
            tries++;
            triesLabel.setText("Number of Tries: " + tries);

            int r1 = getRow(first), c1 = getCol(first);
            int r2 = row, c2 = col;

            if (letters[r1][c1].equals(letters[r2][c2])) {
                matched[r1][c1] = matched[r2][c2] = true;
                resetSelection();
                checkWin();
            } else {
                new Timer(1000, e -> {
                    // Get back image
                    String backImagePath = setDirectoryPath + imagesPathPrefix + "Back Card" + "/" + "Back Card Designs_" + currentTheme + ".png";
                    File back = new File(backImagePath);
                    if (back.exists()) {
                        ImageIcon icon = new ImageIcon(backImagePath);
                        Image img = icon.getImage();
    
                        Image scaledFirst = img.getScaledInstance(first.getWidth(), first.getHeight(), Image.SCALE_SMOOTH);
                        Image scaledSecond = img.getScaledInstance(second.getWidth(), second.getHeight(), Image.SCALE_SMOOTH);
    
                        first.setIcon(new ImageIcon(scaledFirst));
                        second.setIcon(new ImageIcon(scaledSecond));
                    } else {
                        first.setText("");
                        second.setText("");
                        first.setIcon(null);
                        second.setIcon(null);
                    }
    
                    resetSelection();
                    ((Timer) e.getSource()).stop();
                }).start();
            }
        }
    }

    private void resetSelection() {
        first = second = null;
        isChecking = false;
    }

    private int getRow(JButton btn) {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (buttons[i][j] == btn) return i;
        return -1;
    }

    private int getCol(JButton btn) {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (buttons[i][j] == btn) return j;
        return -1;
    }

    private void checkWin() {
        for (boolean[] row : matched)
            for (boolean b : row)
                if (!b) return;

        JOptionPane.showMessageDialog(this, "You matched all pairs in " + tries + " tries!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdvancedMemoryGame());
    }
}
