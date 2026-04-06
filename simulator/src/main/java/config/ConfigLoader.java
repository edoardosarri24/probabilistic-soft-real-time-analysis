package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;

public final class ConfigLoader {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public static SimulationConfig loadConfig(String filePath) throws IOException {
        if (filePath.endsWith(".yaml") || filePath.endsWith(".yml")) {
            return yamlMapper.readValue(new File(filePath), SimulationConfig.class);
        } else {
            return jsonMapper.readValue(new File(filePath), SimulationConfig.class);
        }
    }

}
