package weatherlink;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
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
                            read();
                            while(buffer.size() >= 198) {
                                byte[] arr = get99();
                                byte[] arr2 = get99();
                                WeatherLoopPacket packet = new WeatherLoopPacket(arr, arr2);
                                callback.weatherDataSent(packet);
                                lastPacket = packet;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    serialPort.closePort();
                } catch (SerialPortException ex) {
                    ex.printStackTrace();
                }
            }
        };
        loopThread.start();
    }

    private Queue<Byte> buffer = new LinkedList<>();

    private void read() {
        try {
            byte[] arr = serialPort.readBytes(1, 5000);
            if (arr != null) {
                for (byte b : arr) {
                    buffer.add(b);
                }
            }
        } catch (Exception ex) {
            reset();
        }
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
            serialPort.writeBytes("LPS 3\n".getBytes());
            serialPort.readBytes(1, 5000);
        } catch(SerialPortTimeoutException ex) {
            reset();
        }
        catch (SerialPortException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private byte[] get99() {
        if (buffer.size() >= 99) {
            byte[] ret = new byte[99];
            for (int i = 0; i < 99; i++) {
                ret[i] = buffer.poll();
            }
            return ret;
        } else {
            return null;
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
