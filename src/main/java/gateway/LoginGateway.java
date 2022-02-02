package gateway;

import exceptions.UnauthorizedException;
import exceptions.UnknownException;
import model.Session;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoginGateway {
    private static final Logger LOGGER = LogManager.getLogger();

    public static Session login(String userName, String password) throws UnauthorizedException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost("http://localhost:8080/login");

            JSONObject formData = new JSONObject();
            formData.put("username", userName);
            formData.put("password", password);
            String formDataString = formData.toString();

            StringEntity reqEntity = new StringEntity(formDataString);
            postRequest.setEntity(reqEntity);

            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Content-type", "application/json");

            response = httpclient.execute(postRequest);

            System.out.println("On Login");
            switch(response.getStatusLine().getStatusCode()){
                case 200:
                    HttpEntity entity = response.getEntity();
                    String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    EntityUtils.consume(entity);

                    Session session = new Session(strResponse);
                    System.out.println("Response code received: " + response.getStatusLine().getStatusCode());
                    return session;
                case 401:
                    System.out.println("Non 200 response code received: " + response.getStatusLine().getStatusCode());
                    throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
                default:
                    System.out.println("Non 200 response code received: " + response.getStatusLine().getStatusCode());
                    throw new UnknownException(response.getStatusLine().getReasonPhrase());
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);

        }finally {
            try {
                if(httpclient != null)
                    httpclient.close();
                if(response != null)
                    response.close();

            } catch (IOException e) {
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }
}