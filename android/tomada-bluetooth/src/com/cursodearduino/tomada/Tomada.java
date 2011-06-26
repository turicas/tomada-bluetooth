package com.cursodearduino.tomada;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class Tomada extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	public static final String DEFAULT_DEVICE_ADDRESS = "00:3C:B8:B1:12:F0";
	
	public static final String PREFS = "tomada";
	public static final String PREF_DEVICE_ADDRESS = "device_address";
	
	private ArduinoMessageReceiver arduinoMessageReceiver = new ArduinoMessageReceiver();
	private ArduinoConnectionVerifier arduinoConnectionVerifier = new ArduinoConnectionVerifier();
	
	SharedPreferences prefs;
	
	String deviceAddress;
	
	private Button buttonPower;
	private TextView arduinoFeedback;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        // get references to views defined in our main.xml layout file
        buttonPower = (Button) findViewById(R.id.button);
        arduinoFeedback = (TextView) findViewById(R.id.arduinofeedback);
        
        // registering listener
        buttonPower.setOnClickListener(this);
        
        // get device address
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        deviceAddress = prefs.getString(PREF_DEVICE_ADDRESS, DEFAULT_DEVICE_ADDRESS);
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    	
    	// in order to receive broadcasted intents we need to register our receiver
    	registerReceiver(arduinoMessageReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
    	registerReceiver(arduinoConnectionVerifier, new IntentFilter(AmarinoIntent.ACTION_CONNECTED));
    	
        Amarino.connect(this, deviceAddress);
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	
    	// stop Amarino's background service, we don't need it any more 
		Amarino.disconnect(this, deviceAddress);
		
		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoMessageReceiver);
		unregisterReceiver(arduinoConnectionVerifier);
    }
    
    public void verifyTomada(){
    	Amarino.sendDataToArduino(this, deviceAddress, 's', "status");
    }
    
    public void onClick (View view){
    	updateTomada();
    }
    
    public void updateTomada(){
    	buttonPower.setText("Ligar");
    	Amarino.sendDataToArduino(this, deviceAddress, 'p', "power");
    }
    
    public class ArduinoMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String data = null;
			
			// the device address from which the data was sent, we don't need it here but to demonstrate how you retrieve it
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support differnt data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				
				if (data != null){
					String text = "The electrical socket is ";
					arduinoFeedback.setText(text.concat(data));
				}
			}
		}
	}


    public class ArduinoConnectionVerifier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			verifyTomada();
		}
	}
}