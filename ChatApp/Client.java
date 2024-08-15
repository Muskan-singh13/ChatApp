

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Client extends JFrame {

    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    // Declare components
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    // Constructor
    public Client() {
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1", 7777);

            System.out.println("Connection done.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            createGUI();
            handleEvents();
            startReading();
            // startWriting(); // Uncomment if you want to use the console for sending messages

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // No implementation needed
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // No implementation needed
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("You have pressed enter button");

                    String contentToSend = messageInput.getText();
                    messageArea.append("Me : " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    private void createGUI() {
        setTitle("Client Messenger");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set fonts
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        //add image

        heading.setIcon(new ImageIcon("chat.png"));

        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        messageArea.setEditable(false);

        // Frame layout
        setLayout(new BorderLayout());
        add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        add(jScrollPane, BorderLayout.CENTER);
        add(messageInput, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started....");
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    if (msg.equals("exit")) {
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Server : " + msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer Started");
            try {
                while (!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Connection closed");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is client");
        new Client();
    }
}

