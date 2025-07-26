import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
    static final String API_KEY = "AIzaSyA39CqY8KEZbcYSjZtaYgKByXn7Emxy88o";
    static final String SEARCH_ENGINE_ID = "b5051e476f0be4950";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/search", Main::handleSearch);
        server.setExecutor(null);
        System.out.println("Server running at http://localhost:8080/");
        server.start();
    }

    public static void handleSearch(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        String query = "";
        String[] params = exchange.getRequestURI().getQuery().split("&");
        for (String param : params) {
            if (param.startsWith("query=")) {
                query = URLDecoder.decode(param.substring(6), "UTF-8");
            }
        }

        String response;
        try {
            String rawJson = searchGoogle(query);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, rawJson.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(rawJson.getBytes());
            os.close();
            return;

        } catch (Exception e) {
            response = "Error fetching results: " + e.getMessage();
        }

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static String searchGoogle(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        String urlStr = "https://www.googleapis.com/customsearch/v1?q=" + encodedQuery +
                "&key=" + API_KEY + "&cx=" + SEARCH_ENGINE_ID + "&num=5";

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString(); // You can parse and prettify this if needed
    }
}
