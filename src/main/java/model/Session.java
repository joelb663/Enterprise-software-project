package model;

public class Session {
    private String sessionToken;

    public Session(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionToken='" + sessionToken + '\'' +
                '}';
    }
}
