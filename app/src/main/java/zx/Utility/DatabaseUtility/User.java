package zx.Utility.DatabaseUtility;

/**
 * Created by Lesley on 2016/3/11.
 */
public class User {
    private static String username = null;
    public static void setUsername(String user_name){
        username = user_name;
    }
    public static String getUsername(){
        return username;
    }
}
