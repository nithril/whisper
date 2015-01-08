package demo.domain;

import com.fasterxml.jackson.annotation.JsonView;
import demo.domain.view.Views;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by nlabrot on 12/12/14.
 */
public class Channel {

    @JsonView(value = Views.Public.class)
    private String name;

    @JsonView(value = Views.Public.class)
    private String publicId;

    @JsonView(value = Views.Public.class)
    private String privateId;

    @JsonView(value = Views.Internal.class)
    private boolean visible = true;

    @JsonView(value = Views.Public.class)
    private Set<User> users = new LinkedHashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getPrivateId() {
        return privateId;
    }

    public void setPrivateId(String privateId) {
        this.privateId = privateId;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Optional<User> findUser(Predicate<? super User> predicate){
        return users.stream().filter(predicate).findFirst();
    }

    public Optional<User> findUserByPublicId(String publicId){
        return users.stream().filter(u -> publicId.equals(u.getPublicId())).findFirst();
    }

    public void removeUserBySessionId(String sessionId) {
        users = users.stream().filter(u -> !sessionId.equals(u.getSessionId())).collect(Collectors.toSet());
    }


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
