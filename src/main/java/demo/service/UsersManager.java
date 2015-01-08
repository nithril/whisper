package demo.service;

import demo.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by nlabrot on 17/12/14.
 */
@Service
public class UsersManager {

    private List<User> users = new ArrayList<>();

    public User addUser(String name, String publicKey, String sessionId) {

        User user = new User();
        user.setName(name);
        user.setPublicKey(publicKey);
        user.setPrivateId(UUID.randomUUID().toString());
        user.setPublicId(UUID.randomUUID().toString());
        user.setSessionId(sessionId);

        users.add(user);

        return user;
    }

    public List<User> getUsers() {
        return users;
    }

    public Optional<User> findUserByPrivateId(String privateId){
        return users.stream().filter(u -> privateId.equals(u.getPrivateId())).findFirst();
    }

    public Optional<User> findUserByPublicId(String publicId){
        return users.stream().filter(u -> publicId.equals(u.getPublicId())).findFirst();
    }


    public Optional<User> findUserBySessionId(String sessionId){
        return users.stream().filter(u -> sessionId.equals(u.getSessionId())).findFirst();
    }


    public Optional<User> findUserByName(String name){
        return users.stream().filter(u -> name.equals(u.getName())).findFirst();
    }


    public void removeUserBySessionId(String sessionId) {
        users = users.stream().filter(u -> !sessionId.equals(u.getSessionId())).collect(Collectors.toList());
    }
}
