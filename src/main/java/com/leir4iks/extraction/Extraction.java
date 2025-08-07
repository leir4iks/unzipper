package com.leir4iks.extraction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Extraction {
    void extract(Path sourceFile, Path destinationDirectory) throws IOException;
    List<String> list(Path sourceFile) throws IOException;
}