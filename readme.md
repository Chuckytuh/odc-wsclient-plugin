# OdcWsClient Plugin

Sample plugin for "Step up your game with native plugins" session on OutSystems Developer Conference 2018.

## Usage

```javascript
// The "TrafficLight" object that receives the events whenever the color changes
var trafficLight = new cordova.plugins.OdcWsClient.TrafficLight();

// Example definition of a callback for the onColorChanged event
trafficLight.onColorChanged = function(color) {
    // Do something with the new color!
};

// Required setter to define which Semaphore will react to color change events.
cordova.plugins.OdcWsClient.setTrafficLight(trafficLight);


// Call to open a connection with the TrafficLight server through which we receive
// the updated colors.
// Note: Requires a TrafficLight previously set.
cordova.plugins.OdcWsClient.openConnection(ip, port, success, fail);

```
