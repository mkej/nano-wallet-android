package co.nano.nanowallet;

import android.content.SharedPreferences;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.security.SecureRandom;
import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity {
    private WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        // check if we have a wallet. if none, send intent to welcome wizard, return with valid seed
        // for now, either load an existing seed or generate a new one
        SharedPreferences pref = this.getSharedPreferences("co.nano.nanowallet", Context.MODE_PRIVATE);
        String encryptedSeedHex = "9F1D53E732E48F25F94711D5B22086778278624F715D9B2BEC8FB81134E7C904";//pref.getString("seed", null);
        if (encryptedSeedHex == null) {
            SecureRandom random = new SecureRandom();
            byte seed[] = new byte[32];
            random.nextBytes(seed);
            encryptedSeedHex = NanoUtil.bytesToHex(seed);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("seed", encryptedSeedHex);
            edit.commit();
        }

        Log.i("Wallet", "Seed " + encryptedSeedHex);
        String private_key = NanoUtil.seedToPrivate(encryptedSeedHex);
        Log.i("Wallet", "Private " + private_key);
        String public_address = NanoUtil.privateToPublic(private_key);
        Log.i("Wallet", "Public " + public_address);
        Log.i("Wallet", "Address " + NanoUtil.publicToAddress(public_address));

        // load our wallet view
        // setContentView(R.layout.activity_main);
        // we have a wallet, lets connect and sync with the server
        // connectWebSocket();

    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("wss://raicast.lightrai.com:443");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("{\"action\":\"block_count\"}");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                //runOnUiThread(new Runnable() {
                //    @Override
                //    public void run() {
                //        TextView textView = (TextView)findViewById(R.id.messages);
                //        textView.setText(textView.getText() + "\n" + message);
                //    }
                //});
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage(View view) {
        EditText editText = (EditText)findViewById(R.id.message);
        mWebSocketClient.send(editText.getText().toString());
        editText.setText("");
    }

}