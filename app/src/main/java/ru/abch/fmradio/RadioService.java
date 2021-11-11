package ru.abch.fmradio;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;

import ru.abch.fmradio.android_serialport_api.SerialPort;

public class RadioService extends Service {
    final String TAG = "RadioService";
    RadioBinder binder = new RadioBinder();
    protected SerialPort serialPort;
    protected OutputStream outputStream;
    private InputStream inputStream;
    ReadRadioThread readThread;
    private static final int ID_SERVICE = 101;
    Context ctx;
    public RadioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        ctx = this;
        try {
            serialPort = App.getSerialPort();
            outputStream = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
            /* Create a receiving thread */
            readThread = new ReadRadioThread();
            readThread.start();
        } catch (SecurityException e) {
            Log.d(TAG, "Security error");
        } catch (IOException e) {
            Log.d(TAG, "Unknown error");
        } catch (InvalidParameterException e) {
            Log.d(TAG, "Configuration error");
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(ID_SERVICE, notification);
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "RadioServiceChannelId";
        String channelName = "RadioService";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }
    private class ReadRadioThread extends Thread {
        byte[] readBuffer = new byte[512];
        int readBufferPos = 0;
        @Override
        public void run() {
            super.run();
            Arrays.fill(readBuffer, (byte) 0);
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[128];
                    if (inputStream == null) return;
                    size = inputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        protected  void onDataReceived(final byte[] buffer, final int size) throws IOException {
            int i = 0;
            while (i < size) {
                if (buffer[i] == 10 || buffer[i] == 13) {
                    if (readBufferPos > 0) {
                        String line = new String(Arrays.copyOfRange(readBuffer, 0, readBufferPos));
                        Log.d(TAG, "Radio Received " + readBufferPos + " bytes " + line );
                        readBufferPos = 0;
                        Arrays.fill(readBuffer, (byte) 0);
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        RadioState radioState = gson.fromJson(line, RadioState.class);
                        if(radioState != null) {
                            Parcel radioParcel = Parcel.obtain();
                            radioParcel.writeInt(radioState.f);
                            radioParcel.writeInt(radioState.v);
                            radioParcel.writeInt(radioState.r);
                            radioParcel.writeByte((byte) (radioState.m ? 1 : 0));
                            radioParcel.writeString(radioState.n);
                            radioParcel.writeString(radioState.t);
                            RadioStateParcel radioStateParcel = new RadioStateParcel(radioParcel);
                            Intent intent = new Intent("android.intent.radio.state",null);
                            intent.putExtra("state",radioStateParcel);
                            sendBroadcast(intent);
                        }
                        FreqsArray freqs = gson.fromJson(line, FreqsArray.class);
                        if (freqs != null) {
                            Intent intent = new Intent("android.intent.radio.freqs",null);
                            intent.putExtra("freqs", freqs.F);
                            sendBroadcast(intent);
                        }
                    }
                    break;
                } else {
                    readBuffer[readBufferPos] = buffer[i];
                    i++;
                    readBufferPos++;
                }
            }
        }
    }
    class RadioBinder extends Binder {
        RadioService getService() {
            return RadioService.this;
        }
    }
}