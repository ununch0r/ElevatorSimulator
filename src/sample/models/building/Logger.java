package sample.models.building;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Logger {
    private  static java.util.logging.Logger logger= java.util.logging.Logger.getGlobal();
    public static  void ConfigLogger(){
        try {
            FileHandler handler=new FileHandler("Log.log");
            handler.setFormatter(new LogFormatter());

            logger.setUseParentHandlers(false);
            logger.addHandler(handler);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void Log(String message){
        logger.info(message);
    }
}
