package com.pos.demoui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		Intent intent=getIntent();
		intent.putExtra("name", "qianmeng");
		intent.putExtra("isSuccess", true);
		this.setResult(201);
	}
}
