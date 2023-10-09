package dk.cphbusiness.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class Role {
    @Id
    private String name;
    @ManyToMany(mappedBy = "roles")
    Set<User> users = new HashSet<>();
    public Role(String name) {
        this.name = name;
    }
}
