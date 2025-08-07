package com.leir4iks.extraction;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

public class TarGzExtraction extends AbstractTarExtraction {
    @Override
    protected InputStream getDecompressorStream(InputStream inputStream) throws IOException {
        return new GzipCompressorInputStream(inputStream);
    }
}