package weatherlink;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author mjrb5
 */
public class WeatherLoopPacket {

    public enum BarometerTrend {

        FALLING_RAPIDLY, FALLING_SLOWLY, STEADY, RISING_SLOWLY, RISING_RAPIDLY, UNKNOWN
    }
    
    public enum Forecast {
        MOSTLY_CLEAR, PARTLY_CLOUDY, MOSTLY_CLOUDY, MOSTLY_CLOUDY_RAIN_12HOURS, MOSTLY_CLOUDY_SNOW_12HOURS, MOSTLY_CLOUDY_RAIN_OR_SNOW_12HOURS,
        PARTLY_CLOUDY_RAIN_12HOURS, PARTLY_CLOUDY_SNOW_12HOURS, PARTLY_CLOUDY_RAIN_OR_SNOW_12HOURS, UNKNOWN
    }

    private byte[] arr;
    private byte[] arr2;

    public WeatherLoopPacket(byte[] arr, byte[] arr2) {
        if(arr[4]==1 && arr2[4]==0) {
            byte[] temp = arr;
            arr = arr2;
            arr2 = temp;
        }
        if(arr[4]!=0) {
            this.arr = null;
        }
        else {
            this.arr = arr;            
        }
        if(arr2[4]!=1) {
            this.arr2 = null;
        }
        else {
            this.arr2 = arr2;            
        }
    }

    public BarometerTrend getBarometerTrend() {
        switch (arr[3]) {
            case -60:
                return BarometerTrend.FALLING_RAPIDLY;
            case -20:
                return BarometerTrend.FALLING_SLOWLY;
            case 0:
                return BarometerTrend.STEADY;
            case 20:
                return BarometerTrend.RISING_SLOWLY;
            case 60:
                return BarometerTrend.RISING_RAPIDLY;
        }
        return BarometerTrend.UNKNOWN;
    }

    public double getBarometerReading() {
        double inches = get2byteUnsigned(arr[7], arr[8]);
        double bar = inches * 0.03386;
        return bar;
    }
    
    public Forecast getForecast() {
        switch(arr[89]) {
            case 8:return Forecast.MOSTLY_CLEAR;
            case 6:return Forecast.PARTLY_CLOUDY;
            case 2:return Forecast.MOSTLY_CLOUDY;
            case 3:return Forecast.MOSTLY_CLOUDY_RAIN_12HOURS;
            case 18:return Forecast.MOSTLY_CLOUDY_SNOW_12HOURS;
            case 19:return Forecast.MOSTLY_CLOUDY_RAIN_OR_SNOW_12HOURS;
            case 7:return Forecast.PARTLY_CLOUDY_RAIN_12HOURS;
            case 22:return Forecast.PARTLY_CLOUDY_SNOW_12HOURS;
            case 23:return Forecast.PARTLY_CLOUDY_RAIN_OR_SNOW_12HOURS;
        }
        return Forecast.UNKNOWN;
    }

    public Temperature getInsideTemperature() {
        double far = (get2byteUnsigned(arr[9], arr[10]) * 0.1);
        return Temperature.fromFar(far);
    }

    public int getInsideHumidity() {
        return arr[11];
    }
    
    public Temperature getOutsideTemperature() {
        double far = (get2byteUnsigned(arr[12], arr[13]) * 0.1);
        return Temperature.fromFar(far);
    }

    public int getWindSpeed() {
        return arr[14] & 0xFF;
    }

    public double get10MinAverageWindSpeed() {
        return get2byteUnsigned(arr2[18], arr2[19])*0.1;
    }

    public double get10MinWindGust() {
        return get2byteUnsigned(arr2[22], arr2[23])*0.1;
    }
    
    public int getWindGustDirection() {
        return get2byteUnsigned(arr2[24], arr2[25]);
    }

    public double get2MinAverageWindSpeed() {
        return get2byteUnsigned(arr2[20], arr2[21])*0.1;
    }
    
    public Temperature getDewPoint() {
        return Temperature.fromFar(get2byteUnsigned(arr2[30], arr2[31]));
    }
    
    public Temperature getHeatInex() {
        return Temperature.fromFar(get2byteUnsigned(arr[35], arr[36]));
    }
    
