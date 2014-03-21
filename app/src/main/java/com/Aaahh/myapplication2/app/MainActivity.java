package com.Aaahh.myapplication2.app;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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

    public void buttonOnClick1(View v1) throws IOException, InterruptedException {
        AssetManager assetManager = getAssets();
        InputStream in;
        OutputStream out;
        in = assetManager.open("framaroot.apk");
        out = new FileOutputStream("/sdcard/framaroot.apk");
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File("/sdcard/framaroot.apk")), "application/vnd.android.package-archive");
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int reqCode, int resultCode, Intent intent) {
        if (reqCode == 1) {
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.alephzain.framaroot");
            startActivity(LaunchIntent);
        }
    }

    public void buttonOnClick2(View v2) {
        Intent browserIntent;
        browserIntent = new Intent(ACTION_VIEW, Uri.parse("https://drive.google.com/folderview?id=0B0r5jFEucqVyZDN5TnJvX1d4eVU&usp=sharing"));
        startActivity(browserIntent);
    }

    public void buttonOnClick3(View v3) {
        /*
       String s = getFilesDir().getAbsolutePath();
        String s1 = (new StringBuilder(String.valueOf(s))).append("/busybox").toString();
        String s3 = (new StringBuilder(String.valueOf(s))).append("/system").toString();
        String s4 = (new StringBuilder(String.valueOf(s))).append("/bootmenu").toString();
        StringBuilder stringbuilder = new StringBuilder();

        Log.d("MainActivity", "one");
        ///Installing Recovery to System
        stringbuilder.append((new StringBuilder(String.valueOf(s1))).append(" mount -oremount,rw /system ; ").toString());
        stringbuilder.append((new StringBuilder(String.valueOf(s1))).append(" cp -r ").append(s3).append(" / ; ").toString());
        stringbuilder.append("cd /system/bin ; ln -s bootmenu logwrapper ; ");

        Log.d("MainActivity", "two");
        ///Installing recovery to System/bootmenu/binary/bootmenu
        stringbuilder.append((new StringBuilder(String.valueOf(s1))).append(" mkdir -p /system/bootmenu/binary/bootmenu ; ").toString());
        stringbuilder.append((new StringBuilder(String.valueOf(s1))).append(" cp ").append(s4).append(" /system/bootmenu/binary/bootmenu ; ").toString());

        Log.d("MainActivity", "three");
        //System to ro
        stringbuilder.append((new StringBuilder(String.valueOf(s1))).append(" mount -oremount,ro /system ; ").toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setPositiveButton(android.R.string.ok, null);
        try
        {
            Helper.runSuCommand(MainActivity.this, stringbuilder.toString());
            return;
        }
        catch (Exception exception)
        {
            android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(MainActivity.this);
            builder2.setPositiveButton(0x104000a, null);
            builder2.setTitle("Failure");
            builder2.setMessage(exception.getMessage());
            exception.printStackTrace();
            return;
        }
        */
    }

    public void buttonOnClick4(View v4) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("su");
            p = Runtime.getRuntime().exec("sleep 10");
            OutputStream os = p.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeBytes("reboot recovery" + "\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
                Log.d("MainActivity", "su was not granted");
            }
        }
    }

    public void buttonOnClick5(View v5) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("su");
            p = Runtime.getRuntime().exec("sleep 10");
            OutputStream os = p.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeBytes("reboot" + "\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
                Log.d("MainActivity", "su was not granted");
            }
        }
    }

    public void buttonOnClick6(View v6) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("sudo su");
            p = Runtime.getRuntime().exec("sleep 10");
            OutputStream os = p.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeBytes("reboot" + "\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}