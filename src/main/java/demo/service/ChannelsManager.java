package demo.service;

import demo.domain.Channel;
import demo.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by nlabrot on 20/12/14.
 */
@Service
public class ChannelsManager {

    private List<Channel> channels = new ArrayList<>();

    public Channel addChannel(String name){
        Channel channel = new Channel();
        channel.setName(name);
        channel.setPublicId(UUID.randomUUID().toString());
        channel.setPrivateId(UUID.randomUUID().toString());
        channels.add(channel);
        return channel;
    }

    public Optional<Channel> findByName(String name){
        return channels.stream().filter(c -> c.getName().equals(name)).findFirst();
    }

    public Optional<Channel> findByPublicId(String id){
        return channels.stream().filter(c -> c.getPublicId().equals(id)).findFirst();
    }

    public Optional<Channel> findByPrivateId(String id){
        return channels.stream().filter(c -> c.getPrivateId().equals(id)).findFirst();
    }

    public List<Channel> findAllVisibleChannel(){
        return channels.stream().filter(Channel::isVisible).collect(Collectors.toList());
    }


    public void removeUserBySessionId(String sessionId) {
        for (Channel channel : channels) {
            channel.removeUserBySessionId(sessionId);
        }

    }
}
