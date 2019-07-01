package com.angelfish.multiplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "MultiPlayer";
    private static final String INNERFILEPATH  = Environment.getExternalStorageDirectory()+"/LocalVideos/";
    private static final String VIDEOSPATH  = "/videos/";
    private File[] mLocalFiles;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e(TAG, "onReceive android.intent.action.BOOT_COMPLETED");
            Intent startIntent = new Intent();  // 要启动的Activity
            //1.如果自启动APP，参数为需要自动启动的应用包名
//            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            //下面这句话必须加上才能开机自动运行app的界面
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.setClass(context, MainActivity.class);
            //2.如果自启动Activity
            context.startActivity(startIntent);
            //3.如果自启动服务
//            context.startService(startIntent);
        }else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
            Log.e(TAG, "Intent.ACTION_MEDIA_MOUNTED");
            List<String> pathForUSBDisks = getExtSDCardPath();
            copyFromUSB(pathForUSBDisks);

        }
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
            File[] fileToCopy = pathInUSB.listFiles();
            for (int j = 0; j < fileToCopy.length; i++) {
                if (fileToCopy[j].isFile()) {
                    // 源文件
                    File sourceFile = fileToCopy[j];
                    String nameToCopy = sourceFile.getName();
                    // 目标文件
                    File targetFile = new
                            File(INNERFILEPATH+nameToCopy);
                    if(!targetFile.exists()){
                        try {
                            copyFile(sourceFile, targetFile);
                        }catch(IOException ex){
                            ex.printStackTrace();
                            targetFile.delete();
                        }

                    }
                }

            }
        }
    }

    public void checkInnerDirectory(){
        File f = new File(INNERFILEPATH);
        AddressUtils.checkFilePath(f);
        mLocalFiles = f.listFiles();

    }

    public static void copyFile(File sourceFile,File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff=new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }


}
