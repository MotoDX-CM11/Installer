package com.Aaahh.myapplication2.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends ActionBarActivity {

    private static final String Main = "MainActivity";
    private Handler handler;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        checksd();
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

    public void checksd() {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            long megAvailable = bytesAvailable / 1048576;
            System.out.println("Megs :" + megAvailable);
            if (megAvailable >= 11) {
            } else {
                dialog = new ProgressDialog(this);
                dialog.setTitle("Not Enough Available Space on SDCard ");
                dialog.setMessage("Please free at least 12MB on your sdcard. ");
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                exec.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                        long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
                        long megAvailable = bytesAvailable / 1048576;
                        System.out.println("Megs :" + megAvailable);
                        if (megAvailable >= 11) {
                            dialog.dismiss();
                            exec.shutdown();
                        }
                    }
                }, 0, 5, TimeUnit.SECONDS);
            }
        } else {
            dialog = new ProgressDialog(this);
            dialog.setTitle("SDCard Unavailable");
            dialog.setMessage("Please insert an sdcard and/or unplug your usb cable.");
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                    if (isSDPresent) {
                        dialog.dismiss();
                        exec.shutdown();
                    }
                    }
            }, 0, 5, TimeUnit.SECONDS);
        }
    }


    public void buttonOnClick1(View v1) throws IOException, InterruptedException {
        try {
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.alephzain.framaroot");
            startActivity(LaunchIntent);
        } catch (Exception e) {
            AssetManager assetManager = getAssets();
            InputStream in;
            OutputStream out;
            in = assetManager.open("framaroot.apk");
            out = new FileOutputStream(getExternalFilesDir(null) + "/framaroot.apk");
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(getExternalFilesDir(null) + "/framaroot.apk")), "application/vnd.android.package-archive");
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int reqCode, int resultCode, Intent intent) {
        if (reqCode == 1) {
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.alephzain.framaroot");
            startActivity(LaunchIntent);
        }
        if (reqCode != 1) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Framaroot not installed")
                    .setMessage("Do you want to try installing again?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                buttonOnClick1(null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public void buttonOnClick2(View v2) {
        Intent browserIntent;
        browserIntent = new Intent(ACTION_VIEW, Uri.parse("https://drive.google.com/folderview?id=0B0r5jFEucqVyZDN5TnJvX1d4eVU&usp=sharing"));
        startActivity(browserIntent);
    }

    /**
     * Copy the asset at the specified path to this app's data directory. If the
     * asset is a directory, its contents are also copied.
     *
     * @param path Path to asset, relative to app's assets directory.
     */
    private boolean copyAsset(String path) {
        AssetManager manager = getAssets();

        // If we have a directory, we make it and recurse. If a file, we copy its
        // contents.
        try {
            String[] contents = manager.list(path);

            // The documentation suggests that list throws an IOException, but doesn't
            // say under what conditions. It'd be nice if it did so when the path was
            // to a file. That doesn't appear to be the case. If the returned array is
            // null or has 0 length, we assume the path is to a file. This means empty
            // directories will get turned into files.
            if (contents == null || contents.length == 0)
                throw new IOException();

            // Make the directory.
            File dir = new File(getExternalFilesDir(null), path);
            dir.mkdirs();

            // Recurse on the contents.
            for (String entry : contents) {
                copyAsset(path + "/" + entry);
            }
        } catch (IOException e) {
            copyFileAsset(path);
        }
        return true;
    }

    /**
     * Copy the asset file specified by path to app's data directory. Assumes
     * parent directories have already been created.
     *
     * @param path Path to asset, relative to app's assets directory.
     */
    private void copyFileAsset(String path) {
        File file = new File(getExternalFilesDir(null), path);
        try {
            InputStream in = getAssets().open(path);
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            Log.e(Main, "exception", e);
        }
    }

    public void buttonOnClick3() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        copyAsset("system");
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (copyAsset("system") == true) {
                    exec.shutdown();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
        new Thread() {
            public void run() {
                try {
                    sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Process p = null;
                Process a = null;
                //Process b = null;
                Process c = null;
                Process d = null;
                //Process f = null;
                Process g = null;
                Process h = null;
                Process i = null;
                Process j = null;
                Process k = null;
                Process l = null;
                Process m = null;
                try {
                    p = Runtime.getRuntime().exec(new String[]{"su", "-c", "mount -o rw,remount /dev/block/mmcblk1p21 /system"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    a = Runtime.getRuntime().exec(new String[]{"su", "-c", "cp", "-r " + getExternalFilesDir(null) + "/system/* /system"});
                    c = Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod", "777", "/system/bootmenu"});
                    d = Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod", "777", "/system/bootmenu/*"});
                    g = Runtime.getRuntime().exec(new String[]{"su", "-c", "ln", "-s", "/system/bin/logwrapper", "system/bin/bootmenu"});
                    h = Runtime.getRuntime().exec(new String[]{"su", "-c", "ln", "-s", "/system/bootmenu/images/indeterminate.png", "/system/bootmenu/images/indeterminate1.png"});
                    i = Runtime.getRuntime().exec(new String[]{"su", "-c", "ln", "-s", "/system/bootmenu/images/indeterminate.png", "/system/bootmenu/images/indeterminate2.png"});
                    j = Runtime.getRuntime().exec(new String[]{"su", "-c", "ln", "-s", "/system/bootmenu/images/indeterminate.png", "/system/bootmenu/images/indeterminate3.png"});
                    k = Runtime.getRuntime().exec(new String[]{"su", "-c", "ln", "-s", "/system/bootmenu/images/indeterminate.png", "/system/bootmenu/images/indeterminate4.png"});
                    l = Runtime.getRuntime().exec(new String[]{"su", "-c", "ln", "-s", "/system/bootmenu/images/indeterminate.png", "/system/bootmenu/images/indeterminate5.png"});
                    m = Runtime.getRuntime().exec(new String[]{"su", "-c", "ln", "-s", "/system/bootmenu/images/indeterminate.png", "/system/bootmenu/images/indeterminate6.png"});

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    p.waitFor();
                    a.waitFor();
                    c.waitFor();
                    d.waitFor();
                    g.waitFor();
                    h.waitFor();
                    i.waitFor();
                    j.waitFor();
                    k.waitFor();
                    l.waitFor();
                    m.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                dialog.dismiss();
            }
        };
    }

    public void buttonOnClick4(View v4) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot recovery"});
        } catch (IOException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Rebooting Failed")
                    .setMessage("Did you grant root?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot recovery"});
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public void buttonOnClick5(View v5) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
        } catch (IOException e) {
            e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Rebooting Failed")
                        .setMessage("Did you grant root?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
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