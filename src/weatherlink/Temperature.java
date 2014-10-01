/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weatherlink;

/**
 *
 * @author mjrb5
 */
public class Temperature {
    
    private double far;
    
    private Temperature(double far) {
        this.far = far;
    }
    
    public double getCelcius() {
        return (5.0/9)*(far-32);
    }
    
    public double getFarenheight() {
        return far;
    }
    
    static Temperature fromRaw(double raw) {
        return new Temperature(raw);
    }
    
    @Override
    public String toString() {
        return getCelcius()+"°C";
    }
    
}
