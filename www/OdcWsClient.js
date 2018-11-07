var exec = require('cordova/exec');

function TrafficLight() {};

TrafficLight.prototype.onColorChanged = function (color) {
    console.log("[Debug] onColorChanged:", color);
};

TrafficLight.prototype.onConnectionClosed = function (result) {
    console.log("[Debug] onConnectionClosed:", result);
};

var trafficLight;
exports.setTrafficLight = function (newTrafficLight) {
    trafficLight = newTrafficLight;
};

module.exports.TrafficLight = TrafficLight;

module.exports.openConnection = function (ip, port, success, error) {
    console.log("[Debug] openConnection", ip, port);
    if (trafficLight === undefined) {
        console.error("[Debug] Semaphore not set! Please call setSemaphore first.");
        error({"type": "onError", "message": "Semaphore not set! Please call setSemaphore first."});
        return;
    }

    var innerSuccess = function (result) {
        console.log("[Debug] innerSuccess", result);
        if (result === undefined) {
            return;
        }
    
        if (result.type === "onColorChanged") {
            trafficLight.onColorChanged(result.color);
        } else if (result.type === "onOpen") {
            success(result);
        } else if(result.type === "onClose") {
            trafficLight.onConnectionClosed(result);
        } else {
            error(result);
        }
    }

    exec(innerSuccess, error, 'OdcWsClient', 'openConnection', [ip, port]);
};