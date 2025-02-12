package com.example.richpresencediscord;

import com.google.gson.JsonObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

@Mod("richpresencediscord")
public class Richpresencediscord {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static String webhookUrl = "";

    // Inicialização do mod
    public Richpresencediscord() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Configuração do mod, carregando o webhook
    private void setup(final FMLCommonSetupEvent event) {
        webhookUrl = ConfigManager.loadWebhook();
        LOGGER.info("Richpresencediscord carregado! Webhook: " + webhookUrl);
    }

    // Evento quando um jogador entra no servidor
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        String message = formatPlayerMessage(event.getEntity(), "entrou");
        sendDiscordMessage(message);
    }

    // Evento quando um jogador sai do servidor
    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        String message = formatPlayerMessage(event.getEntity(), "saiu");
        sendDiscordMessage(message);
    }

    // Formatação da mensagem para o Discord
    private String formatPlayerMessage(Player player, String action) {
        String playerName = player.getName().getString();
        String currentTime = new SimpleDateFormat("HH:mm").format(new Date());

        ResourceKey<Level> dimension = player.level().dimension();
        String dimensionName = dimension.location().toString();
        dimensionName = dimensionName.replace("minecraft:", "").replace("_", " ");

        return String.format("🎮 **%s** %s no servidor! 🚀\n⏰ **Horário**: %s\n🌍 **Dimensão**: %s\n ", playerName, action, currentTime, dimensionName);
    }


    // Envio da mensagem para o Discord
    private void sendDiscordMessage(String message) {
        if (webhookUrl.isEmpty()) {
            LOGGER.warn("Webhook do Discord não configurado. Mensagem não enviada.");
            return;
        }

        try {
            // Usando Gson para criar o payload JSON
            JsonObject json = new JsonObject();
            json.addProperty("content", message);
            String jsonPayload = json.toString();

            java.net.URL url = new java.net.URL(webhookUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (java.io.OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                LOGGER.warn("Erro ao enviar mensagem para o Discord. Código: " + responseCode);
            }
        } catch (Exception e) {
            LOGGER.error("Erro ao enviar mensagem para o Discord: ", e);
        }
    }
}
