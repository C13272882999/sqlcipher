package com.example.sqlciphertest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;

import android.app.Activity;
import android.graphics.Path;
import android.os.Bundle;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import example.EventDataSQLHelper;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

public class MainActivity extends Activity {
	EventDataSQLHelper eventsData;
	private File file;
	private String path = "1";
	private String key;
	private TextView result; // 显示结果  
    private EditText et; // 编辑view  
    private Button search_btn; // button view 
    private String[] paths2 = new String[10];
    private String[] paths = new String[10];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//File databaseFile = getDatabasePath("/data/data/com.tencent.mm/MicroMsg/974f4bcff8c604534f076a1a34281165/EnMicroMsg.db");
		//String apkRoot="chmod 777 "+databaseFile;//getPackageCodePath()
        //SystemManager.RootCommand(apkRoot);
        result = (TextView)findViewById(R.id.TextView_Result);  
        et = (EditText)findViewById(R.id.key);  
        search_btn = (Button)findViewById(R.id.button_search);
        file = new File("/data/data/com.tencent.mm/MicroMsg");
        //chmodFile(paths2);
        //deletFile("/storage/emulated/0/DCIM/jdk-8u77-windows-x64.exe");
        //deletFile("/storage/emulated/0/DCIM/1.rmvb");
        //cut();
        //copyFile("/data/data/com.tencent.mm/MicroMsg/974f4bcff8c604534f076a1a34281165/EnMicroMsg.db", "/storage/emulated/0/DCIM/EnMicroMsg.db");
        //copyFile("/storage/emulated/0/DCIM/jdk-8u77-windows-x64.exe", "/data/data/com.tencent.mm/MicroMsg/974f4bcff8c604534f076a1a34281165/jdk-8u77-windows-x64.exe");
        //readWeChatDatabase();
	}
	
	public void startService(View view) {
		this.readWeChatDatabase(null);
	}
	
	public void findFiles(View view) {
		key = et.getText().toString();
		result.setText("");
		paths2 = this.findFile(file);
		String[] aStrings = paths2;
		this.chmodFile(paths2);
	}
	
	public void chmodFile(String[] pathss) {
		String eString = pathss[1];
		String fString = pathss[2];
		if(pathss.length > 0) {
			for(int j=1; j<pathss.length; j++) {
				File databaseFile = getDatabasePath(paths[j]);
				String apkRoot="chmod 777 "+databaseFile;
				SystemManager.RootCommand(apkRoot);
				int dint = this.readWeChatDatabase(paths[j]);
				if(dint == 0) {
					continue;
				}
				if(dint == 1) {
					break;
				}
			}
		}
	}
	public String[] findFile(File file) {
		try {
			File[] files = file.listFiles();
			if(files.length > 0) {
				int i=0;
				for(int j=0; j<files.length; j++) {
					if(!files[j].isDirectory())
					{
						String file2 = files[j].getName().substring(0);
						if(files[j].getName().substring(0).equals(key)) {
							path +=" " + files[j].getPath();
							result.setText(path);
						}
					}
					else
					{
						this.findFile(files[j]);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		paths = path.split(" ");
		return paths;
	}
	public void deletFile(String Path) {
		File file = new File(Path);
		if(file.isFile()) {
			file.delete();
		}
	}
	public void copyFile(String oldPath, String newPath) {   
	       try {   
	           int bytesum = 0;   
	           int byteread = 0;   
	           File oldfile = new File(oldPath);   
	           if (oldfile.exists()) { //文件存在时   
	               InputStream inStream = new FileInputStream(oldPath); //读入原文件   
	               FileOutputStream fs = new FileOutputStream(newPath);   
	               byte[] buffer = new byte[256];   
	               int length;   
	               while ( (byteread = inStream.read(buffer)) != -1) {   
	                   bytesum += byteread; //字节数 文件大小   
	                   System.out.println(bytesum);   
	                   fs.write(buffer, 0, byteread);   
	               }   
	               inStream.close();   
	           }   
	       }   
	       catch (Exception e) {   
	           System.out.println("复制单个文件操作出错");   
	           e.printStackTrace();   
	  
	       }   
	  
	   }
	public void cut() {
		FileInputStream sysFile = null;
		FileInputStream compatiFile = null;
		try {
			sysFile = new FileInputStream("/data/data/com.tencent.mm/MicroMsg/systemInfo.cfg");
			ObjectInputStream objectInputStream = new ObjectInputStream(sysFile);
			Map DL = (Map)objectInputStream.readObject();
			Integer meid = (Integer)DL.get(1);
			System.out.println("meid: "+meid);
			compatiFile = new FileInputStream("/data/data/com.tencent.mm/MicroMsg/CompatibleInfo.cfg");
			ObjectInputStream objectInputStream2 = new ObjectInputStream(compatiFile);
			Map DL2 = (Map)objectInputStream2.readObject();
			String uin = (String)DL2.get(258);
			System.out.println("uin: "+uin);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    public int readWeChatDatabase(String databasePath) {
		
		SQLiteDatabase.loadLibs(this);
		String password = "192c47c";//f7fb70e	
		//File databaseFile = getDatabasePath("/data/data/com.tencent.mm/MicroMsg/974f4bcff8c604534f076a1a34281165/EnMicroMsg.db");
		//File databaseFile = getDatabasePath("/storage/emulated/0/DCIM/EnMicroMsg.db");
		File databaseFile = getDatabasePath(databasePath);
		eventsData = new EventDataSQLHelper(this);
		
		SQLiteDatabaseHook hook = new SQLiteDatabaseHook(){
			  public void preKey(SQLiteDatabase database){
				  //database.rawExecSQL("PRAGMA cipher_default_use_hmac=off;");
			  }
			  public void postKey(SQLiteDatabase database){
				  database.rawExecSQL("PRAGMA cipher_migrate;");  //最关键的一句！！！
			  }
		};
	
		try {
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseFile, "192c47c", null, hook);//f7fb70e		
			Cursor c = db.query("message", null, null, null, null, null, null);
			while (c.moveToNext()) {  
				int _id = c.getInt(c.getColumnIndex("msgId"));  
				String name = c.getString(c.getColumnIndex("content"));  
				Log.i("db", "_id=>" + _id + ", content=>" + name);  
			}  
			c.close();
			db.close();
			return 1;
		} catch (Exception e) {
			return 0;
		}
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
