package ru.abch.fmradio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import ru.abch.fmradio.android_serialport_api.SerialPort;

public class RadioService extends Service {
    final String TAG = "RadioService";
    RadioBinder binder = new RadioBinder();
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    ReadRadioThread mReadThread;
    private static final int ID_SERVICE = 101;
    Context ctx;
    public RadioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
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