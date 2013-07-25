package com.jerrymannel.smsgateway;

import com.jerrymannel.smsgateway.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView textView2 = (TextView) findViewById(R.id.textView2);
		textView2.setText("Open the app and start the server. The app displays the URL to which the request has to be made.\n\n" + 
						"The HTTP GET URL is as shown below,\n\n" + 
						"http://<device ip>:18080/?<phone number>=<short message>\n\n" + 
						"e.g. http://192.168.2.3:18080/?phone=+919912345678&message=HelloWorld\n\n" +
						"The app MUST be running in the foreground to access the service. If the app closes or goes into the background, the server stops automatically.");
	}
}
