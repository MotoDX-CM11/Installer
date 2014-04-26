package com.Aaahh.myapplication2.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    protected static String[] availableTestCommands = new String[]{
            "echo -BOC-",
            "id"
    };
    public boolean Sdcard;
    public boolean fake;
    private Handler handler;
    private ProgressDialog dialog;

    protected static boolean parseAvailableResult(String[] ret, boolean checkForRoot) {
        if (ret == null) return false;

        // this is only one of many ways this can be done
        boolean echo_seen = false;
        for (String line : ret) {
            if (line.contains("uid=")) {
                // id command is working, let's see if we are actually root
                return !checkForRoot || line.contains("uid=0");
            } else if (line.contains("-BOC-")) {
                // if we end up here, at least the su command starts some kind of shell,
                // let's hope it has root privileges - no way to know without additional
                // native binaries
                echo_seen = true;
            }
        }

        return echo_seen;
    }

    public static boolean available() {
        // this is only one of many ways this can be done

        String[] ret = (MainActivity.availableTestCommands);
        return MainActivity.parseAvailableResult(ret, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(Main, "First");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
            checksd();
        }
        Log.e(Main, "1");
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
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void checksd() {
        Log.e(Main, "2");
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            long megAvailable = bytesAvailable / 1048576;
            System.out.println("Megs :" + megAvailable);
            if (megAvailable >= 11) {
                Sdcard = true;
                Log.e(Main, "3");
            } else {
                Log.e(Main, "3b");
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("Not Enough Available Space on SDCard ");
                dialog.setMessage("Please free at least 12MB on your sdcard. ");
                dialog.setCancelable(true);
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
                            Sdcard = true;
                            exec.shutdown();
                        }
                    }
                }, 0, 5, TimeUnit.SECONDS);
            }
        } else {
            Log.e(Main, "f2");
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("SDCard Unavailable");
            dialog.setMessage("Please insert an SDCard and/or unplug your usb cable.");
            dialog.setCancelable(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            Log.e(Main, "f3");
            final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                    if (isSDPresent) {
                        Log.e(Main, "f4");
                        dialog.dismiss();
                        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                        long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
                        long megAvailable = bytesAvailable / 1048576;
                        System.out.println("Megs :" + megAvailable);
                        if (megAvailable >= 11) {
                            Sdcard = true;
                            Log.e(Main, "3");
                            exec.shutdown();
                        } else {
                            Log.e(Main, "3b");
                            dialog = new ProgressDialog(MainActivity.this);
                            dialog.setTitle("Not Enough Available Space on SDCard ");
                            dialog.setMessage("Please free at least 12MB on your sdcard. ");
                            dialog.setCancelable(true);
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
                                        Sdcard = true;
                                        exec.shutdown();
                                    }
                                }
                            }, 0, 5, TimeUnit.SECONDS);
                        }
                        exec.shutdown();
                        Log.e(Main, "f5");
                    }
                }
            }, 0, 5, TimeUnit.SECONDS);
            dialog.show();
        }
    }

    public boolean isDeviceRooted() {
        return available();
    }

    public void buttonOnClick1(View v1) throws IOException {
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
            File file = new File(getExternalFilesDir(null) + "/framaroot.apk");
            if (file.exists()) {
            intent.setDataAndType(Uri.fromFile(new File(getExternalFilesDir(null) + "/framaroot.apk")), "application/vnd.android.package-archive");
            startActivityForResult(intent, 1);
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sdcard Unavailable")
                        .setMessage("An sdcard is needed to install the rooting app at this time. Please try with an sdcard or use 'adb install framaroot.apk' from your pc.")
                        .setPositiveButton("Okay", null)
                        .show();
            }
        }
    }

    protected void onActivityResult(int reqCode, int resultCode, Intent intent) {
        if (reqCode == 1) {
            Intent i;
            PackageManager manager = getPackageManager();
            try {
                i = manager.getLaunchIntentForPackage("com.alephzain.framaroot");
                if (i == null)
                    throw new PackageManager.NameNotFoundException();
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Framaroot can't be found.")
                        .setMessage("Please restart and try again.")
                        .setPositiveButton("Okay", null)
                        .show();
            }
        }
        if (reqCode != 1) {
            new AlertDialog.Builder(MainActivity.this)
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
            if (contents.length == 0)
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

    public void buttonOnClick3(View v3) {
        if (!Sdcard) {
            Log.e(Main, "n");
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("SDCard Unavailable");
            dialog.setMessage("Would you like to try to continue without an SDCard? (Requires root)");
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Process p1 = null;
                    Process p2 = null;
                    Process p3 = null;
                    Process p4 = null;
                    try {
                        Log.e(Main, "f1");
                        p1 = Runtime.getRuntime().exec(new String[]{"su", "-c", "mv /sdcard /sdcardreal "});
                        p3 = Runtime.getRuntime().exec(new String[]{"su", "-c", "mv /storage/sdcard0 /storage/sdcard0real"});
                        p2 = Runtime.getRuntime().exec(new String[]{"su", "-c", "mkdir  /sdcard "});
                        p4 = Runtime.getRuntime().exec(new String[]{"su", "-c", "mkdir /storage/sdcard0"});
                        fake = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (p1 != null) {
                                p1.waitFor();
                            }
                            if (p2 != null) {
                                p2.waitFor();
                            }
                            if (p3 != null) {
                                p3.waitFor();
                            }
                            if (p4 != null) {
                                p4.waitFor();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e(Main, "Failed to use fake sdcard");
                        }
                        if (fake) {
                            Sdcard = true;
                            Log.e(Main, "1click");
                        }
                    }
                }
            });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Thread.yield();
                }
            });
            dialog.show();
        } else {
            dialog.show();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Loading");
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            File file = new File("/system/bootmenu/recovery.sh");
            File logwrapper = new File("/system/bin/logwrapper");
            if (!file.exists()) {
                if (!logwrapper.exists()) {
                    copyAsset("system");
                    final ScheduledExecutorService execq = Executors.newSingleThreadScheduledExecutor();
                    execq.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            if (copyAsset("system")) {
                                execq.shutdown();
                            }
                        }
                    }, 0, 5, TimeUnit.SECONDS);
                }
            }
            new Thread() {
                public void run() {
                    try {
                        sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Process p = null;
                    Process a = null;
                    Process b = null;
                    Process c = null;
                    Process d = null;
                    boolean system;

                    try {
                        p = Runtime.getRuntime().exec(new String[]{"su", "-c", "mount -o rw,remount /dev/block/mmcblk1p21 /system"});
                        system = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        system = false;
                    }
                    try {
                        if (system) {
                            a = Runtime.getRuntime().exec(new String[]{"su", "-c", "cp", "-r " + getExternalFilesDir(null) + "/system/* /system"});
                            c = Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod", "-R", "755", "/system/bin/logwrapper"});
                            b = Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod", "-R", "755", "/system/bootstrap"});
                            d = Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod", "-R", "755", "/system/bootstrap/*"});
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (p != null) {
                            p.waitFor();
                        }
                        if (a != null) {
                            a.waitFor();
                        }
                        if (b != null) {
                            b.waitFor();
                        }
                        if (c != null) {
                            c.waitFor();
                        }
                        if (d != null) {
                            d.waitFor();
                        }
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
    }

    public void buttonOnClick4(View v4) {
        Process p;
        try {
            p = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot recovery"});
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Rebooting Failed")
                    .setMessage("Do you want to try again?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Process p;
                            try {
                                p = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot recovery"});
                                p.waitFor();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    public void buttonOnClick5(View v5) {
        Process p;
        try {
            p = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Rebooting Failed")
                    .setMessage("Do you want to try again?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Process p;
                            try {
                                p = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
                                p.waitFor();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean OneClick() {
        Log.e(Main, "oc");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Want to try the OneClick method? (Experimental)")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!isDeviceRooted()) {
                            try {
                                buttonOnClick1(null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Restart Your Phone and Try again.")
                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                System.exit(0);
                                            }
                                        })
                                        .show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Notice")
                                    .setMessage("Please make sure you have the rom on your sdcard.")
                                    .setPositiveButton("Okay", null)
                                    .setNegativeButton("Cancel Install", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            Thread.currentThread().yield();
                                        }
                                    })
                                    .show();
                        }
                                try {
                                    buttonOnClick3(null);
                                    File file = new File("/system/bootmenu/recovery.sh");
                                    File logwrapper = new File("/system/bin/logwrapper");
                                    Thread.currentThread();
                                    Thread.sleep(60000);
                                    if (file.exists()) {
                                        if (logwrapper.exists()) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Thread.currentThread().interrupt();
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Bootstrap not installed")
                                            .setMessage("Please restart and try again.")
                                            .setPositiveButton("Okay", null)
                                            .show();
                                }
                            }
                        })
                .setNegativeButton("No Thanks", null)
                        .show();
        return true;
    }

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