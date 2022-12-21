package view;

import controller.GameController;
import controller.NetController;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示游戏窗体，窗体上包含：
 * 1 Chessboard: 棋盘
 * 2 JLabel: 标签
 * 3 JButton： 按钮
 */
public class ChessGameFrame extends JFrame {
    private final int WIDTH;
    private final int HEIGHT;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;
    private static JLabel statusLabel;
    private static JLabel statusScore;
    private static Chessboard chessboard;
    public static JButton reButton;
    private static JButton CheatButton;
    private static JButton NetButton;
    private static NetController netWork;
    private static Clip clip = null;

    public ChessGameFrame(int width, int height) {
        setTitle("2022 CS109 Project Demo"); // 设置标题
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CHESSBOARD_SIZE = HEIGHT * 4 / 5;

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        addChessboard();
        addLabel();
        addRestartButton();
        addLoadButton();
        addSaveButton();
        addScore();
        addCheatButton();
        initNet();
        addNetButton();
        addBackMusicButton();
    }

    private void initNet() {
        try {
            netWork = new NetController(chessboard);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 在游戏窗体中添加棋盘
     */
    private void addChessboard() {
        chessboard = new Chessboard(CHESSBOARD_SIZE / 2, CHESSBOARD_SIZE);
        gameController = new GameController(chessboard);
        chessboard.setLocation(HEIGHT / 10, HEIGHT / 10);
        add(chessboard);
    }

    /**
     * 在游戏窗体中添加标签
     */
    private void addLabel() {
        statusLabel = new JLabel("Choose any one");
        statusLabel.setLocation(WIDTH * 3 / 5, HEIGHT / 10);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(statusLabel);
    }

    /**
     * 在游戏窗体中添加得分
     */
    private void addScore() {
        statusScore = new JLabel(String.format("Black 0 : Red 0"));
        statusScore.setLocation(WIDTH * 3 / 5, HEIGHT / 10 + 360);
        statusScore.setSize(300, 60);
        statusScore.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(statusScore);
    }

    public static JLabel getStatusLabel() {
        return statusLabel;
    }

    public static JLabel getStatusScore() {
        return statusScore;
    }

    /**
     * 在游戏窗体中增加一个按钮，如果按下的话就会重新开始!
     */

    private void addRestartButton() {
        reButton = new JButton("You can restart here!!!");
        reButton.addActionListener((e) -> {
            JOptionPane.showMessageDialog(this, "restart");
            remove(chessboard);
            remove(statusScore);
            remove(statusLabel);
            addChessboard();
            addScore();
            addLabel();
            repaint();
        });
        new Thread(() -> {
            while (true) {
                if(chessboard.RedSum>60||chessboard.BlackSum>60){
                    JOptionPane.showMessageDialog(this, "restart");
                remove(chessboard);
                remove(statusScore);
                remove(statusLabel);
                addChessboard();
                addScore();
                addLabel();
                repaint();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }).start();
        reButton.setLocation(WIDTH * 3 / 5, HEIGHT / 10 + 120);
        reButton.setSize(280, 60);
        reButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(reButton);
    }

    private void addLoadButton() {
        JButton button = new JButton("Load");
        button.setLocation(WIDTH * 3 / 5, HEIGHT / 10 + 240);
        button.setSize(180, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        button.setBackground(Color.LIGHT_GRAY);
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click load");
            String path = JOptionPane.showInputDialog(this, "Input Path here");
            int mid = gameController.loadGameFromFile(path);
            if (mid != 0) {
                JOptionPane.showMessageDialog(this, "error code is " + mid);
            }
        });

    }

    private void addSaveButton() {
        JButton button = new JButton("Save");
        button.setLocation(WIDTH * 3 / 5, HEIGHT / 10 + 480);
        button.setSize(180, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        button.setBackground(Color.LIGHT_GRAY);
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click save");
            String path = JOptionPane.showInputDialog(this, "Output Path here");
            gameController.saveGameToFile(path);
        });

    }

    private void addCheatButton() {
        CheatButton = new JButton("Cheating");
        CheatButton.setLocation(WIDTH * 3 / 5, HEIGHT / 10 + 580);
        CheatButton.setSize(120, 40);
        CheatButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        CheatButton.setBackground(Color.LIGHT_GRAY);
        add(CheatButton);

        CheatButton.addActionListener(e -> {
            if (netWork.outputStream == null) {
                if (Chessboard.cheatMode) {
                    Chessboard.cheatMode = false;
                    CheatButton.setText("Cheating");
                } else {
                    Chessboard.cheatMode = true;
                    CheatButton.setText("Normal");
                }
            } else {
                JOptionPane.showMessageDialog(this, "you can not cheat when at network model!!");
            }
        });
    }

    class RoundBorder implements Border {
        private Color color;

        public RoundBorder(Color color) {// 有参数的构造方法
            this.color = color;
        }

        public RoundBorder() {// 无参构造方法
            this.color = Color.WHITE;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        // 实现Border（父类）方法
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width,
                int height) {
            BasicStroke stokeLine = new BasicStroke(13);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(color);
            g2.setStroke(stokeLine);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawOval(0, 0, 30, 30);
            // g.drawRoundRect(0, 0, c.getWidth(), c.getHeight(), 100, 100);
        }
    }

    class CircleButton extends JButton {
        public CircleButton(String label, ImageIcon pic) {
            super(pic);
        }
    }

    private void addBackMusicButton() {
        try {
            // 创建相当于音乐播放器的对象
            clip = AudioSystem.getClip();
            // 将传入的文件转成可播放的文件
            AudioInputStream audioInput = AudioSystem
                    .getAudioInputStream(new File("DarkChess/src/video/backmusic.wav"));
            // 播放器打开这个文件
            clip.open(audioInput);
            // 循环播放
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println("filed!!!");
        }
        var pic = new ImageIcon("Darkchess/src/pic/musicbutton.png");
        Image image = pic.getImage(); // transform it
        Image newimg = image.getScaledInstance(29, 29, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        pic = new ImageIcon(newimg);
        JButton music = new CircleButton("Music", pic);
        music.setLocation(WIDTH * 4 / 5 + 80, 25);
        music.setSize(50, 50);
        music.setFont(new Font("Rockwell", Font.BOLD, 20));
        music.setBackground(Color.LIGHT_GRAY);
        music.setBorder(new RoundBorder());
        music.setBounds(WIDTH * 4 / 5 + 80, 25, 30, 30);
        add(music);

        music.addActionListener(e -> {
            if (clip.isActive()) {
                clip.stop();
            } else {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        });
    }

    private void addNetButton() {
        NetButton = new JButton("Con");
        NetButton.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 580);
        NetButton.setSize(120, 40);
        NetButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        NetButton.setBackground(Color.LIGHT_GRAY);
        add(NetButton);

        NetButton.addActionListener(e -> {
            String path = JOptionPane.showInputDialog(this, "input where?");
            try {
                netWork.connetOther(path);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
    }

    public static void OnceChange() throws IOException {
        netWork.change();
    }
}
