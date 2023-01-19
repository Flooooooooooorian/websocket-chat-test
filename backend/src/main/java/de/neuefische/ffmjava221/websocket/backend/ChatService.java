package de.neuefische.ffmjava221.websocket.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Set;

@Service
public class ChatService extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new HashSet<>();

    private final ObjectMapper objectMapper;

    public ChatService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.add(session);

        System.out.println(session.getPrincipal().getName() + " Verbindung hergestellt!");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        System.out.println("Nachricht empfangen: " + message.getPayload());

        for (WebSocketSession s : sessions) {
            System.out.println(s.getPrincipal());
            if (s.getPrincipal() != null) {

                if (s.getPrincipal().getName().equals(chatMessage.to())) {
                    try {
                        s.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session);

        System.out.println("Verbindung abgebrochen!");
    }
}
