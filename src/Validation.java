import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.net.ftp.*;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.net.ftp.FTPClient;
import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.domimpl.HTMLDocumentImpl;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.mozilla.javascript.ast.NewExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import com.meterware.httpunit.WebClient;
import com.meterware.httpunit.WebConversation;

/**
 * @author ����������
 * @date 2012-4-17
 */
public class Validation {

    Document prefixDocument;
    // contains the CVs and regex of some fiel
    String validationPath = "configuration/validation.xml";
    MyHttpClient myHttpClient;
    String identifier;
    // boolean isValid;
    FTPClient ftpClient;
    HashMap<String, ArrayList<String>> cvMap;

    Database database;

    static String urlRegex;
    Cookie cookie;
    WebConversation webConversation;
    WebClient webClient;
    Boolean checksample = false;//new add for test sample_id

    Validation() throws Exception {
        cvMap = new HashMap<String, ArrayList<String>>();
        myHttpClient = new MyHttpClient();
        // ignore the cookie
        DefaultHttpParams.getDefaultParams().setParameter(
                "http.protocol.cookie-policy", CookiePolicy.IGNORE_COOKIES);
        ftpClient = new FTPClient();
        webConversation = new WebConversation();
        readXmlFiles();
        urlRegex = cvMap.get("url").get(0);
        database = new Database();
    }

