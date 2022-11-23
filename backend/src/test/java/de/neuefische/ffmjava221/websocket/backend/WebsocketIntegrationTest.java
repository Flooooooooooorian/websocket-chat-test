package de.neuefische.ffmjava221.websocket.backend;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebsocketIntegrationTest {

    @LocalServerPort
    private String serverPort;

    @Test
    void test() throws Exception {
        TextWebSocketHandler handler = mock(TextWebSocketHandler.class);

        doAnswer(invocation -> {
            invocation.getArgument(0, WebSocketSession.class)
                    .sendMessage(new TextMessage("Hello World!"));
            return null;
        }).when(handler).afterConnectionEstablished(any());

        CountDownLatch numberOfExpectedMessages = new CountDownLatch(1);
        doAnswer(invocation -> {
            numberOfExpectedMessages.countDown();
            return null;
        }).when(handler).handleMessage(any(), any());

        WebSocketClient client = new StandardWebSocketClient();
        ListenableFuture<WebSocketSession> future = client.doHandshake(handler, "ws://localhost:" + serverPort + "/api/ws/chat");
        WebSocketSession webSocketSession = future.get(2, SECONDS);

        boolean allExpectedMessagesReceived = numberOfExpectedMessages.await(2, SECONDS);
        if (!allExpectedMessagesReceived) {
            throw new AssertionError("Not all expected messages received");
        }

        webSocketSession.close();

        verify(handler).afterConnectionEstablished(any());
        verify(handler).handleMessage(any(), argThat(message -> message.getPayload().equals("Hello World!")));
        verify(handler).afterConnectionClosed(any(), any());
    }

}
