package demo.controller;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class MyHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("ok");

        session.sendMessage(new TextMessage("ddd"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.getId();
    }
}