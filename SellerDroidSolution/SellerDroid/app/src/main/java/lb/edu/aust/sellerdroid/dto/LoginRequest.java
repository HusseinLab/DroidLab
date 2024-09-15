package lb.edu.aust.sellerdroid.dto;

/**
 * Created on 2016-02-23.
 */
public class LoginRequest {
    public String Username;
    public String Password;
    public LoginRequest(String u, String p)
    {
        this.Username = u;
        this.Password = p;
    }
}
