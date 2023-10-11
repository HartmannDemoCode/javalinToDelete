package dk.cphbusiness.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    private String username;
    private String password;
    @ManyToMany
            @JoinTable(name = "user_roles", joinColumns = {
                    @JoinColumn(name = "user_name", referencedColumnName = "username")}, inverseJoinColumns = {
                    @JoinColumn(name = "role_name", referencedColumnName = "name") } )
    Set<Role> roles = new HashSet<>();
    public User(String username, String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        this.username = username;
        this.password = hashed;
    }
    public User(String username, Set<Role> roles){
        this.username = username;
        this.roles = roles;
    }
    public String getRolesAsString(){
        return roles.stream().map(role->role.getName()).collect(Collectors.toSet()).toString();
    }
    public boolean verifyPassword(String pw){
        return BCrypt.checkpw(pw, this.password);
    }
    public void addRole(Role role){
        roles.add(role);
        role.getUsers().add(this);
    }
    public void removeRole(Role role){
        roles.remove(role);
        role.getUsers().remove(this);
    }
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", roles=" + getRolesAsString() +
                '}';
    }
}
