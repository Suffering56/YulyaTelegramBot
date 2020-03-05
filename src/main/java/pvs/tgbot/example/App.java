package pvs.tgbot.example;

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @author v.peschaniy
 * Date: 27.02.2020
 */
@Log4j2
public class App {

    private static final int VERSION = 12;

    public static void main(String[] args) throws IOException, TelegramApiRequestException {
        log.info("YulyaTelegramBot started. Version: " + VERSION);

        Properties textProperties = new Properties();
        textProperties.load(new InputStreamReader(Utils.loadResource("/text.properties"), Charset.forName("cp1251")));

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        api.registerBot(new YulyaTelegramBot(textProperties));
    }
}
