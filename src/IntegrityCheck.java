import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

import Log.Log;

/**
 *
 */

/**
 * @author 森
 *
 * @date 2012-4-29
 */
public class IntegrityCheck {
    static String tableName = "dataset";
    String dataDir;
    File file;
    private MessageDigest md = null;
    String md5 = null;
    long startTime;
    long endTime;
    Log timeFile = new Log("time.txt", false);

    /**
     * @throws IOException
     *
     */
    // ArrayList<>
    public IntegrityCheck(String arg) {
        // TODO Auto-generated constructor stub

        try {
            if (!Setting.isload) {
                Setting.Loadsetting();
            }
            dataDir = Setting.dataDir;
            md = MessageDigest.getInstance(arg);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    // 1: duplicate
    // 2: new file
    // 3. old file, update
    public int duplicate(File file) throws Exception {

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));
        md.reset();
        int len = 0;
        byte[] buffer = new byte[8192];
        while ((len = bis.read(buffer)) > -1) {
            md.update(buffer, 0, len);
        }
        md5 = format(md.digest());

        if (Excel2Database.database.exist(tableName, "excelfile_md5", md5)) {
            return 1;
        } else {
            int result = processNewFile(file);
            return result;
        }
    }

    public void compareFile(File file, File oldFile) {

    }

    public int processNewFile(File file) throws Exception {

        int result = 2;
        Excel2Database excel = new Excel2Database(file.getAbsolutePath());
        String identifier = excel.getIdentifier();
        if (Excel2Database.database.exist(tableName, "identifier", identifier)) {
            String query = " select excelfile from dataset where identifier= '"
                    + "' ;";
            ResultSet resultSet = Excel2Database.database.stmt
                    .executeQuery(query);
            String oldFileName = null;
            if (resultSet.next()) {
                oldFileName = resultSet.getString(1);
            }

            File oldFile = new File(dataDir + "/" + oldFileName);
            // compare the two files
            compareFile(file, oldFile);
            oldFile.delete();
            result = 3;
        }

        return result;
    }

    public static String format(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int decValue;
        for (int i = 0; i < bytes.length; i++) {
            String hexVal = Integer.toHexString(bytes[i] & 0xFF);
            if (hexVal.length() == 1)
                hexVal = "0" + hexVal; // put a leading zero
            sb.append(hexVal);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        Setting.Loadsetting();
        Excel2Database excel2Database = new Excel2Database("src/test.xls");
        IntegrityCheck integrityCheck = new IntegrityCheck("MD5");
        File dataDir = new File(Setting.dataDir);

        for (File file : dataDir.listFiles()) {
            if (integrityCheck.duplicate(file) == 1)
                System.out.println(file.getName() + " exist!");
            else
                System.out.println(file.getName() + " doesn't exist!");
        }
    }
}
