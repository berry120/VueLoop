package weatherlink;

/**
 * Manages temperature conversions.
 * @author mjrb5
 */
public class Temperature {
    
    private double far;
    
    private Temperature(double far) {
        this.far = far;
    }
    
    /**
     * Get the temperature in celcius.
     *
     * @return the temperature in celcius.
     */
    public double getCelcius() {
        return (5.0/9)*(far-32);
    }
    
    /**
     * Get the temperature in Farenheight
     * @return the temperature in Farenheight
     */
    public double getFarenheight() {
        return far;
    }
    
    /**
     * Create a temperature object from the temperature in Farenheight.
     * @param far the temperature in Farenheight.
     * @return the Temperature object.
     */
    static Temperature fromFar(double far) {
        return new Temperature(far);
    }
    
    /**
     * Get a string representation of this temperature (defaults to Celcius.)
     * @return a string representation of this temperature.
     */
    @Override
    public String toString() {
        return getCelcius()+"°C";
    }
    
}
