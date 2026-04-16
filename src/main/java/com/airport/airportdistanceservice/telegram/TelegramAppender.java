package com.airport.airportdistanceservice.telegram;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

public class TelegramAppender extends AppenderBase<ILoggingEvent> {

    @Setter
    private String botToken;
    @Setter
    private String chatId;

    private final RestClient restClient = RestClient.create();

    @Override
    protected void append(ILoggingEvent eventObject) {
        
        boolean isOurCode = eventObject.getLoggerName().startsWith("com.airport.airportdistanceservice");
        
        if (!isOurCode && !eventObject.getLevel().toString().equals("ERROR")) {
            return;
        }

        String message = String.format("📢 <b>[%s]</b> 📢%n%n<b>Class:</b> %s%n<b>Message:</b> %s",
                eventObject.getLevel().toString(),
                eventObject.getLoggerName(),
                eventObject.getFormattedMessage());

        sendToTelegram(message);
    }

    private void sendToTelegram(String text) {
        if (botToken == null || chatId == null || botToken.isEmpty()) return;

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", text);
        body.put("parse_mode", "HTML");

        try {
            new Thread(() -> {
                try {
                    restClient.post()
                            .uri(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(body)
                            .retrieve()
                            .toBodilessEntity(); // We do not need the response body
                } catch (Exception e) {
                    addError("Telegram log failed: " + e.getMessage(), e);
                }
            }).start();
        } catch (Exception ignored) {
        }
    }
}

