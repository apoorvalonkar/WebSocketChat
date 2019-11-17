package com.example.springbootwebsocketchat.chat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.springbootwebsocketchat.controller.WebSocketChatServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class WebSocketChatServerTest {
    private Basic endpoint;
    private Session session;
    private WebSocketChatServer server;
    private ArgumentCaptor<String> captor;
    private List<Session> openSessions = new ArrayList();

    @Before
    public void setUp() {
        server = new WebSocketChatServer();
        captor = ArgumentCaptor.forClass(String.class);
        endpoint = mock(Basic.class);
        session = createSession("an id", endpoint);
    }

    @After
    public void tearDown() {
        openSessions.forEach(WebSocketChatServer::onClose);
    }

    @Test
    public void sendMessageonUserJoin() throws IOException {
        WebSocketChatServer.onOpen(session);

        verify(endpoint).sendText(captor.capture());
        assertEquals("ENTER", sentObject().getString("type"));
        assertEquals(1, sentObject().getIntValue("onlineCount"));
    }

    @Test
    public void canOpenMoreThanOneSession() throws IOException {
        Session anotherSession = createSession("another id", mock(Basic.class));

        WebSocketChatServer.onOpen(session);
        WebSocketChatServer.onOpen(anotherSession);

        verify(endpoint, times(2)).sendText(captor.capture());
        assertEquals("ENTER", sentObject().getString("type"));
        assertEquals(2, sentObject().getIntValue("onlineCount"));
    }

    @Test
    public void cannotOpenSameSessionTwice() throws IOException {
        WebSocketChatServer.onOpen(session);
        WebSocketChatServer.onOpen(session);

        verify(endpoint, times(1)).sendText(anyString());
    }

    @Test
    public void sendMessage() throws IOException {
        Map<String, String> message = new HashMap<>();
        message.put("username","a username");
        message.put("msg","a msg");

        WebSocketChatServer.onOpen(session);
        server.onMessage(session, JSON.toJSONString(message));

        verify(endpoint, times(2)).sendText(captor.capture());
        assertEquals("SPEAK", sentObject().getString("type"));
        assertEquals("a username", sentObject().getString("username"));
        assertEquals("a msg", sentObject().getString("msg"));
        assertEquals(1, sentObject().getIntValue("onlineCount"));
    }

    @Test
    public void DisplayUserLeave() throws IOException {
        Session anotherSession = createSession("another id", mock(Basic.class));

        WebSocketChatServer.onOpen(session);
        WebSocketChatServer.onOpen(anotherSession);
        WebSocketChatServer.onClose(anotherSession);

        verify(endpoint, times(3)).sendText(captor.capture());
        assertEquals("LEAVE", sentObject().getString("type"));
        assertEquals(1, sentObject().getIntValue("onlineCount"));
    }

    @Test
    public void CannotsendMessageafterSessionClose() throws IOException {
        Map<String, String> message = new HashMap<>();
        message.put("username","a username");
        message.put("message","a message");

        Session anotherSession = createSession("another id", mock(Basic.class));
        WebSocketChatServer.onOpen(anotherSession);

        WebSocketChatServer.onOpen(session);
        WebSocketChatServer.onClose(session);
        server.onMessage(anotherSession, JSON.toJSONString(message));

        verify(endpoint, times(1)).sendText(anyString());
    }


    private Session createSession(String id, Basic endpoint) {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn(id);
        when(session.getBasicRemote()).thenReturn(endpoint);
        openSessions.add(session);
        return session;
    }

    private JSONObject sentObject() {
        return JSON.parseObject(captor.getValue());
    }
}
