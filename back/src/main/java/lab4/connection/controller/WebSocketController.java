package lab4.connection.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WebSocketController {

    private SimpMessagingTemplate messagingTemplate;

    // Method for client-to-server communication
    @MessageMapping("/send")
    @SendTo("/topic/updates")
    public String handleMessageFromClient(String message) {
        return message;  // This message will be broadcasted to /topic/updates
    }

    // Method to send messages to clients from other controllers
    public void update(String msg) {
        messagingTemplate.convertAndSend("/topic/updates", msg);
    }
}