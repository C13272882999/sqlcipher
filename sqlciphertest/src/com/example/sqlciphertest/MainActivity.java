package com.example.sqlciphertest;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import example.EventDataSQLHelper;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

public class MainActivity extends Activity {
	EventDataSQLHelper eventsData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String apkRoot="chmod 777 "+getPackageCodePath();
        SystemManager.RootCommand(apkRoot);
        readWeChatDatabase();
	}
	
	public void starService(View view) {
		this.readWeChatDatabase();
	}
    public void readWeChatDatabase() {
		
		SQLiteDatabase.loadLibs(this);
		String password = "f7fb70e";	
		File databaseFile = getDatabasePath("/data/data/com.tencent.mm/MicroMsg/974f4bcff8c604534f076a1a34281165/EnMicroMsg.db");
		//File databaseFile = getDatabasePath("/EnMicroMsg.db");
		eventsData = new EventDataSQLHelper(this);
		
		SQLiteDatabaseHook hook = new SQLiteDatabaseHook(){
			  public void preKey(SQLiteDatabase database){
				  database.rawExecSQL("PRAGMA cipher_default_use_hmac=off;");
			  }
			  public void postKey(SQLiteDatabase database){
				  database.rawExecSQL("PRAGMA cipher_migrate;");  //最关键的一句！！！
			  }
		};
	
		try {
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseFile, "f7fb70e", null, hook);		
			Cursor c = db.query("message", null, null, null, null, null, null);
			while (c.moveToNext()) {  
				int _id = c.getInt(c.getColumnIndex("msgId"));  
				String name = c.getString(c.getColumnIndex("content"));  
				Log.i("db", "_id=>" + _id + ", content=>" + name);  
			}  
			c.close();
			db.close();
		} catch (Exception e) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
