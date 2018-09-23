[![Build Status](https://travis-ci.org/berry120/VueLoop.svg?branch=master)](https://travis-ci.org/berry120/VueLoop)

VueLoop
=======

![Vantage Vue](https://d2j31icv6dlhz6.cloudfront.net/Q/faewPOTNuzMMOaxAJaCf/vanage-vue-complete-package.jpg)

Simple library for getting weather data from the Vantage Vue. Serial parameters are hard coded at present since all examples I've found have a baud rate of 19200, but submit an issue / PR if this isn't the case and I'll take a look.

Example usage:

    new VueLooper("COM7").loop(new LoopCallback() {
    
        @Override
        public void weatherDataSent(WeatherLoopPacket packet) {
            System.out.println(packet);
        }
    });
