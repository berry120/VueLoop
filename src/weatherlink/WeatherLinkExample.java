package weatherlink;

/**
 * A simple example program.
 * @author mjrb5
 */
public class WeatherLinkExample {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new VueLooper("COM7").loop(new LoopCallback() {

            @Override
            public void weatherDataSent(WeatherLoopPacket packet) {
                System.out.println(packet);
            }
        });
    }

}
