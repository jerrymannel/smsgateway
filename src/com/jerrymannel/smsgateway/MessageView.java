package com.jerrymannel.smsgateway;

import com.jerrymannel.smsgateway.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageView extends LinearLayout {
	
	private TextView textView_number;
	private TextView textView_message;
	private TextView textView_timeStamp;
	
	public MessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.messagehistory, this);
		
		textView_number = (TextView) findViewById(R.id.textView_number);
		textView_message = (TextView) findViewById(R.id.textView_message);
		textView_timeStamp = (TextView) findViewById(R.id.textView_timeStamp);
	}

	public void setData(String number, String message, String timeStamp){
		textView_number.setText(number);
		textView_message.setText(message);
		textView_timeStamp.setText(timeStamp);
	}
}
