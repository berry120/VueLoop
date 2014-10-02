package weatherlink;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Create a weather loop packet from the two 99 byte arrays (the original loop,
 * and the loop2 packets.)
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

    /**
     * Create a weather loop packet.
     *
     * @param arr the "loop" packet.
     * @param arr2 the "loop2" packet.
     */
    public WeatherLoopPacket(byte[] arr, byte[] arr2) {
        if (arr[4] == 1 && arr2[4] == 0) {
            byte[] temp = arr;
            arr = arr2;
            arr2 = temp;
        }
        if (arr[4] != 0) {
            this.arr = null;
        } else {
            this.arr = arr;
        }
        if (arr2[4] != 1) {
            this.arr2 = null;
        } else {
            this.arr2 = arr2;
        }
    }

    /**
     * Get the barometer trend.
     *
     * @return the barometer trend.
     */
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

    /**
     * Get the barometer reading (in bar.)
     *
     * @return the barometer reading (in bar.)
     */
    public double getBarometerReading() {
        double inches = get2byteUnsigned(arr[7], arr[8]);
        double bar = inches * 0.03386;
        return bar;
    }

    /**
     * Get the forecast data.
     *
     * @return the current forecast.
     */
    public Forecast getForecast() {
        switch (arr[89]) {
            case 8:
                return Forecast.MOSTLY_CLEAR;
            case 6:
                return Forecast.PARTLY_CLOUDY;
            case 2:
                return Forecast.MOSTLY_CLOUDY;
            case 3:
                return Forecast.MOSTLY_CLOUDY_RAIN_12HOURS;
            case 18:
                return Forecast.MOSTLY_CLOUDY_SNOW_12HOURS;
            case 19:
                return Forecast.MOSTLY_CLOUDY_RAIN_OR_SNOW_12HOURS;
            case 7:
                return Forecast.PARTLY_CLOUDY_RAIN_12HOURS;
            case 22:
                return Forecast.PARTLY_CLOUDY_SNOW_12HOURS;
            case 23:
                return Forecast.PARTLY_CLOUDY_RAIN_OR_SNOW_12HOURS;
        }
        return Forecast.UNKNOWN;
    }

    /**
     * Get the current inside temperature.
     *
     * @return the current inside temperature.
     */
    public Temperature getInsideTemperature() {
        double far = (get2byteUnsigned(arr[9], arr[10]) * 0.1);
        return Temperature.fromFar(far);
    }

    /**
     * Get the current relative inside humidity (in %.)
     *
     * @return the current relative inside humidity (in %.)
     */
    public int getInsideHumidity() {
        return arr[11];
    }

    /**
     * Get the current outside temperature.
     *
     * @return the current outside temperature.
     */
    public Temperature getOutsideTemperature() {
        double far = (get2byteUnsigned(arr[12], arr[13]) * 0.1);
        return Temperature.fromFar(far);
    }

    /**
     * Get the current wind speed (in mph.)
     *
     * @return the current wind speed (in mph.)
     */
    public int getWindSpeed() {
        return arr[14] & 0xFF;
    }

    /**
     * Get the 10 minute average wind speed (in mph.)
     *
     * @return the 10 minute average wind speed (in mph.)
     */
    public double get10MinAverageWindSpeed() {
        return get2byteUnsigned(arr2[18], arr2[19]) * 0.1;
    }

    /**
     * Get the 10 minute wind gust (in mph.)
     *
     * @return the 10 minute wind gust (in mph.)
     */
    public double get10MinWindGust() {
        return get2byteUnsigned(arr2[22], arr2[23]) * 0.1;
    }

    /**
     * Get the 10 minute wind gust direction. 0 indicates invalid data, valid
     * points are between 1-360 degrees.
     *
     * @return the 10 minute wind gust direction.
     */
    public int getWindGustDirection() {
        return get2byteUnsigned(arr2[24], arr2[25]);
    }

    /**
     * Get the 2 minute average wind speed (in mph.)
     *
     * @return the 2 minute average wind speed (in mph.)
     */
    public double get2MinAverageWindSpeed() {
        return get2byteUnsigned(arr2[20], arr2[21]) * 0.1;
    }

    /**
     * Get the current dew point temperature.
     *
     * @return the current dew point.
     */
    public Temperature getDewPoint() {
        return Temperature.fromFar(get2byteUnsigned(arr2[30], arr2[31]));
    }

    /**
     * Get the current heat index temperature.
     *
     * @return the current heat index temperature.
     */
    public Temperature getHeatInex() {
        return Temperature.fromFar(get2byteUnsigned(arr[35], arr[36]));
    }

    /**
     * Get the wind chill temperature.
     *
     * @return the wind chill temperature.
     */
    public Temperature getWindChill() {
        return Temperature.fromFar(get2byteUnsigned(arr2[37], arr2[38]));
    }

    /**
     * Get the THSW index temperature.
     *
     * @return the THSW index temperature.
     */
    public Temperature getTHSWIndex() {
        return Temperature.fromFar(get2byteUnsigned(arr[39], arr[40]));
    }

    /**
     * Get the current wind direction. 0 indicates invalid data, valid points
     * are between 1-360 degrees.
     *
     * @return the current wind direction.
     */
    public int getWindDirection() {
        return get2byteUnsigned(arr[16], arr[17]);
    }

    /**
     * Get the relative outside humidity (in %).
     *
     * @return the relative outside humidity (in %).
     */
    public int getOutsideHumidity() {
        return arr[33];
    }

    /**
     * Get the rain rate (in mm per hour.)
     *
     * @return the rain rate (in mm per hour.)
     */
    public double getRainRate() {
        return get2byteUnsigned(arr[41], arr[42]) * 0.0254;
    }

    /**
     * Get the current UV Index.
     *
     * @return the current UV Index.
     */
    public double getUVIndex() {
        return arr[43];
    }

    /**
     * Get the current level of solar radiation.
     *
     * @return the current level of solar radiation.
     */
    public int getSolarRadiation() {
        return get2byteUnsigned(arr[44], arr[45]);
    }

    /**
     * Get the storm rain rate (in mm per hour.)
     *
     * @return the storm rain rate (in mm per hour.)
     */
    public double getStormRain() {
        return get2byteUnsigned(arr[46], arr[47]) * 0.0254;
    }

    /**
     * Get the current storm start date.
     *
     * @return the current storm start date.
     */
    public Date getStormStartDate() {
        int data = get2byteUnsigned(arr[48], arr[49]);
        if (data == 65535) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.clear();
        return c.getTime();
    }

    /**
     * Get the last day's rain level (in mm.)
     * @return the last day's rain level (in mm.)
     */
    public double getDayRain() {
        return get2byteUnsigned(arr[50], arr[51]) * 0.0254;
    }
    /**
     * Get the last 15 minute's rain level (in mm.)
     *
     * @return the last 15 minute's rain level (in mm.)
     */
    public double getLast15MinsRain() {
        return get2byteUnsigned(arr2[52], arr2[53]) * 0.0254;
    }

    /**
     * Get the last hour's rain level (in mm.)
     *
     * @return the last hour's rain level (in mm.)
     */
    public double getLastHourRain() {
        return get2byteUnsigned(arr2[54], arr2[55]) * 0.0254;
    }

    /**
     * Get the last month's rain level (in mm.)
     *
     * @return the last month's rain level (in mm.)
     */
    public double getMonthRain() {
        return get2byteUnsigned(arr[52], arr[53]) * 0.0254;
    }
    /**
     * Get the last year's rain level (in mm.)
     *
     * @return the last year's rain level (in mm.)
     */
    public double getYearRain() {
        return get2byteUnsigned(arr[54], arr[55]) * 0.0254;
    }

    /**
     * Get the day evapotranspiration level (in mm.) 
     *
     * @return the day evapotranspiration level (in mm.)
     */
    public double getDayET() {
        return get2byteUnsigned(arr[56], arr[57]) * 0.0254;
    }

    /**
     * Get the month evapotranspiration level (in mm.)
     *
     * @return the month evapotranspiration level (in mm.)
     */
    public double getMonthET() {
        return get2byteUnsigned(arr[58], arr[59]) * 0.254;
    }

    /**
     * Get the year evapotranspiration level (in mm.)
     *
     * @return the year evapotranspiration level (in mm.)
     */
    public double getYearET() {
        return get2byteUnsigned(arr[60], arr[61]) * 0.254;
    }

    /**
     * Get the transmitter's battery status (further details unknown.)
     * @return the transmitter's battery status.
     */
    public int getTransmitterBatteryStatus() {
        return arr[86];
    }

    /**
     * Get the console's current battery voltage.
     * @return the console's current battery voltage.
     */
    public double getConsoleBatteryVoltage() {
        return ((get2byteUnsigned(arr[87], arr[88]) * 300) / 512) / 100.0;
    }

    @Override
    public String toString() {
        return "Barometer reading: " + getBarometerReading() + "\n"
                + "Barometer trend: " + getBarometerTrend().toString() + "\n"
                + "Forecast: " + getForecast() + "\n"
                + "Inside temp: " + getInsideTemperature().toString() + "\n"
                + "Outside temp: " + getOutsideTemperature().toString() + "\n"
                + "Wind speed: " + getWindSpeed() + "\n"
                + "10 Min average wind speed: " + get10MinAverageWindSpeed() + "\n"
                + "2 Min average wind speed: " + get2MinAverageWindSpeed() + "\n"
                + "Wind direction: " + getWindDirection() + "\n"
                + "10 Min wind gust: " + get10MinWindGust() + "\n"
                + "10 Min wind gust direction: " + getWindGustDirection() + "\n"
                + "Wind chill: " + getWindChill() + "\n"
                + "Inside humidity: " + getInsideHumidity() + "\n"
                + "Outside humidity: " + getOutsideHumidity() + "\n"
                + "UV Index: " + getUVIndex() + "\n"
                + "Storm Rain: " + getStormRain() + "\n"
                + "Storm start date: " + getStormStartDate() + "\n"
                + "Last 15 mins Rain: " + getLast15MinsRain() + "\n"
                + "Last Hour Rain: " + getLastHourRain() + "\n"
                + "Day Rain: " + getDayRain() + "\n"
                + "Month Rain: " + getMonthRain() + "\n"
                + "Year Rain: " + getYearRain() + "\n"
                + "Dew point: " + getDewPoint() + "\n"
                + "Day ET: " + getDayET() + "\n"
                + "Month ET: " + getMonthET() + "\n"
                + "Year ET: " + getYearET() + "\n"
                + "Console battery voltage: " + getConsoleBatteryVoltage() + "\n"
                + "Outside battery status: " + getTransmitterBatteryStatus();
    }

    /**
     * Convenience method for providing an int from 2 byte values.
     * @param x the first byte (least significant)
     * @param y the second byte (most significant)
     * @return the int formed from the two bytes.
     */
    private static int get2byteUnsigned(byte x, byte y) {
        return (y << 8 & 0xFF00 | x & 0xFF);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Arrays.hashCode(this.arr);
        hash = 53 * hash + Arrays.hashCode(this.arr2);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WeatherLoopPacket other = (WeatherLoopPacket) obj;
        if (!Arrays.equals(this.arr, other.arr)) {
            return false;
        }
        if (!Arrays.equals(this.arr2, other.arr2)) {
            return false;
        }
        return true;
    }
    
}
