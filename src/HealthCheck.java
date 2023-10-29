import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


public class HealthCheck {
    static List<String> healthyServers = new ArrayList<>();
    static public int doHealthcheck() throws IOException, InterruptedException {
        System.out.println("Starting healthcheck");
        for (int i = 0; i< Main.servers.size(); i++) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(Main.servers.get(i)+"/healthcheck")).GET().build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if(response.statusCode()!=200){
                    System.out.println(response.statusCode());
                    healthyServers.remove(Main.servers.get(i));
                }
                else {
                    if(!healthyServers.contains(Main.servers.get(i))){
                        healthyServers.add(Main.servers.get(i));
                    }
                }
            } catch(Exception e){
                healthyServers.remove(Main.servers.get(i));
            }
        }
        if(healthyServers.size() == 0){
            System.out.println("No healthy servers!");
            return 0;
        }
        System.out.printf("HealthCheck complete, %d healthy servers\n",healthyServers.size());
        return 1;
    }
}
