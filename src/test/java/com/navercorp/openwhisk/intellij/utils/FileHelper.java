package com.navercorp.openwhisk.intellij.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FileHelper {
    public static String readFile(String filePath) throws IOException {
        InputStream inputStream = FileHelper.class.getClassLoader().getResourceAsStream(filePath);
        return new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
