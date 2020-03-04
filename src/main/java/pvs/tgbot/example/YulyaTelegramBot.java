package pvs.tgbot.example;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author v.peschaniy
 * Date: 27.02.2020
 */
public class YulyaTelegramBot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "YulyaTelegramBot";
    private static final String BOT_TOKEN = "999519992:AAGxKzcJJiX1UUsT8tsZcyhZ-A8Hn38UCcs";
    private final Properties textProperties;

    YulyaTelegramBot(Properties textProperties) {
        this.textProperties = textProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("onUpdateReceived");

        if (update.hasInlineQuery()) {
            System.out.println("INLINE QUERY");
            System.out.println("u");

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            System.out.println("callbackData = " + callbackData);

            sendAnswerCallbackQuery(callbackQuery.getId(), callbackData);
        } else {
            Message inputMessage = update.getMessage();

            String txt = inputMessage.getText();
            System.out.println("inputMessage = " + txt);

            if (txt.equals("/start")) {
                sendAnswer(inputMessage.getChatId(), "start");
            }
            else if (txt.equals("/img")) {

                SendPhoto photo1 = new SendPhoto()
                        .setChatId(inputMessage.getChatId())
                        .setPhoto("ed", App.class.getResourceAsStream("/img/ed_sheeran.png"));

                SendPhoto photo2 = new SendPhoto()
                        .setChatId(inputMessage.getChatId())
                        .setPhoto("ed", App.class.getResourceAsStream("/img/some_dog.jpg"));

                sendPhotoUnsafe(photo1);
                sendPhotoUnsafe(photo2);
            } else {
                sendAnswer(inputMessage.getChatId(), "Мой бот еще глуповат для общения на любые темы.");
                //TODO: randomSticker
            }
        }
    }

    @SneakyThrows
    private Message sendPhotoUnsafe(SendPhoto photo) {
        return execute(photo);
    }

    private void sendAnswerCallbackQuery(String callbackQueryId, String text) {
        try {
            AnswerCallbackQuery answer = new AnswerCallbackQuery()
                    .setText(txt(text))
                    .setCallbackQueryId(callbackQueryId);
            sendApiMethod(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAnswer(long chatId, String messageName) {
        try {
            sendApiMethod(makeAnswer(chatId, messageName));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage makeAnswer(long chatId, String messageName) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);

        String text = txt(messageName);
        answer.setText(text);

        KeyboardRow keyboardRow = Stream.of("/run", "/cancel")
                .map(KeyboardButton::new)
                .collect(
                        KeyboardRow::new,
                        KeyboardRow::add,
                        throwingMerge()
                );

        answer.setReplyMarkup(new ReplyKeyboardMarkup(
                new ArrayList<KeyboardRow>() {{
                    add(keyboardRow);
                }}
        ));

        return answer;
    }

    private <T> BiConsumer<T, T> throwingMerge() {
        return (t1, t2) -> {
            throw new UnsupportedOperationException();
        };
    }

    private String txt(String messageName) {
        return textProperties.getProperty(messageName, messageName);
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}


//    List<InlineKeyboardButton> buttons = Stream.of("run", "cancel")
//            .map(buttonText -> {
//                InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
//                button.setCallbackData("/" + buttonText);
//                return button;
//            })
//            .collect(Collectors.toList());
//
//        answer.setReplyMarkup(new InlineKeyboardMarkup(
//                new ArrayList<List<InlineKeyboardButton>>() {{
//        add(buttons);
//        }}
//        ));