package com.DivineInspiration.experimenter.Model;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class IdGen {

    public interface IDCallBackable{
        public void onIdReady(String id);
    }

    /**
     * Generates a user id, a 53 bit long. Where the first 32 bits is epoch time, in seconds.
     * The latter 20 bits are first 4 digit of firebase installation id(base 64) loosely converted to base 36
     */
    //TODO should userId be stored as long or a string?
    public static void genUserId(IDCallBackable callable){
        /*
        Name: Rajeev Singh
        Link: https://www.callicoder.com/distributed-unique-id-sequence-number-generator/
        Date: Jun 8, 2018
        License: Unknown
        Usage: Introduction to twitter snowflake, the below is a rough implementation similar to twitter snowflake.
         */
        /*
        Name: MH.
        Link: https://stackoverflow.com/a/8675243/12471420
        Date: Dec 30, 2011
        License: CC BY-SA 3.0
        Usage: To use callback interface to handle async functions
         */
        FirebaseInstallations.getInstance().getId().addOnCompleteListener(task -> {
            Log.d("stuff","entering gen user id");
            long output = 0;
            if (task.isSuccessful()){
                String fid = task.getResult();
                assert fid != null;
                fid = fid.substring(0, 4).toUpperCase();
                StringBuilder temp = new StringBuilder();
                for(int i = 0; i < 4; i++){
                    if(!inRange(fid.charAt(i), 65, 90) && !inRange(fid.charAt(i), 30, 39)){
                        temp.append((fid.charAt(i) % 26)+65);
                    }
                    else{
                        temp.append(fid.charAt(i));
                    }
                }
                fid = temp.toString();
                long fidVal = base36To10(fid);
                //in base 36, the first 4 digits are the
                output=((System.currentTimeMillis()/1000)  << 21);
                output=(output|fidVal);
            }
            else{
                output=(-1);
            }
            Log.d("stuff","exiting genUser id");
           callable.onIdReady(base10To36(output));
        });

    }

    public static long genExperimentId(int expCount, User user){
        return 0L;
    }

    public static long genTrialsId(User user, int trialsCount){
        return 0L;
    }

    /**
     * converts a long in base10 to base36 as a string
     * @param source The number to be converted
     * @return converted string
     */
    public static String base10To36(long source) {
        StringBuilder out = new StringBuilder();
        while (source != 0){
            int temp =(int)( source % 36);
            if(inRange(temp, 0, 9)){
                out.insert(0, (char) (temp + 48));
            }
            else{
                out.insert(0, (char)(temp + 55));
            }
            source = source/36;
        }
        return out.toString();
    }

    /**
     * Converts a base36 String to a base10 long
     * @param source String to be converted
     * @return converted string.
     */
    public static long base36To10(String source){
        long output = 0;
        for(int i = 0; i < source.length(); i++){
            int val = source.charAt(i);
            if(inRange(val, 65, 90)){
                val -= 55;
            }else if(inRange(val, 48, 57)){
                val -= 48;
            }
            else{
                //a non b36 digit found
                return -1L;
            }
            output += val * Math.pow(36, source.length() - 1 -i);
        }
        return output;
    }

    private static boolean inRange(int val, int min, int max){
        return val >= min && val <= max;
    }
}
