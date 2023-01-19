package de.neuefische.ffmjava221.websocket.backend;

public record ChatMessage(
        String to,
        String message
) {
}
