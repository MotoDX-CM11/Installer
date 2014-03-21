package com.Aaahh.myapplication2.app;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.IOException;

public class Helper
{

    static final String LOGTAG = "Droid2Bootstrap";
    public static final String SCRIPT_NAME = "surunner.sh";

    public Helper()
    {
    }

    public static int runSuCommand(Context context, String s)
        throws IOException, InterruptedException
    {
        return runSuCommandAsync(context, s).waitFor();
    }

    public static Process runSuCommandAsync(Context context, String s)
        throws IOException
    {
        DataOutputStream dataoutputstream = new DataOutputStream(context.openFileOutput("surunner.sh", 0));
        dataoutputstream.writeBytes(s);
        dataoutputstream.close();
        String as[] = new String[3];
        as[0] = "su";
        as[1] = "-c";
        as[2] = (new StringBuilder(". ")).append(context.getFilesDir().getAbsolutePath()).append("/").append("surunner.sh").toString();
        return Runtime.getRuntime().exec(as);
    }

    public static int runSuCommandNoScriptWrapper(Context context, String s)
        throws IOException, InterruptedException
    {
        String as[] = {
            "su", "-c", s
        };
        return Runtime.getRuntime().exec(as).waitFor();
    }
}
