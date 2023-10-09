package dk.cphbusiness.data;

public interface ISecurityDAO {
    public User getUser(String username);
    public User createUser(User user);
    public void addRoleToUser(String username, String roleName);

    public boolean authenticateUser(String username, String password);
}