    public Temperature getWindChill() {
        return Temperature.fromFar(get2byteUnsigned(arr2[37], arr2[38]));
    }
    
    public Temperature getTHSWIndex() {
        return Temperature.fromFar(get2byteUnsigned(arr[39], arr[40]));
    }

    public int getWindDirection() {
        return get2byteUnsigned(arr[16], arr[17]);
    }

    public int getOutsideHumidity() {
        return arr[33];
    }

    public double getRainRate() {
        return get2byteUnsigned(arr[41], arr[42]) * 0.0254;
    }

    public double getUVIndex() {
        return arr[43];
    }

    public int getSolarRadiation() {
        return get2byteUnsigned(arr[44], arr[45]);
    }

    public double getStormRain() {
        return get2byteUnsigned(arr[46], arr[47]) * 0.0254;
    }

    public Date getStormStartDate() {
        int data = get2byteUnsigned(arr[48], arr[49]);
        if(data==65535) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.clear();
        return c.getTime();
    }
    
    public double getDayRain() {
        return get2byteUnsigned(arr[50], arr[51]) * 0.0254;
    }
    
    public double getLast15MinsRain() {
        return get2byteUnsigned(arr2[52], arr2[53]) * 0.0254;
    }
    
    public double getLastHourRain() {
        return get2byteUnsigned(arr2[54], arr2[55]) * 0.0254;
    }

    public double getMonthRain() {
        return get2byteUnsigned(arr[52], arr[53]) * 0.0254;
    }

    public double getYearRain() {
        return get2byteUnsigned(arr[54], arr[55]) * 0.0254;
    }

    public double getDayET() {
        return get2byteUnsigned(arr[56], arr[57]) * 0.0254;
    }

    public double getMonthET() {
        return get2byteUnsigned(arr[58], arr[59]) * 0.254;
    }

    public double getYearET() {
        return get2byteUnsigned(arr[60], arr[61]) * 0.254;
    }
    
    public int getTransmitterBatteryStatus() {
        return arr[86];
    }
    
    public double getConsoleBatteryVoltage() {
        return ((get2byteUnsigned(arr[87], arr[88]) * 300)/512)/100.0;
    }
    
    @Override
    public String toString() {
        return "Barometer reading: " + getBarometerReading() + "\n" +
                "Barometer trend: " + getBarometerTrend().toString() + "\n" +
                "Forecast: " + getForecast() + "\n" +
                "Inside temp: " + getInsideTemperature().toString() + "\n" +
                "Outside temp: " + getOutsideTemperature().toString() + "\n" +
                "Wind speed: " + getWindSpeed() + "\n" + 
                "10 Min average wind speed: " + get10MinAverageWindSpeed() + "\n" + 
                "2 Min average wind speed: " + get2MinAverageWindSpeed() + "\n" + 
                "Wind direction: " + getWindDirection() + "\n" + 
                "10 Min wind gust: " + get10MinWindGust() + "\n" + 
                "10 Min wind gust direction: " + getWindGustDirection() + "\n" + 
                "Wind chill: " + getWindChill() + "\n" + 
                "Inside humidity: " + getInsideHumidity() + "\n" + 
                "Outside humidity: " + getOutsideHumidity() + "\n" +
                "UV Index: " + getUVIndex() + "\n" +
                "Storm Rain: " + getStormRain() + "\n" + 
                "Storm start date: " + getStormStartDate() + "\n" + 
                "Last 15 mins Rain: " + getLast15MinsRain() + "\n" +
                "Last Hour Rain: " + getLastHourRain() + "\n" +
                "Day Rain: " + getDayRain() + "\n" +
                "Month Rain: " + getMonthRain() + "\n" + 
                "Year Rain: " + getYearRain() + "\n" +
                "Dew point: " + getDewPoint() + "\n" +
                "Day ET: " + getDayET() + "\n" +
                "Month ET: " + getMonthET() + "\n" +
                "Year ET: " + getYearET() + "\n" +
                "Console battery voltage: " + getConsoleBatteryVoltage() + "\n" + 
                "Outside battery status: " + getTransmitterBatteryStatus();
    }

    public static int get2byteUnsigned(byte x, byte y) {
        return (y << 8 & 0xFF00 | x & 0xFF);
    }

}
