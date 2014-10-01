VueLoop
=======

Simple library for getting weather data from the Vantage Vue

Example usage:

    new VueLooper("COM7").loop(new LoopCallback() {
    
        @Override
        public void weatherDataSent(WeatherLoopPacket packet) {
            System.out.println(packet);
        }
    });
