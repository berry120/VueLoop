package org.berry120.vueloop;

/**
 * A callback called when weather data is received.
 * @author mjrb5
 */
public interface LoopCallback {
    
    /**
     * Weather data has been sent.
     * @param packet the weather data.
     */
    void weatherDataSent(WeatherLoopPacket packet);
    
}
