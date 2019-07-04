package com.angelfish.multiplayer.services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.angelfish.multiplayer.activity.MainActivity;
import com.angelfish.multiplayer.util.AddressUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CopyLocalFilesService extends Service {

    private static final String TAG = "MultiPlayer";
    private static final String INNERFILEPATH  = Environment.getExternalStorageDirectory()+"/LocalVideos/";
    private static final String VIDEOSPATH  = "/videos/";
    private int mCopyingFilesCount = 0;
    private File[] mFileToCopy;
    /** For showing and hiding our notification. */
//    NotificationManager mNM;
    /** Keeps track of all current registered clients. */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    /** Holds last value set by a client. */
    int mValue = 0;

    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    static final int MSG_SET_VALUE = 3;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    for (int i=mClients.size()-1; i>=0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null,
                                    MSG_SET_VALUE, mValue, 0));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            mClients.remove(i);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate() {
//        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
        // Display a notification about us starting.
//        showNotification();
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
//        mNM.cancel(R.string.remote_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, "Service of copying files ends", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("LongRunningService", "executed at " + new Date().
                        toString());
            }
        }).start();
        if(mCopyingFilesCount == 0){
            List<String> pathForUSBDisks = getExtSDCardPath();
            copyFromUSB(pathForUSBDisks);
        }else{
            Log.e(TAG, "Copying files from sd card to inner storage");
//            TODO: check why this function is called after copy has started
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取外置SD卡路径
     * @return	应该就一条记录或空
     */
    public List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("/storage/"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[2];
                    Log.e(TAG, "adding usb path to check "+path);
                    if(!path.equals("/storage/emulated") && !path.equals("/storage/self") ){
                        File file = new File(path);
                        if (file.isDirectory())
                        {
                            lResult.add(path);

                        }
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    public void copyFromUSB(List<String> pathForUSBDisks){
        checkInnerDirectory();
        for(int i =0; i < pathForUSBDisks.size(); i++){
            String pathToCopy = pathForUSBDisks.get(i)+VIDEOSPATH;
            Log.e(TAG, "check path "+pathToCopy);
            File pathInUSB = new File(pathToCopy);
            if(!pathInUSB.exists() || !pathInUSB.isDirectory()){
                return;
            }
//            File[] fileToCopy = pathInUSB.listFiles();
            mFileToCopy = pathInUSB.listFiles();

                        try {

                            new CopyFilesTask().execute();
                        }catch(Exception ex){
                            Log.e(TAG,"URL parsing error");
                            ex.printStackTrace();
                        }

        }

    }

    public void checkInnerDirectory(){
        File f = new File(INNERFILEPATH);
        AddressUtils.checkFilePath(f);
//        mLocalFiles = f.listFiles();

    }

    private class CopyFilesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void...arg0) {
            int count = mFileToCopy.length;
            long totalSize = 0;
            String fileName = "";
            try{
                for (int i = 0; i < count; i++) {
                    FileInputStream input = new FileInputStream(mFileToCopy[i]);
                    BufferedInputStream inBuff=new BufferedInputStream(input);
                    fileName = mFileToCopy[i].getName();
                    File targetFile = new
                            File(INNERFILEPATH+fileName);
                    if(targetFile.exists()){
                        continue;
                    }
                    // 新建文件输出流并对它进行缓冲
                    FileOutputStream output = new FileOutputStream(targetFile);
                    BufferedOutputStream outBuff=new BufferedOutputStream(output);

                    // 缓冲数组
                    byte[] b = new byte[1024 * 5];
                    int len;
                    mCopyingFilesCount ++;
                    while ((len =inBuff.read(b)) != -1) {
                        outBuff.write(b, 0, len);
                    }
                    // 刷新此缓冲的输出流
                    outBuff.flush();
                    mCopyingFilesCount --;

                    //关闭流
                    inBuff.close();
                    outBuff.close();
                    output.close();
                    input.close();
                }
                checkCopyFinished();
            }catch(Exception e){
                if(!fileName.equals("")){
                    File f = new File(INNERFILEPATH+fileName);
                    f.delete();
                }
                stopService();

                return null;
            }
            return null;
        }

        protected void onProgressUpdate() {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute() {
            checkCopyFinished();
        }
    }
    public void checkCopyFinished() {
        Log.e(TAG, "checkCopyFinished mCopyingFilesCount ="+mCopyingFilesCount);
        if (mCopyingFilesCount <= 0) {
            this.stopSelf();
        }
    }

    public void stopService(){
        this.stopSelf();
    }

}
