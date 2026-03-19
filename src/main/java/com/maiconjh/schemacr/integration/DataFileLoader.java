package com.maiconjh.schemacr.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Loads YAML or JSON data files into generic Java objects (Map/List/primitive).
 */
public class DataFileLoader {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public Object load(Path path, boolean yaml) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("Data file does not exist: " + path);
        }

        ObjectMapper mapper = yaml ? yamlMapper : jsonMapper;
        return mapper.readValue(path.toFile(), new TypeReference<Map<String, Object>>() {
        });
    }
}