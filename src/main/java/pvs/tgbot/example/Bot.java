package pvs.tgbot.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author v.peschaniy
 *      Date: 27.02.2020
 */
public class Bot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "YulyaTelegramBot";
    private static final String TOKEN = "999519992:AAEkDHFixEE1Nn_fOduJj4z1pWuNPF5doGc";

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("onUpdateReceived");
        System.out.println(update);

        Message inputMessage = update.getMessage();
        String txt = inputMessage.getText();

        if (txt.equals("/start")) {
            sendAnswer(inputMessage.getChatId(), "Hello, world! This is simple bot!");
        }
    }

    private void sendAnswer(long chatId, String text) {

        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);
        messageToSend.setText(text);
        try {
            execute(messageToSend);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        System.out.println("getBotUsername");
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        System.out.println("getBotToken");
        return TOKEN;
    }
}
