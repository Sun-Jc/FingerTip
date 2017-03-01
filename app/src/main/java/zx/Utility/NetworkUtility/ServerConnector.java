package zx.Utility.NetworkUtility;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by ZX on 2016/3/2.
 */
public class ServerConnector {
    private static String ServerIP = "192.168.0.102";
    private static int ServerPort = 7777;
    private static Socket connection = null;
    private static DataInputStream input = null;
    private static DataOutputStream output = null;
    private static ServerConnector instance;

    private ServerConnector() {
        try {
            Log.i("zxdebug", "new ServerConnector");
            connection = new Socket();
            Log.i("zxdebug", "new connection");
            connection.connect(new InetSocketAddress(ServerIP, ServerPort), 5000);
            Log.i("zxdebug", "connection ready");
            input = new DataInputStream(connection.getInputStream());
            output = new DataOutputStream(connection.getOutputStream());
        } catch (IOException e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        }catch (Exception e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        }
    }

    public static DataInputStream getInputStream() {
        if (instance == null || connection.isClosed()) {
            Log.i("zxdebug", "getInstance, new instance");
            instance = new ServerConnector();
        }
        return input;
    }

    public static DataOutputStream getOutputStream() {
        if (instance == null || connection.isClosed()) {
            Log.i("zxdebug", "getInstance, new instance");
            instance = new ServerConnector();
        }
        return output;
    }
    public static void close(){
        try {
            connection.close();
        } catch (IOException e) {
            Log.e("zxdebug", Log.getStackTraceString(e));
        }
    }
}
