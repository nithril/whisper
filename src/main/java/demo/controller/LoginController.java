package demo.controller;

import demo.service.UsersManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by nlabrot on 13/12/14.
 */
@Controller
public class LoginController {

    @Autowired
    private UsersManager usersManager;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @Autowired
    private WebSocketHandler subProtocolWebSocketHandler;


    @RequestMapping(value = "/login", produces = "application/json")
    public ResponseEntity test(@RequestBody Map<String,Object> body, HttpSession httpSession) {
        usersManager.removeUserBySessionId(httpSession.getId());
        return ok(usersManager.addUser((String) body.get("name"), (String) body.get("publicKey") , httpSession.getId()));
    }
}
