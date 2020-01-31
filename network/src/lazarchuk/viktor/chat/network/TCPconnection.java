package lazarchuk.viktor.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPconnection {

    private final TCPconnectionListener evenListener;
    private final Socket socket;
    private final Thread rxTread;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPconnection(TCPconnectionListener evenListener, String ipAddr, int port) throws IOException{
        this(evenListener, new Socket(ipAddr,port));
    }

    public TCPconnection(TCPconnectionListener evenListener,Socket socket) throws IOException {
        {
            this.evenListener = evenListener;
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
            rxTread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        evenListener.onConnectionReady(TCPconnection.this);
                        while(!rxTread.isInterrupted()){
                            evenListener.onReceiveString(TCPconnection.this, in.readLine());
                        }
                    }catch (IOException e){
                        evenListener.onException(TCPconnection.this , e);
                    }finally {
                        evenListener.onDisconnect(TCPconnection.this);
                    }
                }
            });
            rxTread.start();
        }
    }

    public synchronized void sendString(String value)
    {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            evenListener.onException(TCPconnection.this , e);
            disconnect();
        }
    }
    public synchronized void disconnect()
    {
        rxTread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            evenListener.onException(TCPconnection.this , e);
        }
    }

    @Override
    public String toString()
    {
        return "TCPconnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
