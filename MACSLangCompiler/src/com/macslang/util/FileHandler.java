package com.macslang.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {
public static String readFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    return new String(bytes);
}
}