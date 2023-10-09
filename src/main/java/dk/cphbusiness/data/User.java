package dk.cphbusiness.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
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
        this.username = username;
        this.password = password;
    }
}
