package server;

import java.io.*;
import java.net.Socket;

public class ChatParticipant {
    protected final Socket socket;
    protected final BufferedReader bufferedReader;
    protected final BufferedWriter bufferedWriter;

    public ChatParticipant(Socket socket) throws IOException {
        this.socket = socket;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public boolean isConnected() {
        return !socket.isClosed();
    }

    public boolean isReadyToBeRead() throws IOException {
        return bufferedReader.ready();
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    @Override
    public String toString() {
        return socket.toString();
    }
}

