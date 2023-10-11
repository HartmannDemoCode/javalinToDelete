package dk.cphbusiness.data;

import io.javalin.security.RouteRole;

import java.util.Set;

public interface ISecurityDAO {
     User getUser(String username);
     User createUser(User user);
     void addRoleToUser(String username, String roleName);

     boolean authenticateUser(String username, String password);
}
