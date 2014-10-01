/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weatherlink;

import jssc.SerialPort;

/**
 *
 * @author mjrb5
 */
public class WeatherLink {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SerialPort serialPort = new SerialPort("COM7");
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_19200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.writeBytes("LPS 3\n".getBytes());
            serialPort.readBytes(1);
            for (int i = 0; i < 1; i++) {
                byte[] arr = serialPort.readBytes(99);
                byte[] arr2 = serialPort.readBytes(99);
                WeatherLoopPacket packet = new WeatherLoopPacket(arr, arr2);
                System.out.println(packet);
            }
            serialPort.closePort();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
