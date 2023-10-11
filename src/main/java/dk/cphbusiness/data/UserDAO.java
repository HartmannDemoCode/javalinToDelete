package dk.cphbusiness.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;

public class UserDAO implements ISecurityDAO {

    private static EntityManagerFactory emf;
    private static ISecurityDAO securityDAO;
    private UserDAO() { }
    public static ISecurityDAO getInstance() {
        if(securityDAO == null) {
            securityDAO = new UserDAO();
            emf = HibernateConfig.getEntityManagerFactory();
        }
        return securityDAO;
    }
    public void setEmf(EntityManagerFactory _emf) {
        this.emf = _emf;
    }
    @Override
    public User getUser(String username) {
        try(EntityManager em = emf.createEntityManager()){
            User found = em.find(User.class, username);
            if(found==null)
                throw new EntityNotFoundException("No user found with username: "+username);
            found.getRoles().size();
            return found;
        }
    }

    @Override
    public User createUser(User user) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            user.roles.forEach(role -> {
                    Role found = em.find(Role.class, role.getName());
                    if(found==null) {
                        em.persist(role);
                    }else{
                        role = found;
                    }
                });
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        userDAO.setEmf(HibernateConfig.getEntityManagerFactory());
//        User user = new User("user", "test123");
//        user.addRole(new Role("user"));
//        user = userDAO.createUser(user);
//        System.out.println(user);
        System.out.println(userDAO.getUser("user"));
        if(userDAO.authenticateUser("user", "test123")) System.out.println("User authenticated");
        else System.out.println("User not authenticated");
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        //

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        User user = getUser(username);
        return user.verifyPassword(password);
    }
}
