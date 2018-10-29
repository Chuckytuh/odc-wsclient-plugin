var exec = require('cordova/exec');

function Semaphore() {};

Semaphore.prototype.onColorChanged = function (color) {
    console.log("[Debug] onColorChanged:", color);
};

exports.setSemaphore = function (semaphore) {
    this.semaphore = semaphore;
};

module.exports.Semaphore = Semaphore;

module.exports.openConnection = function (ip, port, success, error) {
    if (this.semaphore === undefined) {
        error("Semaphore not set! Please call setSemaphore first.");
        return;
    }
    // {"type": "onOpen"}
    var innerSuccess = function (result) {
        if (result === undefined) {
            return;
        }

        if (result.type === "onColorChanged") {
            this.semaphore.onColorChanged(result.color);
            return;
        }

        if (result.type === "onOpen" || result.type === "onClose") {
            success(result);
        } else {
            success(result);
        }
    }

    exec(innerSuccess, error, 'OdcWsClient', 'openConnection', [ip, port]);
};