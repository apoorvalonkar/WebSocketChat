package com.example.springbootwebsocketchat.controller;

import com.alibaba.fastjson.JSON;
import com.example.springbootwebsocketchat.model.Message;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session   WebSocket Session
 */

@Component
@ServerEndpoint("/chat")
public class WebSocketChatServer {
    /**
     * All chat sessions.
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    private static void sendMessageToAll(String msg) {
        //TODO: add send message method.
        onlineSessions.forEach((id,session) -> {
            try {
                session.getBasicRemote().sendText(msg);
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    /**
     * Open connection, 1) add session, 2) add user.
     */
    @OnOpen
    public static void onOpen(Session session) {
        //TODO: add on open connection.
        if(onlineSessions.containsKey(session.getId())) { return; }

        onlineSessions.put(session.getId(), session);
        sendMessageToAll(Message.jsonConverter("ENTER", "", "", onlineSessions.size()));

    }

    /**
     * Send message, 1) get username and session, 2) send message to all.
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        //TODO: add send message.
        onlineSessions.get(session.getId());
        Message message= JSON.parseObject(jsonStr,Message.class);
        sendMessageToAll(Message.jsonConverter("SPEAK",message.getUsername(),message.getMsg(),onlineSessions.size()));

    }

    /**
     * Close connection, 1) remove session, 2) update user.
     */
    @OnClose
    public static void onClose(Session session) {
        //TODO: add close connection.
        onlineSessions.remove(session.getId());
        sendMessageToAll(Message.jsonConverter("LEAVE","","",onlineSessions.size()));

    }

    /**
     * Print exception.
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

}
