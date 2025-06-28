package com.highpixelstudio.survivalpixel2d.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GameWebSocketHandler extends TextWebSocketHandler {
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> latestPayloads = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public GameWebSocketHandler() {
        startBroadcastLoop();
    }

    private void startBroadcastLoop() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                broadcastToAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 50, TimeUnit.MILLISECONDS); // 20 FPS
    }

    private void broadcastToAll() throws Exception {
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            String sessionId = entry.getKey();
            WebSocketSession s = entry.getValue();

            if (!s.isOpen()) continue;

            StringBuilder jsonArrayBuilder = new StringBuilder("[");
            int count = 0;
            for (Map.Entry<String, String> payloadEntry : latestPayloads.entrySet()) {
                String otherId = payloadEntry.getKey();
                if (otherId.equals(sessionId)) continue;
                if (count > 0) jsonArrayBuilder.append(",");
                jsonArrayBuilder.append(payloadEntry.getValue());
                count++;
            }
            jsonArrayBuilder.append("]");

            synchronized (s) {
                s.sendMessage(new TextMessage(jsonArrayBuilder.toString()));
                System.out.println("Sending to: "+sessionId+". Message: "+jsonArrayBuilder.toString());
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("Player connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String senderId = session.getId();
        JsonObject obj = gson.fromJson(message.getPayload(), JsonObject.class);
        obj.addProperty("sessionID", senderId);
        String updatedPayload = gson.toJson(obj);
        latestPayloads.put(senderId, updatedPayload);
        // System.out.println("Received from " + senderId + ": " + updatedPayload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        latestPayloads.remove(session.getId());
        System.out.println("Player disconnected: " + session.getId());
    }
}
