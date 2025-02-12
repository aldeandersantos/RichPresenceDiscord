package com.example.richpresencediscord;

import com.electronwill.nightconfig.core.file.FileConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class ConfigManager {
    private static final Path CONFIG_PATH = Paths.get("config/richpresencediscord.toml");

    public static String loadWebhook() {
        if (!Files.exists(CONFIG_PATH)) {
            createDefaultConfig();
        }

        try (FileConfig config = FileConfig.builder(CONFIG_PATH).autoreload().build()) {
            config.load();
            return config.getOrElse("webhook_url", "");
        }
    }

    private static void createDefaultConfig() {
        try {
            Path parentDir = CONFIG_PATH.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);  // Cria a pasta 'config' se não existir
            }
            if (!Files.exists(CONFIG_PATH)) {
                Files.createFile(CONFIG_PATH);  // Cria o arquivo 'richpresencediscord.toml' se não existir
                Files.writeString(CONFIG_PATH, "webhook_url = \"\"\n");  // Escreve o conteúdo inicial no arquivo
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo de configuração: " + e.getMessage());
        }
    }
}
