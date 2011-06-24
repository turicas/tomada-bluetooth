package com.cursodearduino.tomada;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.abraxas.amarino.Amarino;

public class Tomada extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	public static final String DEFAULT_DEVICE_ADDRESS = "00:3C:B8:B1:12:F0";
	
	public static final String PREFS = "tomada";
	public static final String PREF_DEVICE_ADDRESS = "device_address";
	
	SharedPreferences prefs;
	
	String deviceAddress;
	
	private Button buttonPower;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        // get references to views defined in our main.xml layout file
        buttonPower = (Button) findViewById(R.id.button);
        
        // registering listener
        buttonPower.setOnClickListener(this);
        
        // get device address
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        deviceAddress = prefs.getString(PREF_DEVICE_ADDRESS, DEFAULT_DEVICE_ADDRESS);
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    	
        Amarino.connect(this, deviceAddress);
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	
    	// stop Amarino's background service, we don't need it any more 
		Amarino.disconnect(this, deviceAddress);
    }
    
    public void onClick (View view){
    	updateTomada();
    }
    
    public void updateTomada(){
    	buttonPower.setText("Ligar");
    	Amarino.sendDataToArduino(this, deviceAddress, 'p', "power");
    }
}