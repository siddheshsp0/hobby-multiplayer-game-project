package com.highpixelstudio.networking;

import com.google.gson.Gson;
import com.highpixelstudio.elements.PlayerDTO;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketHandler extends WebSocketListener {
    private WebSocket webSocket;
    private Gson gson = new Gson();
    private OkHttpClient client;
    PlayerDTO[] otherPlayersHistory;
    public String sessionID = java.util.UUID.randomUUID().toString();


    public String getSessionID() {
        return sessionID;
    }

    public void connect(String url) {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newWebSocket(request, this); // This will call `onOpen` automatically
    }

    public void send(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing normally");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }

    public void sendPlayer(PlayerDTO player) {
        if (webSocket != null) {
            player.setSessionID(sessionID);
            String json = gson.toJson(player);
            webSocket.send(json);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        System.out.println("WebSocket connected");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // System.out.println("Received: " + text);

        // To deserialize if receiving list from server
        PlayerDTO[] receivedPlayers = gson.fromJson(text, PlayerDTO[].class);
        for(int i=0; i<receivedPlayers.length; i++) {
            System.out.println(receivedPlayers[i].getElementX());
        }
        if (receivedPlayers!=null) {
            otherPlayersHistory = receivedPlayers;
        }
    }

    public PlayerDTO[] getOtherPlayers() {
        return otherPlayersHistory;
    }

     @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.err.println("WebSocket error: " + t.getMessage());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        System.out.println("WebSocket closing: " + reason);
        webSocket.close(1000, null);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("WebSocket closed: " + reason);
    }
}
