package pvs.tgbot.example;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static pvs.tgbot.example.Utils.loadResource;

/**
 * @author v.peschaniy
 * Date: 27.02.2020
 */
public class YulyaTelegramBot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "YulyaTelegramBot";
    private static final String BOT_TOKEN = "999519992:AAGxKzcJJiX1UUsT8tsZcyhZ-A8Hn38UCcs";

    private final Properties textProperties;
    private final Map<String, String> buttonTextToKeyMap;
    private final Map<Long, QuestProgress> progressMap = new HashMap<>();

    YulyaTelegramBot(Properties textProperties) {
        this.textProperties = textProperties;
        this.buttonTextToKeyMap = new HashMap<>();

        textProperties.forEach((key, value) -> {
            if (key.toString().startsWith("btn_")) {
                buttonTextToKeyMap.put(value.toString(), key.toString());
            }
        });
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("onUpdateReceived");
        if (!update.hasMessage()) {
            return;
        }

        Message updateMessage = update.getMessage();
        String inputText = updateMessage.getText();

        long chatId = updateMessage.getChatId();
        QuestProgress progress = progressMap.computeIfAbsent(chatId, QuestProgress::new);

        System.out.println("chatId = " + chatId);
        System.out.println("inputText = " + inputText);

        String buttonKey = buttonTextToKeyMap.get(inputText);
        if (buttonKey != null) {
            handleButtonCommand(progress, buttonKey);
        } else {
            switch (inputText) {
                case "/start":
                    sendAnswerTextMessage(chatId, "answer_start", "btn_goto_stage1", "btn_help");
                    break;

                case "/img":
                    SendPhoto photo1 = new SendPhoto()
                            .setChatId(chatId)
                            .setPhoto("ed", loadResource("/img/ed_sheeran.png"));

                    SendPhoto photo2 = new SendPhoto()
                            .setChatId(chatId)
                            .setPhoto("dog", loadResource("/img/some_dog.jpg"));

                    sendPhotoUnsafe(photo1);
                    sendPhotoUnsafe(photo2);

                    break;
                default:
                    sendAnswerTextMessage(chatId, "answer_unknown");
                    //TODO: randomSticker
                    break;
            }
        }
    }

    private void handleButtonCommand(QuestProgress progress, String buttonKey) {
        long chatId = progress.getChatId();

        switch (buttonKey) {

            // NAVIGATION
            case "btn_help":
                sendAnswerTextMessage(chatId, "answer_help");
                break;

            case "btn_restart":
            case "btn_rerunQuest":
                progress.clean();
                handleButtonCommand(progress, "btn_continueQuest");
                break;

            case "btn_rollback":
                progress.decrementProgress();
                handleButtonCommand(progress, "btn_continueQuest");
                break;

            case "btn_continueQuest":
                if (progress.getStage() <= 1) {
                    handleButtonCommand(progress, "btn_goto_stage1");
                } else {
                    handleButtonCommand(progress, "btn_goto_stage" + progress.getStage());
                }
                break;

            // STAGE 1
            case "btn_goto_stage1":
                if (progress.getStage() > 1) {
                    progress.setStage(1);
                    sendAnswerTextMessage(chatId, "answer_goto_stage1", "btn_goto_stage2", "btn_stage1_help1", "btn_stage1_help2", "btn_stage1_help3");
                } else {
                    sendAnswerTextMessage(chatId, "answer_goto_stage1_error", "btn_help");
                }
                break;

            case "btn_stage1_help1":
                sendAnswerTextMessage(chatId, "answer_stage1_help1");
                break;

            case "btn_stage1_help2":
                sendAnswerTextMessage(chatId, "answer_stage1_help2");
                break;

            case "btn_stage1_help3":
                sendAnswerTextMessage(chatId, "answer_stage1_help3");
                break;

            // STAGE 2
            case "btn_goto_stage2":
                progress.setStage(2);
                sendAnswerTextMessage(chatId, "answer_goto_stage2", "btn_goto_stage3");
                break;

            // STAGE 3
            case "btn_goto_stage3":
                progress.setStage(3);
                sendAnswerTextMessage(chatId, "answer_goto_stage3", "btn_goto_stage4");
                break;

            // STAGE 4
            case "btn_goto_stage4":
                progress.setStage(4);
                sendAnswerTextMessage(chatId, "answer_goto_stage4", "btn_goto_stage5");
                break;

            // STAGE 5
            case "btn_goto_stage5":
                progress.setStage(5);
                sendAnswerTextMessage(chatId, "answer_goto_stage5", "btn_goto_stage6");
                break;

            // STAGE 6
            case "btn_goto_stage6":
                progress.setStage(6);
                sendAnswerTextMessage(chatId, "answer_goto_stage6", "btn_goto_stage7");
                break;

            // STAGE 7
            case "btn_goto_stage7":
                progress.setStage(7);
                sendAnswerTextMessage(chatId, "answer_finishQuest", "btn_applyQuestReward", "btn_rerunQuest");
                break;

            // SKIP
            case "btn_skip":
            case "btn_skipQuest":
                sendAnswerTextMessage(chatId, "answer_skipQuest", "btn_skipQuest_ok", "btn_continueQuest");
                break;

            case "btn_skipQuest_ok":
                sendAnswerTextMessage(chatId, "answer_skipQuest_ok", "btn_skipQuest_final_ok", "btn_continueQuest");
                break;

            case "btn_skipQuest_final_ok":
                handleButtonCommand(progress, "btn_goto_stage7");
                break;

            // apply reward
            case "btn_applyQuestReward":
                sendAnswerTextMessage(chatId, "answer_applyQuestReward", "btn_rerunQuest");
                break;

            default:
                sendAnswerTextMessage(chatId, "answer_unknown_button");
                break;
        }
    }

    @SneakyThrows
    private Message sendPhotoUnsafe(SendPhoto photo) {
        return execute(photo);
    }

    private void sendAnswerTextMessage(long chatId, @Nonnull String messageName, String... buttons) {
        try {
            SendMessage answer = new SendMessage()
                    .setChatId(chatId)
                    .setText(getMessage(messageName));

            if (buttons.length > 0) {
                List<KeyboardRow> keyboard = createKeyboard(buttons);
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
                answer.setReplyMarkup(keyboardMarkup);
            }

            sendApiMethod(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<KeyboardRow> createKeyboard(String[] buttons) {
        return Arrays.stream(buttons)
                .map(this::getMessage)
                .map(KeyboardButton::new)
                .map(keyboardButton -> {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRow.add(keyboardButton);
                    return keyboardRow;
                })
                .collect(Collectors.toList());
    }

    private String getMessage(String messageName) {
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

    @Getter
    private static class QuestProgress {

        private long chatId;
        @Setter private int stage;

        private static final int INITIAL_PROGRESS = 0;
        private static final int MAX_PROGRESS = 7;

        QuestProgress(long chatId) {
            this.chatId = chatId;
            this.stage = INITIAL_PROGRESS;
        }

        void clean() {
            stage = INITIAL_PROGRESS;
        }

        void decrementProgress() {
            if (stage > INITIAL_PROGRESS) {
                stage--;
            }
        }
    }
}