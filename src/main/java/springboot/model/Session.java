package springboot.model;

import javax.persistence.*;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @Column(name = "token", nullable = false)
    private String Token;

    @Column(name = "user_id", nullable = false, length = 100)
    private Long id;

    public Session() {
    }

    @Override
    public String toString() {
        return "Session{" +
                "Token='" + Token + '\'' +
                ", userId='" + id + '\'' +
                '}';
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
