import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class RequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        OutputStream os = exchange.getResponseBody();
        if(Main.status==0)
        {
            System.out.println("No healthy hosts to serve the request");
            String errorMessage = "Something went wrong, please try again later";
            exchange.sendResponseHeaders(500,errorMessage.length());
            os.write(errorMessage.getBytes());
            return;
        }
        Main.g++;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(HealthCheck.healthyServers.get(Main.g%(HealthCheck.healthyServers.size())))).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenCompose(response -> {
                    byte[] responseBody = response.body();
                    String responseText = new String(responseBody, StandardCharsets.UTF_8);
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Content-Type", "text/plain"); // Adjust content type as needed
                    try {
                        exchange.sendResponseHeaders(200, responseText.length());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return CompletableFuture.supplyAsync(() -> responseText);
                })
                .thenAccept(responseText -> {
                    try {
                        System.out.println(responseText);
                        os.write(responseText.getBytes(StandardCharsets.UTF_8));
                        os.close(); // Close the output stream when done
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null; // Handle exceptions
                });
    }
}
