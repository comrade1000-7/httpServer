package ru.netology;

import lombok.Data;
import lombok.Value;
import org.apache.hc.core5.http.NameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.apache.hc.core5.net.URLEncodedUtils.parse;


@Data
public class Request {
    String method;
    String path;
    String mimeType;
    Map<String, List<String>> params = new HashMap<>();


    public Request(String method, String path) {
        this.method = method;

        if (!path.contains("?")) {
            this.path = path;
        } else {
            this.path = path.substring(0, path.indexOf("?"));
            try {
                List<NameValuePair> nameValuePairs = parse(new URI(path), StandardCharsets.UTF_8);

                for (NameValuePair nameValuePair: nameValuePairs) {
                    if (!params.containsKey(nameValuePair.getName())) {
                        params.put(nameValuePair.getName(), new ArrayList<>(Collections.singleton(nameValuePair.getValue())));
                    } else {
                        params.get(nameValuePair.getName()).add(nameValuePair.getValue());
                    }
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getPath() {
        return path;
    }

    long length;
    String response;


    public String getResponse() {
        try {
            mimeType = Files.probeContentType(Path.of("httpServer", "public", path));
            length = Files.size(Path.of("httpServer", "public", path));

        }catch (IOException e){
            e.printStackTrace();
        }
        response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        return response;
    }
    public String getResponse(byte[] content){
        try {
            mimeType = Files.probeContentType(Path.of(path));

            length = Files.size(Path.of(path));

        }catch (IOException e){
            e.printStackTrace();
        }
        response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        return response;
    }

    public Map<String, List<String>> getParams () {
        return params;
    }

    public List<String> getParamsByName (String name) {
        return params.get(name);
    }
}
