package demo.domain;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by nlabrot on 12/12/14.
 */
public class Channel {

    private String name;

    private String publicId;
    private String privateId;

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

}
