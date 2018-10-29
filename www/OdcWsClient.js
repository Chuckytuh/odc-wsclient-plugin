var exec = require('cordova/exec');

function Semaphore() {};

Semaphore.prototype.onColorChanged = function (color) {
    console.log("[Debug] onColorChanged:", color);
};

Semaphore.prototype.onConnectionClosed = function (result) {
    console.log("[Debug] onConnectionClosed:", result);
};

var semaphore;
exports.setSemaphore = function (newSemaphore) {
    semaphore = newSemaphore;
};

module.exports.Semaphore = Semaphore;

module.exports.openConnection = function (ip, port, success, error) {
    console.log("[Debug] openConnection", ip, port);
    if (semaphore === undefined) {
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
            semaphore.onColorChanged(result.color);
        } else if (result.type === "onOpen") {
            success(result);
        } else if(result.type === "onClose") {
            semaphore.onConnectionClosed(result);
        } else {
            error(result);
        }
    }

    exec(innerSuccess, error, 'OdcWsClient', 'openConnection', [ip, port]);
};