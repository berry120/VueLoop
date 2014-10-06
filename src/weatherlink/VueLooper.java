package weatherlink;

import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 * Connects to a Vantage Vue and sends it commands to periodically reply with
 * weather data.
 *
 * @author mjrb5
 */
public class VueLooper {

    private static final Logger LOGGER = Logger.getLogger(VueLooper.class.getName());
    private SerialPort serialPort;
    private WeatherLoopPacket lastPacket;
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
        Thread loopThread = new Thread() {
            public void run() {
                try {
                    reset();
                    while (!stop) {
                        try {
                            if (!wakeup()) {
                                LOGGER.log(Level.WARNING, "Can't wake up... resetting");
                                reset();
                            }
                            serialPort.writeBytes("LPS 3 2\n".getBytes());
                            if (serialPort.readBytes(1)[0] != 6) {
                                LOGGER.log(Level.WARNING, "No ACK... resetting");
                                reset();
                            }
                            byte[] arr = serialPort.readBytes(99, 5000);
                            byte[] arr2 = serialPort.readBytes(99, 5000);
                            WeatherLoopPacket packet = new WeatherLoopPacket(arr, arr2);
                            if (!packet.equals(lastPacket)) {
                                callback.weatherDataSent(packet);
                            }
                            lastPacket = packet;
                        } catch (SerialPortException | SerialPortTimeoutException ex) {
                            LOGGER.log(Level.SEVERE, "Error in loop", ex);
                            reset();
                        }
                    }
                    serialPort.closePort();
                } catch (SerialPortException ex) {
                    LOGGER.log(Level.SEVERE, "Error in loop", ex);
                }
            }
        };
        loopThread.start();
    }

    private boolean wakeup() {
        for (int i = 0; i < 3; i++) {
            try {
                serialPort.writeByte((byte) 10);
                byte[] returned = serialPort.readBytes(2, 1200);
                if (returned[0] == '\n' && returned[1] == '\r') {
                    return true;
                }
            } catch (SerialPortException | SerialPortTimeoutException ex) {
                LOGGER.log(Level.SEVERE, "Error in wakeup", ex);
            }
        }
        return false;
    }

    private void reset() {
        try {
            if (serialPort.isOpened()) {
                serialPort.closePort();
                Thread.sleep(5000);
            }
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_19200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (SerialPortException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Error in reset", ex);
        }
    }

    /**
     * Stop looping. When the looper is stopped, it cannot be restarted (a new
     * VueLooper must be created.)
     */
    public void stop() {
        stop = true;
    }

}
