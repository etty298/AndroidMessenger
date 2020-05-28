package com.example.skillboxcriptochat;

import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Server {
    //35.214.3.133:8881
    Map<Long,String> names = new ConcurrentHashMap<>();
    WebSocketClient client;
    private Consumer<Pair<String,String>> onMessageReceived;
    private Consumer<Integer> onUsersAmountUpdated;

    public Server(Consumer<Pair<String, String>> onMessageReceived,
                  Consumer<Integer> onUsersAmountUpdated)
    {
        this.onMessageReceived = onMessageReceived;
        this.onUsersAmountUpdated = onUsersAmountUpdated;
    }

    public void connect(){
        URI address;
        try {
            address = new URI("ws://35.214.3.133:8881");
        } catch (URISyntaxException e) {
            return;
        }

        client = new WebSocketClient(address) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("SERVER", "Connection to server is open.");
                String myName = Protocol.packName(new Protocol.UserName(MainActivity.myName));
                Log.i("SERVER", "Server is sending my name: " + myName);
                client.send(myName);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onMessage(String message) {
                Log.i("SERVER", "Got message from server: " + message);
                int type = Protocol.getType(message);
                if (type == Protocol.USER_STATUS){
                    userStatusChanged(message);
                }
                if (type == Protocol.MESSAGE){
                    try {
                        displayIncomingMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("SERVER", "Connection is closed");
            }

            @Override
            public void onError(Exception ex) {
                Log.i("SERVER", "ERROR occured" + ex.getMessage());
            }
        };
        client.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayIncomingMessage(String json) throws Exception {
        Protocol.Message m = Protocol.unpackMessage(json);
        String name = names.get(m.getSender());
        if (name == null){
            name = "UNKNOWN";
        }
        String text = m.getEncodedText();
        text = Crypto.decrypt(text);
        onMessageReceived.accept(
                new Pair<String, String>(name, text)
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void userStatusChanged(String json){
        Protocol.UserStatus s = Protocol.unpackStatus(json);
        Protocol.User user = s.getUser();
        if (s.isConnected()){
            names.put(user.getId(),user.getName());
        } else {
            names.remove(user.getId());
        }
        onUsersAmountUpdated.accept(names.size());
    }

    public void sendMessage(String message){
        if (client == null || !client.isOpen()){
            return;
        }
        try {
            message = Crypto.encrypt(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Protocol.Message m = new Protocol.Message(message);
        m.setReceiver(Protocol.GROUP_CHAT);
        String packedMessage = Protocol.packMessage(m);
        Log.i("SERVER", "Sending message: " + packedMessage);
        client.send(packedMessage);
    }

    public void sendName(String name) {
        Protocol.UserName userName = new Protocol.UserName(name);
        if (client != null && client.isOpen()) {
            client.send(Protocol.packName(userName));
        }
    }
}
