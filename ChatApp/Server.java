
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Server extends JFrame {

    private ServerSocket server;
    private Socket socket;

    private BufferedReader br;
    private PrintWriter out;

    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();

    // Constructor
    public Server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            createGUI();
            handleEvents();
            startReading();
            startWriting();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in server");
        }
    }

    private void createGUI() {
        setTitle("Server Messenger");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set fonts
        java.awt.Font font = new java.awt.Font("Roboto", java.awt.Font.PLAIN, 20);
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setIcon(new ImageIcon("chat.png"));

        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        messageArea.setEditable(false);

        setLayout(new BorderLayout());
        add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        add(jScrollPane, BorderLayout.CENTER);
        add(messageInput, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleEvents() {
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String contentToSend = messageInput.getText();
                messageArea.append("Server: " + contentToSend + "\n");
                out.println(contentToSend);
                messageInput.setText("");
                messageInput.requestFocus();
            }
        });
    }

    public void startReading() {
        Runnable r1 = () -> {
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    if (msg.equals("exit")) {
                        JOptionPane.showMessageDialog(this, "Client terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Client: " + msg + "\n");
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
        System.out.println("Starting server");
        new Server();
    }
}








