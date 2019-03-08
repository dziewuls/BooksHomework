package com.example.homework.infrastructure;

import java.io.IOException;

public class DataSourceNotFoundException extends IOException {

    DataSourceNotFoundException(String message) {
        super(message);
    }
}
