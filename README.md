# WebSocketchat
This application is a simple chat room application developed using web-sockets in Java. The basic tasks users can perform are:

1. Entering a room
2. Sending messages
3. Leaving the room

## Background
WebSocket is a communication protocol that makes it possible to establish a two-way communication channel between a
server and a client.


### Implemented the message model
Message model is the message payload that will be exchanged between the client and the server. Implement the Message
class in chat module.
1. ENTER
2. CHAT
3. LEAVE

### WebSocketChatServer 
Added the methods to ServerEndpoint WebSocket Client
1. onOpen() ->Establishes a connection between client and server.
2. onMessage() ->Gets session and user information and sends messages.
3. onClose() ->Closes the connection.
4. SendMessagetoAll() ->Sends the message to all connected users


### Run the application with command
mvn build; 
mvn spring-boot:run
