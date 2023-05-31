package ru.netology;

import lombok.Value;

@Value
public class Request {
    String method;
    String path;
    String headers;
    String body;

}
