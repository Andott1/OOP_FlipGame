import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;

import java.util.Collections;

public class BasicMemoryGame extends JFrame {
    private final JButton[][] buttons = new JButton[4][4];
    private final String[][] letters = new String[4][4];
    private final boolean[][] matched = new boolean[4][4];
    private JButton first = null, second = null;
    private boolean isChecking = false;
    private int tries = 0;

    private final JLabel triesLabel = new JLabel("Tries: 0");
    private final JButton restartButton = new JButton("Restart");

    public BasicMemoryGame() {
        setTitle("Memory Match Game");
        setSize(420, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with tries and restart
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(triesLabel, BorderLayout.WEST);
        topPanel.add(restartButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Grid of buttons
        JPanel gridPanel = new JPanel(new GridLayout(4, 4));
        add(gridPanel, BorderLayout.CENTER);

        // Init game
        restartButton.addActionListener(e -> startGame(gridPanel));
        startGame(gridPanel);
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
        triesLabel.setText("Tries: 0");
        grid.removeAll();

        Iterator<String> it = pairList.iterator();
        for (int i = 0; i < 4; i++) {
            Arrays.fill(matched[i], false);
            for (int j = 0; j < 4; j++) {
                String letter = it.next();
                letters[i][j] = letter;
                JButton btn = new JButton("");
                btn.setFont(new Font("Arial", Font.BOLD, 24));
                buttons[i][j] = btn;
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
        btn.setText(letters[row][col]);

        if (first == null) {
            first = btn;
        } else if (second == null && btn != first) {
            second = btn;
            isChecking = true;
            tries++;
            triesLabel.setText("Tries: " + tries);

            int r1 = getRow(first), c1 = getCol(first);
            int r2 = row, c2 = col;

            if (letters[r1][c1].equals(letters[r2][c2])) {
                matched[r1][c1] = matched[r2][c2] = true;
                resetSelection();
                checkWin();
            } else {
                new Timer(1000, e -> {
                    first.setText("");
                    second.setText("");
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

        JOptionPane.showMessageDialog(this, "Congratulations! You matched all pairs in " + tries + " tries!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BasicMemoryGame().setVisible(true));
    }
}
