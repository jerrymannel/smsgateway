package com.jerrymannel.smsgateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import com.jerrymannel.smsgateway.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MySMSGatewayMainActivity extends Activity {

	private LinearLayout linerLayout_server;
	private LinearLayout linearLayout_message;
	private Switch switch_server;
	private TextView textView_serverStatus;
	private TextView textView_comment;
	private SharedPreferences prefs;

	private String ipAddress;
	private int port;
	private HTTPServer server;
	private SimpleDateFormat sdf;
	private String currentTime;

	private SmsManager sms;
	private String phoneNumber;
	private String message;

	private static final String TAG = "mysmsgateway";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkNetworkState();
		getLocalIpAddress();

		sms = SmsManager.getDefault();

		prefs = this.getSharedPreferences("com.jerrymannel.mysmsgateway",
				Context.MODE_PRIVATE);
		if (prefs.getInt("port", 0) == 0) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("port", 18080);
			editor.commit();
			port = 18080;
		}

		linerLayout_server = (LinearLayout) findViewById(R.id.linearLayout_server);
		linearLayout_message = (LinearLayout) findViewById(R.id.linearLayout_message);
		textView_serverStatus = (TextView) findViewById(R.id.textView_serverStaus);
		textView_comment = (TextView) findViewById(R.id.textView_comment);
		switch_server = (Switch) findViewById(R.id.switch_server);
		server = null;

		switch_server.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (checkNetworkState()) {
					if (isChecked) {
						textView_serverStatus.setText(R.string.serverOn);
						linerLayout_server
								.setBackgroundResource(R.drawable.backgroud_start);
						port = prefs.getInt("port", 0);
						Log.i(TAG, "Port set to " + port);
						textView_comment
								.setText(getString(R.string.connectComment)
										+ "http://" + ipAddress + ":" + port);

						Log.i(TAG, "Starting server ...");
						server = new HTTPServer();
						server.execute("");
					} else {
						textView_serverStatus.setText(R.string.serverOff);
						linerLayout_server
								.setBackgroundResource(R.drawable.backgroud_stop);
						textView_comment.setText(R.string.stopComment);
						server.cancel(true);
						server = null;
						Log.i(TAG, "Server stopped!");
					}
				} else {
					switch_server.toggle();
				}
			}
		});
	}

	protected void onPause() {
		Log.i(TAG, "App has gone into pause mode. Stopping server!");
		if (server != null)
			server.cancel(true);
		textView_serverStatus.setText(R.string.serverOff);
		linerLayout_server.setBackgroundResource(R.drawable.backgroud_stop);
		textView_comment.setText(R.string.initialComment);
		switch_server.setChecked(false);
		super.onPause();
	}

	protected void onDestroy() {
		Log.i(TAG, "Murderer!!! The app has been killed!. Stopping server!");
		if (server != null)
			server.cancel(true);
		textView_serverStatus.setText(R.string.serverOff);
		linerLayout_server.setBackgroundResource(R.drawable.backgroud_stop);
		textView_comment.setText(R.string.initialComment);
		switch_server.setChecked(false);
		super.onPause();
	}

	protected void onRestart() {
		Log.i(TAG, "App restart has occured.");
		if (server != null)
			server.cancel(true);
		textView_serverStatus.setText(R.string.serverOff);
		linerLayout_server.setBackgroundResource(R.drawable.backgroud_stop);
		textView_comment.setText(R.string.initialComment);
		switch_server.setChecked(false);
		super.onPause();
		super.onRestart();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.options_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_clear:
			linearLayout_message.removeAllViews();
			break;
		case R.id.item_setting:
			startActivity(new Intent(this, SettingsView.class));
			break;
		case R.id.item_about:
			startActivity(new Intent(this, AboutView.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean checkNetworkState() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		showAlert("No active network connections \navailable.");
		return false;
	}

	private void showAlert(final String s) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(MySMSGatewayMainActivity.this, s,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (intf.getName().contentEquals("wlan0")) {
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							ipAddress = inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		} catch (SocketException ex) {
			System.out.println(ex.toString());
		}
	}

	private void sendSMS() {
		sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();
		currentTime = sdf.format(new Date());

		final MessageView v1 = new MessageView(
				linearLayout_message.getContext(), null);

		try {
			sms.sendTextMessage(phoneNumber, null, message, null, null);
			v1.setData(phoneNumber, message, currentTime);
			showAlert("Message Sent!");
		} catch (Exception e) {
			showAlert("SMS failed, please try again later!");
			v1.setData(phoneNumber, "[FAIL]" + message, currentTime);
			e.printStackTrace();
		}

		runOnUiThread(new Runnable() {
			public void run() {
				linearLayout_message.addView(v1);
			}
		});

	}

	private class HTTPServer extends AsyncTask<String, Void, Void> {
		protected Void doInBackground(String... params) {
			try {
				ServerSocket server = new ServerSocket(port);
				Log.i(TAG, "Port Set. Server started!");
				while (true) {
					Socket socket = server.accept();
					if (isCancelled()) {
						socket.close();
						server.close();
						break;
					}
					BufferedReader in = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					DataOutputStream out = new DataOutputStream(
							socket.getOutputStream());
					// get the first line of the HTTP GET request
					// sample : GET /?phone=+911234567890&message=HelloWorld HTTP/1.1
					String data = in.readLine();
					// get the substring after GET /?
					data = data.substring(6);
					
					// if the URL doesn't contain the sting phone, do nothing.
					if (!data.contains("phone")) {
						Log.i(TAG, "Invalid URL");
						showAlert("Invalid URL");
					} else {
						// get the data before  HTTP/1.1
						data = data.substring(0, data.length() - 9);
						String[] myparams = data.split("&");
						if (data.contains("=")) {
							phoneNumber = myparams[0].split("=")[1];
							message = myparams[1].split("=")[1];
							Log.i(TAG, "Got a request to sent an SMS.");
							Log.i(TAG, "Phone Number: " + phoneNumber);
							Log.i(TAG, "Message: " + message);

							sendSMS();
						}
					}

					out.writeBytes("HTTP/1.1 200 OK \r\n");
					out.writeBytes("Connection: close\r\n");
					out.writeBytes("\r\n");
					out.close();
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
