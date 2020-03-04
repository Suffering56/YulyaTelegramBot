package pvs.tgbot.example;

import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static InputStream loadResource(String path) {
        return App.class.getResourceAsStream(path);
    }
}
