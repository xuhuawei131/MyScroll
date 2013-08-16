package org.christ.myscroll;

import org.christ.myscroll.adapter.MyScrollAdapter;
import org.christ.myscroll.costomViews.MyScrollView;
import org.christ.myscroll.listener.OnItemSelectedChangeListener;
import org.christ.myscroll.listener.OnSelectedListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private MyScrollView<MyScrollAdapter> view;
	private TextView tx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		view = (MyScrollView<MyScrollAdapter>) findViewById(R.id.view);
		tx = (TextView) findViewById(R.id.tx);
		view.setAdapter(new MyScrollAdapter(this));
		view.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onItemChange(MyScrollView<? extends BaseAdapter> view, int positon, int oldPosition) {
				tx.setText(String.valueOf(positon));
			}
		});
		view.setOnSelectedListener(new OnSelectedListener() {

			@Override
			public void OnItemClick(MyScrollView<? extends BaseAdapter> view, int position) {
				Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
