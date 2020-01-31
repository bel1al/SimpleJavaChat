package lazarchuk.viktor.chat.client;

import lazarchuk.viktor.chat.network.TCPconnection;
import lazarchuk.viktor.chat.network.TCPconnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.http.WebSocket;

public class ClientWindow extends JFrame implements ActionListener, TCPconnectionListener {

    private static final String IP_ADDR = "192.168.31.18";
    private static final int PORT = 8189;
    private static final int HEIGHT = 400;
    private static final int WIDTH = 600;

    public static void main (String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("bel1al");
    private final JTextField fieldInput = new JTextField();

    private TCPconnection connection;


    private ClientWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);

        add(log, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldNickName,BorderLayout.NORTH);
        add(fieldInput, BorderLayout.SOUTH);

        setVisible(true);
        try {
            connection = new TCPconnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String msg = fieldInput.getText();
        if(msg.equals(""))return;
        else {
            fieldInput.setText(null);
            connection.sendString(fieldNickName.getText() + ": " + msg);
        }
    }

    @Override
    public void onConnectionReady(TCPconnection tcpconnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPconnection tcpconnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPconnection tcpconnection) {
        printMsg("Connection close...");
    }

    @Override
    public void onException(TCPconnection tcpconnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength() );
            }
        });
    }
}
