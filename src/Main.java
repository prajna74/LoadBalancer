import com.sun.net.httpserver.HttpServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {
    static List<String> servers = new ArrayList<String>();
    static String filePath = "src/resources/config.properties";
    static FileInputStream inputStream;
    static int g=0;
    static Properties properties = new Properties();
    static int status;
    public static void main(String[] args) throws IOException, InterruptedException {
        inputStream = new FileInputStream(filePath);
        properties.load(inputStream);
        String serverList = properties.getProperty("servers");
        servers = List.of(serverList.split(","));

        HttpServer httpserver = HttpServer.create(new InetSocketAddress(Integer.parseInt(properties.getProperty("port"))),0);
        httpserver.createContext("/",new RequestHandler());
        //Using cached threadpool because with this new threads are created as needed. But cannot be used when the tasks are very long running.
        //Read more here - https://howtodoinjava.com/java/multi-threading/java-thread-pool-executor-example/#3-2-cached-thread-pool-executor
        httpserver.setExecutor(Executors.newCachedThreadPool());

        status = HealthCheck.doHealthcheck();

        httpserver.start();
        Runnable healthcheck = () -> {
            try {
                status = HealthCheck.doHealthcheck();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
         final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(HealthCheck.healthyServers.size());
         scheduler.scheduleAtFixedRate(healthcheck,60,60,SECONDS);
    }
}