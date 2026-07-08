package pkgServer.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pkgBoundary.DBMSboundary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBMSHandler implements HttpHandler {
    private final Gson gson = new Gson();

    @Override
    @SuppressWarnings("unchecked")
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equals(exchange.getRequestMethod())) {
            try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody())) {
                Map<String, Object> request = gson.fromJson(isr, new TypeToken<Map<String, Object>>(){}.getType());
                String methodName = (String) request.get("method");
                List<String> paramTypes = (List<String>) request.get("paramTypes");
                List<Object> params = (List<Object>) request.get("params");

                Class<?>[] types = new Class<?>[paramTypes.size()];
                Object[] args = new Object[params.size()];

                for (int i = 0; i < paramTypes.size(); i++) {
                    String pt = paramTypes.get(i);
                    Object val = params.get(i);
                    if ("String".equals(pt)) {
                        types[i] = String.class;
                        args[i] = val != null ? val.toString() : null;
                    } else if ("int".equals(pt)) {
                        types[i] = int.class;
                        args[i] = val != null ? ((Number) val).intValue() : 0;
                    } else if ("boolean".equals(pt)) {
                        types[i] = boolean.class;
                        args[i] = val != null ? (Boolean) val : false;
                    }
                }

                Method method = DBMSboundary.class.getMethod(methodName, types);
                Object result = method.invoke(DBMSboundary.getInstance(), args);

                String jsonResponse;
                if (result instanceof ResultSet) {
                    ResultSet rs = (ResultSet) result;
                    List<Map<String, Object>> resultList = new ArrayList<>();
                    ResultSetMetaData md = rs.getMetaData();
                    int columns = md.getColumnCount();
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columns; i++) {
                            row.put(md.getColumnName(i), rs.getObject(i));
                        }
                        resultList.add(row);
                    }
                    jsonResponse = gson.toJson(resultList);
                    // Cleanup ResultSet and its implicit Statement since it's just a generic query
                    try { rs.getStatement().close(); } catch(Exception ignored){}
                    try { rs.close(); } catch(Exception ignored){}
                } else {
                    jsonResponse = gson.toJson(result);
                }

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                byte[] bytes = jsonResponse.getBytes();
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String error = "{\"error\": \"" + e.getMessage() + "\"}";
                byte[] bytes = error.getBytes();
                exchange.sendResponseHeaders(500, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
