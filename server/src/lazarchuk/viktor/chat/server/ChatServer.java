package lazarchuk.viktor.chat.server;

import lazarchuk.viktor.chat.network.TCPconnection;
import lazarchuk.viktor.chat.network.TCPconnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPconnectionListener {

    public static void main (String[] args)
    {
        new ChatServer();
    }

    private final ArrayList<TCPconnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running...");
        try(ServerSocket serverSocker = new ServerSocket(8189)) {
            while(true){
               try {
                   new TCPconnection(this, serverSocker.accept());
               }catch (IOException e){
                   System.out.println("TCPconnection exception" + e);
               }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPconnection tcpconnection) {
        connections.add(tcpconnection);
        sendToAllConnections("Client connected: " + tcpconnection);
    }

    @Override
    public synchronized void onReceiveString(TCPconnection tcpconnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPconnection tcpconnection) {
        connections.remove(tcpconnection);
        sendToAllConnections("Client disconnected: " + tcpconnection);
    }

    @Override
    public synchronized void onException(TCPconnection tcpconnection, Exception e) {
        System.out.println("TCPconnection exception :" + e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        final int cnt = connections.size();
        for(int index = 0; index < cnt;  index++)
            connections.get(index).sendString(value);
    }

}
