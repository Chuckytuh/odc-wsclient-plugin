package com.outsystems.odc.wsclient.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

public class OdcWsClient extends CordovaPlugin {
    WebSocketClient webSocketClient;
    CallbackContext callbackContext;

    AtomicBoolean isAlreadyHandlingConnection = new AtomicBoolean(false);

    @Override
    public void onReset() {
        super.onReset();
        if (webSocketClient != null && isAlreadyHandlingConnection.get()) {
            webSocketClient.close();
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("openConnection")) {
            String ip = args.getString(0);
            String port = args.getString(1);
            this.openConnection(callbackContext, ip, port);
            return true;
        }
        return false;
    }

    private void openConnection(final CallbackContext callbackContext, String ip, String port) {
        if (isAlreadyHandlingConnection.get()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "onError");
                jsonObject.put("error", "Already attempting to connect, try again later.");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, jsonObject);
            callbackContext.sendPluginResult(result);
            return;
        }

        isAlreadyHandlingConnection.set(true);

        this.callbackContext = callbackContext;

        StringBuilder sb = new StringBuilder();
        sb.append("ws://").append(ip).append(":").append(port);

        URI serverUri;

        try {
            serverUri = new URI(sb.toString());
        } catch (URISyntaxException e) {
            callbackContext.error(e.getMessage());
            return;
        }


        this.webSocketClient = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                OdcWsClient.this.callbackContext.sendPluginResult(buildSuccessResultOpenConnection());
            }

            @Override
            public void onMessage(String message) {
                OdcWsClient.this.callbackContext.sendPluginResult(buildSuccessResultWithColor(message));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                isAlreadyHandlingConnection.set(false);
                OdcWsClient.this.callbackContext.sendPluginResult(buildSuccessResultOnClose(code, reason, remote));
            }

            @Override
            public void onError(Exception ex) {
                OdcWsClient.this.callbackContext.sendPluginResult(buildSuccessResultOnError(ex.getMessage()));
            }
        };


        this.webSocketClient.connect();

    }

    private PluginResult buildSuccessResultWithColor(String color) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "onColorChanged");
            jsonObject.put("color", color);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
        result.setKeepCallback(true);
        return result;
    }


    private PluginResult buildSuccessResultOpenConnection() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "onOpen");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
        result.setKeepCallback(true);
        return result;
    }

    private PluginResult buildSuccessResultOnError(String errorMessage) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "onError");
            jsonObject.put("error", errorMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
        result.setKeepCallback(true);
        return result;
    }

    private PluginResult buildSuccessResultOnClose(int code, String reason, boolean remote) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "onClose");
            jsonObject.put("code", code);
            jsonObject.put("reason", reason);
            jsonObject.put("remote", remote);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
        result.setKeepCallback(false);
        return result;
    }

}
