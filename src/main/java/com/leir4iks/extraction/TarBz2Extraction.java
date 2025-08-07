package com.leir4iks.extraction;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class TarBz2Extraction extends AbstractTarExtraction {
    @Override
    protected InputStream getDecompressorStream(InputStream inputStream) throws IOException {
        return new BZip2CompressorInputStream(inputStream);
    }
}