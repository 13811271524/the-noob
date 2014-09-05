package com.android.luoqi;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

public class About extends Activity {
	private Button backword;
	private Intent back;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.about);
		backword = (Button) findViewById(R.id.back1);
		backword.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View v){
	        back = new Intent();
	        back.setClass(About.this,MainActivity.class);
	        startActivity(back);
	        About.this.finish();
	        }
	    });
		}
}
