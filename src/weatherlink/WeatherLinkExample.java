package weatherlink;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple example program.
 * @author mjrb5
 */
public class WeatherLinkExample {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        VueLooper looper = new VueLooper("COM3");
        looper.loop((WeatherLoopPacket packet) -> {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            System.out.println("Received at " + dateFormat.format(cal.getTime()) + ". Bar: " + packet.getBarometerReading() + " Temp: " + packet.getOutsideTemperature() + " Hum: " + packet.getOutsideHumidity() + "% Forecast: " + packet.getForecast() + " Bat: " + packet.getTransmitterBatteryStatus());
        });
    }

}