    // read xml File
    void readXmlFiles() throws ParserConfigurationException, SAXException,
            IOException {
        // validation.xml
        Document document = HelpFunctions.getDocument(validationPath);
        Element element = document.getDocumentElement();
        NodeList childList = element.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node node = childList.item(i);
            NodeList childList2 = node.getChildNodes();
            ArrayList<String> values = new ArrayList<String>();
            for (int j = 0; j < childList2.getLength(); j++) {
                Node node2 = childList2.item(j);
                if (node2 instanceof Element) {
                    values.add(node2.getTextContent());
                }
            }// for
            cvMap.put(node.getNodeName(), values);
        }
    }

    public boolean validTest(Schema schema) throws IOException, SQLException {
        boolean result = true;
        Table table = null;
        Table table1 = null;
        String target = null;
        String log = "";
        String prefix = "";
        // dataset identifer
        table = schema.getTable("dataset");
        // dataset publisher
        target = "publisher";
        table1 = schema.getTable("publisher");
        String publisher = table1.getValue("name", 0);
        result &= validTest(target, publisher, log);
        // dataset dataset_type
        target = "name";
        table = schema.getTable("type");
        result &= validTest(table, target, log);
        // dataset dataset_doi_relationship
        target = "relationship";
        table = schema.getTable("relation");
        result &= validTestfordoirelationship(table, target, log);
        // dataset ext_acc-mirror
        log = "prefix error";
        target = "link";
        table = schema.getTable("link");
        log = "";
        // submitter_email
        target = "email";
        table = schema.getTable("gigadb_user");
        String submitter_email = table.getValue(target, 0);
        String regex = cvMap.get(target).get(0);
        result &= regexTest(regex, submitter_email, target, log);

        target = "attribute_id";
        table = schema.getTable("sample_attribute");
        result &= attribute_id_Test(table, target);
        // author_name
        target = "name";
        table = schema.getTable("author");
        regex = cvMap.get("author_name").get(0);
        result &= regexTest(table, target, regex, log);
        System.out.println("author_name test OK");
        // project url
        target = "project_url";
        table = schema.getTable("project");
        regex = urlRegex;
        prefix = "";
        result &= accessTest(table, target, prefix, log);
        // determine if the url is in the database or not
        result &= uniqueProjectNameTest(table);
        System.out.println("project test OK");
        //project image_location
        target = "image_location";
        table = schema.getTable("project");
        String image_location = table.getValue("image_location", 0);
        if (image_location != null) {
            System.out.println("this is image_location    " + image_location);
            result &= validTestforprojectimage(target, image_location, log);
        }
        System.out.println("image test OK");

        // sample id
        table = schema.getTable("sample");
        target = "sample_id";
        log = "";
        //result &= sample_idTest(table, target, log);
        // sample attribute
        table = schema.getTable("sample");
        target = "sample_attributes";
        log = "";
        // file_description
        target = "file_type";
        table = schema.getTable("file");
        result &= validTest(table, target, log, "file_name");
        System.out.println("file_type test OK");

        // file

        table = schema.getTable("file_type");
        table1 = schema.getTable("file");
        target = "name";
        //result &= containFiles(table,table1, target);
        log = "";
        // file file_size
        table = schema.getTable("file");
        target = "size";
        //	result &= file_sizeTest(table, target,log);
        //file latest date
        table = schema.getTable("file");
        target = "date_stamp";
        String latest_time = get_stamp(table, target);
        table = schema.getTable("dataset");
        target = "modification_date";
        //	set_modifi(table,target,latest_time);


        System.out.println("Finished validation OK");

        return result;

    }
    //set modification_date

    public void set_modifi(Table table, String target, String datetime) {
        String publish = table.getValue("publication_date", 0);

        String[] ss1 = datetime.split("\\-");
        String[] ss2 = publish.split("\\/");
        boolean big = true;
        for (int t = 0; t < ss1.length; t++) {
            System.out.println("m1: " + ss1[t]);
            System.out.println("m2: " + ss2[t]);

            if (Integer.parseInt(ss1[t]) > Integer.parseInt(ss2[t])) {
                big = false;
            }
        }
        if (big == false) {
            publish = ss1[0] + "/" + ss1[1] + "/" + ss1[2];
            table.setValue(target, publish, 0);

        }


    }


    // we'll pull doi/pmid here
    public boolean manuscriptTest(Table table, String log)
            throws HttpException, IOException {
        // manuscript table test
        boolean result = true;
        int size = table.recordList.size();
        for (int i = 0; i < size; i++) {
            // ArrayList<E>
            String doi = table.getValue("identifier", i);
            String pmid = table.getValue("pmid", i);
            if (pmid == null) {
                String target = "manuscript_doi";
                String prefix = myHttpClient.prefixMap.get(target);
                String regex = cvMap.get(target).get(0);
                // test if it can be accessed
                if (regexTest(regex, doi, target, log)
                        && myHttpClient.accessTest(doi, prefix, target, log)) {
                    pmid = myHttpClient.get_pmid(doi);
                    if (pmid == null) {
                        Excel2Database.excel2DBLog
                                .writeLine("ValidTest for doi: '"
                                        + doi
                                        + "'. Warning: can't not pull the information of pmid from it");
                    } else
                        // add it to table
                        table.setValue("pmid", pmid, i);
                } else {
                    result = false;
                    pmid = null;
                }
            } else {
                // PMID test
                String target = "pmid";
                String regex = cvMap.get(target).get(0);
                String prefix = myHttpClient.prefixMap.get(target);
                // if the pmid is valid
                if (regexTest(regex, pmid, target, log)
                        && myHttpClient.accessTest(pmid, prefix, target, log)) {
                    doi = myHttpClient.get_doi(pmid);
                    // note: for pmid, there maybe not a doi
                    if (doi == null) {
                        Excel2Database.excel2DBLog
                                .writeLine("ValidTest for pmid: '"
                                        + pmid
                                        + "'. Warning: can't not pull the information of doi from it");
                        // result=false;
                    } else {
                        table.setValue("manuscript", doi, i);
                    }
                } else {
                    result = false;
                    doi = null;
                }
            }
        }
        return result;
    }

    public boolean speciesTest(Table table, String target, String prefix,
                               String log) throws HttpException, IOException {
        boolean result = true;
        int size = table.recordList.size();
        System.out.println(size);
        for (int i = 0; i < size; i++) {
            String postfix = table.getValue(target, i);
            if (myHttpClient.accessTest(postfix, prefix, target, log))
                result = common_nameTest(table, i, prefix, log);
            else
                result = false;
        }
        // isValid &= result;
        return result;
    }

    boolean validTest(Table table, String target, String log, String log_field)
            throws IOException {
        int size = table.recordList.size();
        boolean result = true;
        for (int i = 0; i < size; i++) {
            String content = table.getValue(target, i);
            String field = table.getValue(log_field, i);
            if (!validTest(target, content, log + field))
                result = false;
        }
        // isValid &= result;
        return result;
    }

    boolean accessTest(Table table, String target, String prefix, String log)
            throws HttpException, IOException {
        int size = table.recordList.size();
        boolean result = true;
        for (int i = 0; i < size; i++) {
            String postfix = null;
            if (target == "project_url" || target == "link_url")
                postfix = table.getValue("url", i);
            else
                postfix = table.getValue(target, i);
            //System.out.println("url: "+postfix);
            if (!myHttpClient.accessTest(postfix, prefix, target, log))
                result = false;
        }
        // isValid &= result;
        return result;
    }

    // test if the ext_acc can be accessed and test if the url is valid or not
    boolean ext_acc_Test(Table table, String target, String log)
            throws IOException {
        int size = table.recordList.size();
        boolean result = true;
        for (int i = 0; i < size; i++) {
            String url = null;
            String ext_acc = table.getValue("link", i);
            String is_primary = table.getValue("is_primary", i);
            if (is_primary == "TRUE")
                target = "ext_acc_link";
            else
                target = "ext_acc_mirror";
            String regex = "\\w+:\\w+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(ext_acc);
            if (matcher.find()) {
                String[] temp = ext_acc.split(":");
                String prefix = myHttpClient.prefixMap.get(temp[0].trim());
                if (prefix.equalsIgnoreCase("GEO") || prefix.equalsIgnoreCase("SRA")) {
                    if (checksample == false) {
                        Excel2Database.excel2DBLog
                                .writeLine("ValidTest for ext_acc_link:"
                                        + "Warning there may be a BioSample ID available");
                    }
                }
                String postfix = temp[1].trim();
                result = myHttpClient.accessTest(postfix, prefix, target,
                        " Warning for '" + ext_acc + "'");
            } else {
                if (!regexTest(urlRegex, ext_acc, target, log)) {
                    result = false;
                    continue;
                }
                result = myHttpClient.accessTest(ext_acc, "", target, log);
            }
        }
        // isValid &= result;
        //ext_acc should be return true
        return true;
    }

    boolean common_nameTest(Table table, int i, String prefix, String log)
            throws HttpException, IOException {
        String common_name = table.getValue("common_name", i);
        String tax_id = table.getValue("tax_id", i);

        System.out.println("id   " + tax_id);
        String url = HelpFunctions.getUrl(prefix, tax_id);
        String[] pullNameArray = new String[2];
        HashSet<String> nameSet = myHttpClient.getCommon_name(url,
                pullNameArray);
        // add genbank_name and scientific name
        table.recordList.get(i).set(table.getIndex("scientific_name"),
                pullNameArray[0]);
        table.recordList.get(i).set(table.getIndex("genbank_name"),
                pullNameArray[1]);
        // here
        if (nameSet == null) {
            return false;
        }
        if (nameSet.contains(common_name.toLowerCase()))
            return true;
        else {
            Excel2Database.excel2DBLog.writeLine("ValidTest for common_name: '"
                    + common_name + "'. Warning for tax_id: " + tax_id
                    + " , the right one should be in: "
                    + HelpFunctions.SettoString(nameSet));
            return true;
        }
    }

    boolean validTest(Table table, String target, String log)
            throws IOException {
        boolean result = true;
        int size = table.recordList.size();
        for (int i = 0; i < size; i++) {
            String content = table.getValue(target, i);
            System.out.println("contentXXX: " + content);
            if (target.equals("link")) {
                // test if it is a url or not, we test only when it isn't a url
                String is = table.getValue("is_primary", i);
                if (is == "TRUE")
                    target = "ext_acc_link";
                else
                    target = "ext_acc_mirror";
                if (!content.startsWith("http"))
                    result &= validTest(target, content.split(":")[0].trim(),
                            log);
                target = "link";
            } else {
                result &= validTest(target, content, log);
            }
        }
        // isValid &= result;
        return result;
    }

    boolean validTestfordoirelationship(Table table, String target, String log)
            throws IOException {
        boolean result = true;
        int size = table.recordList.size();
        for (int i = 0; i < size; i++) {
            String content = table.getValue(target, i);
            ArrayList<String> values = cvMap.get(target);
            System.out.println("target: " + target);
            System.out.println("content: " + content);
            System.out.println("values: " + values);
            if (!HelpFunctions.findInArray(content, values)) {

                //if(content!="IsNewVersionOf"||content!="IsSupplementTo"||content!="IsSupplementedBy"||content!="IsPreviousVersionOf"||content!="IsPartOf"||content!="HasPart"||content!="References"||content!="IsReferencedBy")
                //{
                result = false;
                Excel2Database.excel2DBLog.writeLine("validTest for " + target
                        + " : " + content + " doi_relationship is wrong type.");
            }

        }

        return result;
    }

    boolean validTestforprojectimage(String target, String content, String log)
            throws IOException {
        boolean result = true;
        if (content == null || content.equals("") || content.equals("null")) {
            Excel2Database.excel2DBLog.writeLine("validTest for " + target
                    + " : '" + "project_image_location the file does not exist");


        }

        return result;


    }


    // see Controlled vocabulary test
    boolean validTest(String target, String content, String log)
            throws IOException {
        boolean result = true;
        if (content == null || content.equals("") || content.equals("null"))
            ;
        else {
            if (target == "name")
                target = "dataset_type";


            ArrayList<String> values = cvMap.get(target);
            System.out.println("target: " + target);
            System.out.println("content: " + content);
            System.out.println("values: " + values);
            if (!HelpFunctions.findInArray(content, values)) {
                Excel2Database.excel2DBLog.writeLine("validTest for " + target
                        + " : '" + content + "' " + " error " + log);
                // excel2DBLog.writeLine(log);
                result = false;
            }
        }
        // isValid &= result;
        return result;
    }

    boolean validTestforstatus(String target, String content, String date, String log)
            throws IOException {
        boolean result = true;
        ArrayList<String> values = cvMap.get(target);
        if (!HelpFunctions.findInArray(content, values)) {
            Excel2Database.excel2DBLog.writeLine("validTest for " + target
                    + " : '" + "Value is not correct");
            result = false;
        } else {
            //ArrayList<String> values = cvMap.get(target); ���\�N���H�H FindinArray??
            if (content.equalsIgnoreCase("HUP") && date != null) {
                Excel2Database.excel2DBLog.writeLine("validTest for " + target
                        + " : '" + "HUP status should have no publication_date");
                // excel2DBLog.writeLine(log);
                result = false;
            }
            if (content.equalsIgnoreCase("Published") && date == null) {
                Excel2Database.excel2DBLog.writeLine("validTest for " + target
                        + " : '" + "Published status should have publication_date");
                // excel2DBLog.writeLine(log);
                result = false;

            }
        }
        // isValid &= result;
        return result;
    }

    boolean regexTest(Table table, String target, String regex, String log)
            throws IOException {
        boolean result = true;
        int size = table.recordList.size();
        for (int i = 0; i < size; i++) {
            String content = table.getValue(target, i);
            result &= regexTest(regex, content, target, log);
        }
        // isValid &= result;
        return result;
    }

    boolean file_sizeTest(Table table, String target, String log)
            throws IOException {
        boolean result = true;
        int size = table.recordList.size();
        for (int i = 0; i < size; i++) {
            String content = table.getValue(target, i);
            if (content.equals("0")) {
                result = false;
                String file_name = table.getValue("name", i);
                Excel2Database.excel2DBLog.writeLine("ValidTest for " + target
                        + " Error; it's a empty file!" + " file_name: " + file_name + log);
            }
        }
        // isValid &= result;
        return result;
    }

    static boolean regexTest(String regex, String content, String field,
                             String log) throws IOException {
        if (content == null || content.equals("null") || content.equals(""))
            return true;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            Excel2Database.excel2DBLog.writeLine("ValidTest for " + field
                    + " : '" + content + "' " + "url error " + log);
            return false;
        }
        return true;
    }

    boolean ftpTest(String ftp_site, String url, String field, String log)
            throws IOException {
        boolean result = true;


        ftpClient.connect(ftp_site);
        ftpClient.login("", "");

        int beginIndex = 0;
        if (url.indexOf(ftp_site) != -1)
            beginIndex = url.indexOf(ftp_site) + ftp_site.length();
        String location = url.substring(beginIndex);
        try {
            ftpClient.changeWorkingDirectory(location);
        } catch (Exception e) {
            // TODO: handle exception
            ftpClient.disconnect();
            ftpClient.disconnect();
            Excel2Database.excel2DBLog.writeLine("ValidTest for " + field
                    + " : '" + url + "' cann't be resolved." + log);

            result = false;
        }
        ftpClient.disconnect();
        return result;
    }

    boolean uniqueProjectNameTest(Table table) throws SQLException, IOException {
        int size = table.recordList.size();
        boolean result = true;
        for (int i = 0; i < size; i++) {
            String url = table.getValue("url", i);
            String pattern = null;
            if (url.endsWith("/"))
                pattern = url.substring(0, url.length() - 1) + "%";
            else
                pattern = url + "%";
            String name = table.getValue("name", i);
            String query = "select name from project "
                    + "where url='" + pattern + "'";
            ResultSet resultSet = Excel2Database.database.stmt
                    .executeQuery(query);
            if (resultSet.next()) {
                String oldName = resultSet.getString(1);
                if (!name.equals(oldName)) {
                    result = false;
                    Excel2Database.excel2DBLog.writeLine("ValidTest for "
                            + "project_name : '" + name
                            + "' doesn't equal to the " + " '" + oldName
                            + "' in database for project_url: " + url);
                }
            }

        }
        // isValid &= result;
        return result;
    }

    public boolean attribute_id_Test(Table table, String target)
            throws HttpException, IOException {
        boolean result = true;
        int size = table.recordList.size();
        for (int i = 0; i < size; i++) {
            String attribute_id = table.getValue("attribute_id", i);
            String sample_id = table.getValue("sample_id", i);

            System.out.println("attribute_id: " + attribute_id);
            System.out.println("sample_id: " + sample_id);
            if (attribute_id.equals(null)) {
                result = false;
                break;
            } else if (Integer.valueOf(attribute_id) == -1) {
                result = false;
                break;
            }

        }


        return result;

    }

    public boolean sample_idTest(Table table, String target, String log)
            throws HttpException, IOException {
        int size = table.recordList.size();
        boolean result = true;
        String prefix = myHttpClient.prefixMap.get("SAMPLE");
        for (int i = 0; i < size; i++) {
            String url = null;
            String sample_id = table.getValue("code", i);
            String regex = "SAMPLE:(\\w+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sample_id);
            if (matcher.find()) {
                checksample = true;
                int index_sample_attributes = table
                        .getIndex("s_attrs");
                if (!myHttpClient.accessTest(matcher.group(1), prefix, target,
                        log)) {
                    result = false;
                    System.out.println("pull sample_attribute:" + matcher.group(1));
                    System.out.println("wrong");

                } else {
                    // pull information of sample_attributes
                    System.out.println("pull sample_attribute:" + matcher.group(1));
                    String sample_attributes = myHttpClient
                            .get_sample_attributes(matcher.group(1));
                    if (sample_attributes.length() != 0) {
                        table.recordList.get(i).set(index_sample_attributes,
                                sample_attributes);
                    }
                }
            }
        }
        // isValid &= result;
        return result;
    }

    public boolean sample_attributesTest(Table table, String target, String log)
            throws HttpException, IOException {
        int size = table.recordList.size();
        boolean result = true;
        String prefix = myHttpClient.prefixMap.get("DOID");
        for (int i = 0; i < size; i++) {
            String url = null;
            String sample_attributes = table.getValue("s_attrs", i);

            if (sample_attributes.contains("DOID")) {
                String[] arr = sample_attributes.split(":");
                if (!myHttpClient.accessTest(arr[1], prefix, target,
                        log)) {
                    result = false;
                }
            }
        }

        // isValid &= result;
        return result;
    }


    void printPage(String urls) throws HttpException, IOException, SAXException {

        UserAgentContext uacontext = new SimpleUserAgentContext();

        DocumentBuilderImpl builder = new DocumentBuilderImpl(uacontext);

        URL url = new URL(urls);

        InputStream in = url.openConnection().getInputStream();
        char[] buffer = new char[1000];
        try {

            Reader reader = new InputStreamReader(in, "utf-8");
            InputSourceImpl inputSource = new InputSourceImpl(reader, urls);

            Document d = builder.parse(inputSource);
            System.out.print(d.getTextContent());

        } finally {

            in.close();

        }
        // not found the webpage
    }


    public boolean containFiles(Table table, Table table1, String field) throws IOException, SQLException {
        ArrayList<String> fileList = cvMap.get("mandatory_file");
        for (String target : fileList) {
            boolean found = false;
            if (table.recordList.size() > 0) {
                for (int i = 0; i < table.recordList.size(); i++) {
                    String name = table.getValue("name", i);//file_type to  file_name
                    System.out.println("xxxxx:" + name);
                    if (name.equals(target)) {
                        found = true;
                        break;
                    }
                }
            }// for
            if (found == false) {
                for (int i = 0; i < table1.recordList.size(); i++) {
                    String type_id = table1.getValue("type_id", i);
                    String file_type = database.checkcontain1("file_type", "id", type_id, "name");
                    if (file_type.equals(target)) {
                        found = true;
                        break;
                    }
                }

            }
            if (!found) {
                Excel2Database.excel2DBLog.writeLine("ValidTest for "
                        + "necessary file : " + target
                        + " Warning: doesn't exist.");
            }

        }
        return true;
    }

    public String get_stamp(Table table, String target) {
        String stamp = "";
        int size = table.recordList.size();
        int year = 0, month = 0, day = 0;

        for (int i = 0; i < size; i++) {
            String content = table.getValue(target, i);
            String[] ss2;
            if (content.contains("-")) ;
            {
                ss2 = content.split("\\-");
            }
            if (content.contains("/")) {
                ss2 = content.split("/");
            }

            if (Integer.parseInt(ss2[0]) > year) {
                year = Integer.parseInt(ss2[0]);
                month = Integer.parseInt(ss2[1]);
                day = Integer.parseInt(ss2[2]);
            }
            if (Integer.parseInt(ss2[0]) == year) {
                if (Integer.parseInt(ss2[1]) > month) {
                    month = Integer.parseInt(ss2[1]);
                    day = Integer.parseInt(ss2[2]);
                }
                if (Integer.parseInt(ss2[1]) == month) {
                    if (Integer.parseInt(ss2[2]) > day) {
                        day = Integer.parseInt(ss2[2]);
                    }
                }
            }
        }
        stamp = year + "-" + month + "-" + day;
        System.out.println("latest date   " + stamp);

        return stamp;

    }

    public static void main(String[] args) throws Exception {
        Validation validation = new Validation();
    }

}
