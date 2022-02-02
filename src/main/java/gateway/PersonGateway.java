package gateway;

import exceptions.UnauthorizedException;
import model.Audit;
import model.Person;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import view.MainController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersonGateway {

    public Results fetchPeople(int pageNum, String lastName){
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();

            String optionalParameter = "";
            if (!lastName.equals(""))
                optionalParameter += "&lastName=" + lastName;

            HttpGet getRequest = new HttpGet("http://localhost:8080/people?pageNum=" + pageNum + optionalParameter);

            String SessionToken = MainController.getInstance().getSession().getSessionToken();
            getRequest.setHeader("Authorization", SessionToken);

            CloseableHttpResponse response = httpclient.execute(getRequest);

            System.out.println("On fetching people");
            if(response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Non 200 response code received: " + response.getStatusLine().getStatusCode());
                response.close();
                httpclient.close();
                throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
            }
            System.out.println("Response code received: " + response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);

            ArrayList<Person> personsList = new ArrayList<>();
            JSONObject fetchResults = new JSONObject(strResponse);
            JSONArray persons = fetchResults.getJSONArray("persons");

            for (Object rawPerson: persons) {
                JSONObject person = (JSONObject) rawPerson;
                LocalDate dateOfBirth = LocalDate.parse(person.getString("dateOfBirth"));
                Period p = Period.between(dateOfBirth, LocalDate.now());
                int age = p.getYears();
                Person x = new Person(person.getInt("id"), person.getString("firstName"), person.getString("lastName"), dateOfBirth, age, person.getString("lastModified"));
                personsList.add(x);
            }

            Results result = new Results(fetchResults.getInt("currentPage"), fetchResults.getString("searchBy"), fetchResults.getLong("totalElements"), fetchResults.getInt("totalPages"), personsList);

            response.close();
            httpclient.close();
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
    }

    public Person fetchPersonById(int id){
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();

            String url = "http://localhost:8080/people/:" + id;
            HttpGet getRequest = new HttpGet(url);

            String SessionToken = MainController.getInstance().getSession().getSessionToken();
            getRequest.setHeader("Authorization", SessionToken);

            CloseableHttpResponse response = httpclient.execute(getRequest);

            System.out.println("On fetching person");
            if(response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Non 200 response code received: " + response.getStatusLine().getStatusCode());
                response.close();
                httpclient.close();
                throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
            }
            System.out.println("Response code received: " + response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);

            JSONObject person = new JSONObject(strResponse);
            LocalDate dateOfBirth = LocalDate.parse(person.getString("dateOfBirth"));
            Period p = Period.between(dateOfBirth, LocalDate.now());
            int age = p.getYears();
            Person x = new Person(person.getInt("id"), person.getString("firstName"), person.getString("lastName"), dateOfBirth, age, person.getString("lastModified"));

            response.close();
            httpclient.close();
            return x;

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
    }

    public void updatePerson(int id, HashMap<String, String> personFields){
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();

            String url = "http://localhost:8080/people/:" + id;
            HttpPut putRequest = new HttpPut(url);

            String SessionToken = MainController.getInstance().getSession().getSessionToken();
            putRequest.setHeader("Authorization", SessionToken);

            JSONObject formData = new JSONObject();
            for (Map.Entry<String, String> entry : personFields.entrySet()) {
                formData.put(entry.getKey(), entry.getValue());
            }
            String formDataString = formData.toString();

            StringEntity reqEntity = new StringEntity(formDataString);
            putRequest.setEntity(reqEntity);

            putRequest.setHeader("Accept", "application/json");
            putRequest.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = httpclient.execute(putRequest);

            System.out.println("On update person");
            if(response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Non 200 response code received: " + response.getStatusLine().getStatusCode());
                response.close();
                httpclient.close();
                throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
            }
            System.out.println("Response code received: " + response.getStatusLine().getStatusCode());

            response.close();
            httpclient.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
    }

    public void addPerson(String firstName, String lastName, String DateOfBirth){
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost("http://localhost:8080/people");

            String SessionToken = MainController.getInstance().getSession().getSessionToken();
            postRequest.setHeader("Authorization", SessionToken);

            JSONObject formData = new JSONObject();
            formData.put("firstName", firstName);
            formData.put("lastName", lastName);
            formData.put("dateOfBirth", DateOfBirth);
            String formDataString = formData.toString();

            StringEntity reqEntity = new StringEntity(formDataString);
            postRequest.setEntity(reqEntity);

            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = httpclient.execute(postRequest);

            System.out.println("On add person");
            if(response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Non 200 response code received: " + response.getStatusLine().getStatusCode());
                response.close();
                httpclient.close();
                throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
            }
            System.out.println("Response code received: " + response.getStatusLine().getStatusCode());

            response.close();
            httpclient.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
    }

    public void deletePerson(int id){
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();

            String url = "http://localhost:8080/people/:" + id;
            HttpDelete deleteRequest = new HttpDelete(url);

            String SessionToken = MainController.getInstance().getSession().getSessionToken();
            deleteRequest.setHeader("Authorization", SessionToken);

            CloseableHttpResponse response = httpclient.execute(deleteRequest);

            System.out.println("On delete person");
            if(response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Non 200 response code received: " + response.getStatusLine().getStatusCode());
                response.close();
                httpclient.close();
                throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
            }
            System.out.println("Response code received: " + response.getStatusLine().getStatusCode());

            response.close();
            httpclient.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
    }

    public ArrayList<Audit> fetchAuditTrailById(int id){
        try{
            ArrayList<Audit> auditArrayList = new ArrayList<>();
            CloseableHttpClient httpclient = HttpClients.createDefault();

            String url = "http://localhost:8080/people/:" + id + "/audittrail";
            HttpGet getRequest = new HttpGet(url);

            String SessionToken = MainController.getInstance().getSession().getSessionToken();
            getRequest.setHeader("Authorization", SessionToken);

            CloseableHttpResponse response = httpclient.execute(getRequest);

            System.out.println("On fetching audit trail");
            if(response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Non 200 response code received: " + response.getStatusLine().getStatusCode());
                response.close();
                httpclient.close();
                throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
            }
            System.out.println("Response code received: " + response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);

            JSONArray audits = new JSONArray(strResponse);

            for (Object rawAudit: audits) {
                JSONObject audit = (JSONObject) rawAudit;
                Audit x = new Audit(audit.getString("change_msg"), audit.getInt("changed_by"), audit.getInt("person_id"), audit.getString("when_occurred"));
                auditArrayList.add(x);
            }
            response.close();
            httpclient.close();
            return auditArrayList;

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
    }
}