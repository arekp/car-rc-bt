package pl.arekp.car_control;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class Car_controlActivity extends Activity implements
		SensorEventListener {
	private SensorManager sensorManager;
	private boolean color = false;
	private View view;
	private long lastUpdate;

	public static final String DEFAULT_DEVICE_ADDRESS = "00:11:12:14:00:93";
	public static final String PREF_DEVICE_ADDRESS = "device_address";
	private static final int addressEditTextId = 15;
	SharedPreferences prefs;
	String deviceAddress;
	private static final int DIALOG_DEVICE_ADDRESS = 1;

	private TextView textViewX;
	private TextView textViewY;
	private TextView textViewZ;
	private RatingBar predkosc;

	private DrawView draw;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		view = findViewById(R.id.textView);
		view.setBackgroundColor(Color.GREEN);
		
		textViewX = (TextView) findViewById(R.id.textViewX);
		textViewY = (TextView) findViewById(R.id.textViewY);
		textViewZ = (TextView) findViewById(R.id.textViewZ);
		predkosc = (RatingBar) findViewById(R.id.ratingBar1);
		draw =(DrawView)findViewById(R.id.radar);
	

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		lastUpdate = System.currentTimeMillis();

		// pobieramy adres BT 
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		deviceAddress = prefs.getString(PREF_DEVICE_ADDRESS,
				DEFAULT_DEVICE_ADDRESS);

	}

	private BroadcastReceiver connectionStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (AmarinoIntent.ACTION_CONNECTED.equals(action)) {
					//nas³uch na dane 
				}
			}
		}
	};

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}

	}

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;

		float x = values[0];
		float y = values[1];
		float z = values[2];
		textViewX.setText(Float.toString(x));
		textViewY.setText(Float.toString(y));
		textViewZ.setText(Float.toString(z));
predkosc.setRating(x);

		Amarino.sendDataToArduino(this, deviceAddress, 'A',
				event.values.clone());

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if (accelationSquareRoot >= 2) //
		{
			if (actualTime - lastUpdate < 200) {
				return;
			}
			lastUpdate = actualTime;
			Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
					.show();
			if (color) {
				view.setBackgroundColor(Color.GREEN);

			} else {
				view.setBackgroundColor(Color.RED);
			}
			color = !color;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	protected void onStart() {
		super.onStart();

		registerReceiver(connectionStateReceiver, new IntentFilter(
				AmarinoIntent.ACTION_CONNECTED));
		Amarino.connect(this, deviceAddress);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// register this class as a listener for the orientation and
		// accelerometer sensors
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		// unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	// ////////// MENU /////////////////////////////////
	// //////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.device_address:
			showDialog(DIALOG_DEVICE_ADDRESS);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case DIALOG_DEVICE_ADDRESS:
			final EditText addressEditText = new EditText(this);
			addressEditText.setId(addressEditTextId);
			addressEditText.setText(deviceAddress);

			return new AlertDialog.Builder(this)
					.setTitle(R.string.device_address)
					.setMessage(R.string.set_device_address)
					.setView(addressEditText)
					.setPositiveButton("Save", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String address = addressEditText.getEditableText()
									.toString();
							if (Amarino.isCorrectAddressFormat(address)) {
								prefs.edit()
										.putString(PREF_DEVICE_ADDRESS, address)
										.commit();
							} else {
								Toast.makeText(Car_controlActivity.this,
										R.string.device_address_format_error,
										Toast.LENGTH_LONG).show();
							}

						}
					}).setNegativeButton("Discard", null).create();

		default:
			return super.onCreateDialog(id);
		}

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		EditText addressEditText = (EditText) dialog
				.findViewById(addressEditTextId);
		addressEditText.setText(prefs.getString(PREF_DEVICE_ADDRESS,
				DEFAULT_DEVICE_ADDRESS));
		super.onPrepareDialog(id, dialog);
	}

}