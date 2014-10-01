package weatherlink;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Connects to a Vantage Vue and sends it commands to periodically reply with
 * weather data.
 *
 * @author mjrb5
 */
public class VueLooper {

    private SerialPort serialPort;
    private volatile boolean stop;

    /**
     * Create a view looper on a specific port, eg. "COM7".
     *
     * @param port the port to use.
     */
    public VueLooper(String port) {
        serialPort = new SerialPort(port);
    }

    /**
     * Start "looping", which means the station will periodically reply with
     * weather data (once every 2-3 seconds or so.)
     *
     * @param callback the callback which periodically provides weather data.
     */
    public void loop(LoopCallback callback) {
        new Thread() {
            public void run() {
                try {
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_19200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    serialPort.writeBytes("LPS 3\n".getBytes());
                    serialPort.readBytes(1);
                    while (!stop) {
                        byte[] arr = serialPort.readBytes(99);
                        byte[] arr2 = serialPort.readBytes(99);
                        WeatherLoopPacket packet = new WeatherLoopPacket(arr, arr2);
                        callback.weatherDataSent(packet);
                    }
                    serialPort.closePort();
                } catch (SerialPortException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Stop looping. When the looper is stopped, it cannot be restarted (a new
     * VueLooper must be created.)
     */
    public void stop() {
        stop = true;
    }

}
