[![Build Status](https://travis-ci.org/berry120/VueLoop.svg?branch=master)](https://travis-ci.org/berry120/VueLoop)

VueLoop
=======

Simple library for getting weather data from the Vantage Vue.

Example usage:

    new VueLooper("COM7").loop(new LoopCallback() {
    
        @Override
        public void weatherDataSent(WeatherLoopPacket packet) {
            System.out.println(packet);
        }
    });
