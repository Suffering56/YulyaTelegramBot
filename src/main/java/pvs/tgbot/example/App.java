package pvs.tgbot.example;

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
public class App {

    public static void main(String[] args) throws IOException, TelegramApiRequestException {

        System.out.println("YulyaTelegramBot started 3");

        Properties textProperties = new Properties();
        textProperties.load(new InputStreamReader(App.class.getResource("/text.properties").openStream(), Charset.forName("cp1251")));

        System.out.println("textProperties.getProperty(\"start\") = " + textProperties.getProperty("start"));

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        api.registerBot(new YulyaTelegramBot(textProperties));
    }
}
