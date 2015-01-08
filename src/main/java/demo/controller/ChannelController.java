package demo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.domain.Channel;
import demo.domain.User;
import demo.domain.view.Views;
import demo.service.ChannelsManager;
import demo.service.UsersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.builder;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;

/**
 * Created by nlabrot on 13/12/14.
 */
@Controller
public class ChannelController {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @Autowired
    private UsersManager usersManager;

    @Autowired
    private ChannelsManager channelsManager;

    @Autowired
    private WebSocketHandler subProtocolWebSocketHandler;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/index")
    public String test() {
        return "index";
    }

    @JsonView(Views.Public.class)
    @RequestMapping(value = "/ui/channels" , method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllVisibleChannel(){
        return ResponseEntity.ok(channelsManager.findAllVisibleChannel());
    }


    @JsonView(Views.Public.class)
    @RequestMapping(value = "/ui/channels", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity joinChannel(@RequestBody Map body, HttpSession httpSession) {

        String name = (String) body.get("name");

        User user = usersManager.findUserBySessionId(httpSession.getId()).orElse(null);

        if (user != null) {
            Channel channel = channelsManager
                    .findByName(name)
                    .orElseGet(() -> channelsManager.addChannel(name));

            channel.getUsers().add(user);

            //Notify channel user
            channel.getUsers().forEach(u -> {
                try {
                    String json = objectMapper.writerWithView(Views.Public.class).writeValueAsString(channel);

                    this.messagingTemplate.convertAndSend("/queue/" + u.getPrivateId() + "/channel",
                           json);
                } catch (JsonProcessingException e) {
                    LOG.error(e.getMessage() , e);
                }
            });


            return ok(channel);
        }

        return ResponseEntity.notFound().build();
    }


    @RequestMapping("/send")
    public ResponseEntity send() {
        usersManager.getUsers().stream().forEach(u -> this.messagingTemplate.convertAndSend("/topic/" + u.getPrivateId(), "okok"));

        return ok().build();
    }


    @MessageMapping("/queue/channel/{channelPrivateId}")
    public void handle(Message message, @DestinationVariable("channelPrivateId") String channelPrivateId, SimpMessageHeaderAccessor headerAccessor) throws IOException {

        String sessionId = (String) headerAccessor.getSessionAttributes().get(HTTP_SESSION_ID_ATTR_NAME);

        Channel channel = channelsManager.findByPrivateId(channelPrivateId).orElse(null);

        //Find Channel
        if (channel != null) {
            Map map = objectMapper.readValue((byte[]) message.getPayload(), Map.class);

            //Get target
            String toPublicId = (String) map.get("toPublicId");
            //Check if target is registered on the channel
            if (channel.findUserByPublicId(toPublicId).isPresent()) {

                usersManager.findUserBySessionId(sessionId).ifPresent(sender -> {
                    Map newMsg = builder()
                            .put("channel", channel)
                            .put("author", sender.getName())
                            .put("message", map.get("message"))
                            .put("date", System.currentTimeMillis()).build();

                    usersManager.findUserByPublicId((String) map.get("toPublicId")).ifPresent(u -> {
                        try {
                            this.messagingTemplate.convertAndSend("/queue/" + u.getPrivateId() + "/message", objectMapper.writeValueAsString(newMsg));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        }
    }
}
