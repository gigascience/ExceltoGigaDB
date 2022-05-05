import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.ParserConfigurationException;


import org.apache.commons.httpclient.HttpException;
import org.apache.commons.net.*;
import org.apache.commons.net.ftp.*;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.SAXException;


import Log.Log;

/**
 *
 */

/**
 * @author Jesse transfer data in excel files into dataset
 * @date 2012-3-31
 */
public class Excel2Database {
    Workbook workbook;
    Sheet studySheet;
    Sheet samplesSheet;
    Sheet filesSheet;
    Sheet linkSheet;
    Schema schema;
    Projects projects;
    String path;
    File file;
    FormulaEvaluator evaluator;
    boolean isValid;
    String datasettype;
    ArrayList<String> sampleid = new ArrayList<String>();
    static Database database;
    // static Validation validation;
    static Extension2Type extension2Type;
    //email
    Boolean send_email = false;
    String submitter_name;
    String email_identifier;
    String email;
    String author_list;
    String publication_year;
    String title;
    String publisher;
    String email_identifier2;

    int user_id = 0;
    int dataset_id = 0;
    int dataset_attribute_id = 0;
    int relation_id = 0;
    int image_id = 0;
    int publisher_id = 0;
    int log_id = 0;
    //int type_id=0;

    ArrayList<String> external_link_add_id = new ArrayList<String>();
    ArrayList<String> external_link_gen_id = new ArrayList<String>();
    ArrayList<String> authorid = new ArrayList<String>();
    ArrayList<String> projectid = new ArrayList<String>();
    ArrayList<String> typeid = new ArrayList<String>();
    ArrayList<String> curationlog = new ArrayList<String>();

    HashMap<String, Integer> map_species = new HashMap<String, Integer>();
    HashMap<String, Integer> map_sample = new HashMap<String, Integer>();
    HashMap<String, Integer> map_file_type = new HashMap<String, Integer>();
    HashMap<String, Integer> map_file_format = new HashMap<String, Integer>();
    HashMap<String, String> map_sample_attribute = new HashMap<String, String>();
    HashMap<String, Integer> map_file_sample = new HashMap<String, Integer>();


    static String[] comExt = {"7z", "arj", "bz2", "bzip2", "cab", "cpio",
            "deb", "dmg", "gz", "gzip", "hfs", "iso", "lha", "lzh", "lzma",
            "rar", "rpm", "split", "swm", "tar", "taz", "tbz", "tbz2", "tgz",
            "tpz", "wim", "xar", "z", "zip"};

    static Log excel2DBLog = new Log("logFiles/" + "databaseLog"
            + HelpFunctions.datePostfix() + ".txt", false);
    ;

    static HashSet<String> compressExtension = new HashSet<String>(Arrays
            .asList(comExt));

    MyHttpClient myHttpClient;
    HashMap<String, String> locationMap;

    @SuppressWarnings("static-access")
    Excel2Database(String path) throws IOException,
            ParserConfigurationException, SAXException {
        if (!path.endsWith("xls") || !path.endsWith("xlsx"))
            MyFrame.textArea.setText(path + " is not an excel file");
        try {
            file = new File(path);
            // file doesn't exist
            if (!file.exists())
                return;
            this.path = path;
            // create an instance
            workbook = WorkbookFactory.create(file);
            // test
            // MyFrame.textArea.setText("asdfasdfasdfasdfadf"+workbook.getNumberOfSheets());
            filesSheet = workbook.getSheet("Files");
            samplesSheet = workbook.getSheet("Samples");
            studySheet = workbook.getSheet("Study");
            linkSheet = workbook.getSheet("Links");
            evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            schema = new Schema();
            projects = new Projects();
            database = new Database();
            extension2Type = new Extension2Type();
            myHttpClient = new MyHttpClient();

            //
            // read xml file

            try {
                locationMap = HelpFunctions.getMap("configuration/doi-image.xml");
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace(excel2DBLog.printWriter);
            }
        } catch (Exception e) {
            // TODO: handle exception
            excel2DBLog.writeLine(e.toString());
            e.printStackTrace();
            MyFrame.textArea.setText(MyFrame.textArea.getText() + "\n" + path);
            MyFrame.textArea.setText(MyFrame.textArea.getText() + e.toString());
        }
    }

    //there is no close api in apachi poi
    public void close() {
//
    }

    public void setInsertOrder(String table) {
        int num = schema.indexOf(table);
        schema.insertOrderList.add(num);
    }

    public boolean isReserved() throws SQLException, IOException {
        String identifier = getIdentifier();

        boolean result = database.exist("reserved_dataset", "identifier", identifier);
        if (result) {
            excel2DBLog.writeLine("Valid Test for identifier: '" + identifier +
                    "' Error: it is reserved. You can't upload it.");
        } else
            System.out.println(identifier + " is not reserved. You can use it.");
        return result;
    }

    public void comparison() throws SQLException, IOException {

        String identifier = getIdentifier();
        boolean result = database.exist("dataset", "identifier", identifier);
        if (result) {
            excel2DBLog.writeLine("The dataset: '" + identifier +
                    " has already in database. Start compare old dataset with new dataset");
            compare_dataset(identifier);
            // compare_file(identifier);
            compare_sample(identifier);

            excel2DBLog.writeLine("End success.");

        }
    }

    public void compare_sample(String identifier) throws IOException, SQLException {
        Table table = schema.getTable("sample");
        ArrayList<String> sample_id = database.compare_result_in("code", "sample", "id", "sample_id", "dataset_sample", "dataset_id", String.valueOf(dataset_id));
        int size = table.recordList.size();
        for (int i = 0; i < size; i++) {
            String content = table.getValue("code", i);
            boolean flag = true;
            for (int j = 0; j < sample_id.size(); j++) {
                String temp = sample_id.get(j);
                if (content.equals(temp)) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                excel2DBLog.writeLine("The new sample_id added into dataset: '" + content);

            else {
                String content_temp = table.getValue("species_id", i);
                int compare_value = Integer.valueOf(content_temp);
                String content2 = null;
                for (Object o : map_species.keySet()) {
                    String key = o.toString();
                    int value = (Integer) map_species.get(o);
                    if (value == compare_value)
                        content2 = key;

                }
                String query_temp = "select tax_id from species where exists ( select species_id from sample where sample.species_id= species.id and sample.code= " + "'" + content + "')";
                String temp2 = database.get_result(query_temp, "tax_id");
                //String temp2 = database.;
                boolean flag2 = true;
                if (temp2.equals(content2))
                    flag2 = false;
                if (flag2)
                    excel2DBLog.writeLine("The tax_id has changed to:  " + content2 +
                            " in sample_id " + content);


                String content3 = table.getValue("s_attrs", i);
                String temp3 = database.get_result("select s_attrs from sample where code= " + "'" + content + "'", "s_attrs");
                boolean flag3 = true;
                if (content3 != null && temp3 != null) {
                    if (temp3.equals(content3))
                        flag3 = false;
                    if (flag3)
                        excel2DBLog.writeLine("The sample attributes has changed" +
                                " in sample_id: " + content);
                }


            }


        }
    }


    public void compare_dataset(String identifier) throws SQLException, IOException {

        Table table = schema.getTable("dataset");
        ArrayList<String> publication_date = database.compare_result(identifier, "dataset", "publication_date");
        ArrayList<String> modification_date = database.compare_result(identifier, "dataset", "modification_date");
        ArrayList<String> publisher = database.compare_result(identifier, "dataset", "publisher_id");
        ArrayList<String> upload_status = database.compare_result(identifier, "dataset", "upload_status");
        ArrayList<String> description = database.compare_result(identifier, "dataset", "description");
        ArrayList<String> title = database.compare_result(identifier, "dataset", "title");
        ArrayList<String> dataset_size = database.compare_result(identifier, "dataset", "dataset_size");
        ArrayList<String> image_location = database.compare_result(identifier, "dataset", "image_id");

        String date = table.getValue("publication_date", 0);
        if (date == null && publication_date.get(0) != null)
            excel2DBLog.writeLine("You may forgot type publication_date the old one has it ");
        System.out.println("Test for d-date:" + publication_date.get(0));
        System.out.println("Test for t-date:" + date);
        if (publication_date.get(0) != null && date != null) {
            String[] ss1 = publication_date.get(0).split("\\-");
            String[] ss2 = date.split("\\/");
            boolean flag11 = true;
            if (ss1[0].equals(ss2[0]) && ss1[1].equals(ss2[1]) && ss1[2].equals(ss2[2]))
                flag11 = false;
            if (ss1[0].equals(ss2[0]) == false)//only compare year
                excel2DBLog.writeLine("You need new XML file : Publication_year has changed");

            if (flag11) {
                excel2DBLog.writeLine("The dataset publication_date has changed to: " + date +
                        " the old one is " + publication_date.get(0));
            }
        }


        String mo_date = table.getValue("modification_date", 0);
        if (mo_date == null && modification_date.get(0) != null)
            excel2DBLog.writeLine("You may forgot type modification_date the old one has it ");
        System.out.println("Test for m-date:" + modification_date.get(0));
        System.out.println("Test for m-date:" + mo_date);
        if (modification_date.get(0) != null && mo_date != null) {
            String[] ss1 = modification_date.get(0).split("\\-");
            String[] ss2 = mo_date.split("\\/");
            boolean flag111 = true;
            if (ss1[0].equals(ss2[0]) && ss1[1].equals(ss2[1]) && ss1[2].equals(ss2[2]))
                flag111 = false;
            if (flag111) {
                excel2DBLog.writeLine("The dataset modification_date has changed to: " + mo_date +
                        " the old one is " + modification_date.get(0));
                excel2DBLog.writeLine("You need new XML file : modification_date has changed");
            }
        }


        String pub = table.getValue("publisher_id", 0);
        if (publisher.get(0) != null) {
            boolean flag112 = true;
            if (pub.equals(publisher.get(0)))
                flag112 = false;
            if (flag112) {
                excel2DBLog.writeLine("The dataset publisher has changed to: " + pub +
                        " the old one is " + publisher.get(0));
                excel2DBLog.writeLine("You need new XML file : publisher has changed");
            }
        }


        String status = table.getValue("upload_status", 0);
        System.out.println("Test for d-status:" + upload_status.get(0));
        System.out.println("Test for t-status:" + status);
        boolean flag12 = true;
        if (status.equals(upload_status.get(0)))
            flag12 = false;
        if (flag12) {
            excel2DBLog.writeLine("The dataset upload_status has changed to: " + status +
                    " the old one is " + upload_status.get(0));
            excel2DBLog.writeLine("You need new XML file : upload_status has changed");
        }

        String des = table.getValue("description", 0);
        boolean flag13 = true;
        if (des.equals(description.get(0)))
            flag13 = false;
        if (flag13) {
            excel2DBLog.writeLine("The dataset description has changed to: " + des +
                    " the old one is " + description.get(0));
            excel2DBLog.writeLine("You need new XML file : description has changed");
        }

        String ti = table.getValue("title", 0);
        boolean flag14 = true;
        if (ti.equals(title.get(0)))
            flag14 = false;
        if (flag14) {
            excel2DBLog.writeLine("The dataset title has changed to: " + ti +
                    " the old one is " + description.get(0));
            excel2DBLog.writeLine("You need new XML file : title has changed");
        }

        String si = table.getValue("dataset_size", 0);
        boolean flag15 = true;
        if (si.equals(dataset_size.get(0)))
            flag15 = false;
        if (flag15) {
            excel2DBLog.writeLine("The dataset size has changed to: " + ti +
                    " the old one is " + dataset_size.get(0));
        }


        //String im1 =table.getValue("image_id", 0);
        Table table_temp = schema.getTable("image");
        String im = table_temp.getValue("location", 0);
        if (im == null && image_location.size() > 0)
            excel2DBLog.writeLine("You may forgot type image_location the old one has it ");
        boolean flag16 = true;
        String im1 = database.get_result("select location from image where id=" + image_location.get(0), "location");
        //	System.out.println("xxxxxxxxxxxxxxxxxxxxxssssssssssssss"+im1);
        if (im.equals(im1))
            flag16 = false;
        if (flag16) {
            excel2DBLog.writeLine("The image_location has changed to: " + im +
                    " the old one is " + im1);
        }


        Table table1 = schema.getTable("author");
        ArrayList<String> list_au_temp = database.compare_result_all(String.valueOf(dataset_id), "dataset_id", "dataset_author", "author_id");
        ArrayList<String> list_au = new ArrayList<String>();
        for (int i = 0; i < list_au_temp.size(); i++) {
            String temp_author = database.get_result("select name from author where id=" + list_au_temp.get(i), "name");
            //System.out.println("xxxxxxxxxxxxxxxxxxxxxssssssssssauthor"+list_au_temp.get(i));
            list_au.add(temp_author);

        }
        int size = table1.recordList.size();
        if (authorid.size() < 1 && list_au.size() > 0)
            excel2DBLog.writeLine("You may forgot type author name ");

        for (int i = 0; i < authorid.size(); i++) {
            String author = authorid.get(i);
            String author_name = database.get_result("select name from author where id=" + author, "name");
            if (author_name == null) {
                for (int k = 0; k < size; k++) {
                    String author_name_temp = table1.getValue("id", k);
                    if (author_name_temp.equals(author)) {
                        author_name = table1.getValue("name", k);
                        break;
                    }


                }

            }
            //System.out.println("author in table : " + author);
            boolean flag17 = true;
            for (int j = 0; j < list_au.size(); j++) {
                String temp = list_au.get(j);
                //System.out.println("author in database : " + temp);
                if (author_name.equals(temp)) {
                    flag17 = false;
                    break;
                }
            }
            if (flag17) {
                excel2DBLog.writeLine("The author is new added into dataset: " + author_name);
                excel2DBLog.writeLine("You need new XML file : author has changed");
            }
        }


        Table table2 = schema.getTable("manuscript");
        ArrayList<String> list_man = database.compare_result_all(String.valueOf(dataset_id), "dataset_id", "manuscript", "identifier");
        int size2 = table2.recordList.size();
        if (size2 == 0 && list_man.size() > 0)
            excel2DBLog.writeLine("You may forgot type related_manuscript, the old one has it");
        for (int i = 0; i < size2; i++) {
            String manuscript = table2.getValue("identifier", i);
            System.out.println("manuscript_doi in table : " + manuscript);
            boolean flag18 = true;
            for (int j = 0; j < list_man.size(); j++) {
                String temp = list_man.get(j);
                System.out.println("manuscript_doi in database : " + temp);
                if (manuscript.equals(temp)) {
                    flag18 = false;
                    break;
                }
            }
            if (flag18) {
                excel2DBLog.writeLine("The manuscript_doi is changed to : " + manuscript);
                excel2DBLog.writeLine("You need new XML file : manuscript_doi has changed");
            }
        }


        Table table3 = schema.getTable("image");
        ArrayList<String> list_tag = database.compare_result_all(image_location.get(0), "id", "image", "tag");
        ArrayList<String> list_url = database.compare_result_all(image_location.get(0), "id", "image", "url");
        ArrayList<String> list_license = database.compare_result_all(image_location.get(0), "id", "image", "license");
        ArrayList<String> list_photographer = database.compare_result_all(image_location.get(0), "id", "image", "photographer");
        ArrayList<String> list_source = database.compare_result_all(image_location.get(0), "id", "image", "source");
        int size3 = table3.recordList.size();
        for (int i = 0; i < size3; i++) {
            String tag = table3.getValue("tag", i);
            System.out.println("image_tag in table : " + tag);
            boolean flag19 = true;
            for (int j = 0; j < list_tag.size(); j++) {
                String temp = list_tag.get(j);
                System.out.println("image_tag in database : " + temp);
                if (tag.equals(temp)) {
                    flag19 = false;
                    break;
                }
            }
            if (flag19) {
                excel2DBLog.writeLine("The image_tag is changed to : " + tag + " in image_location " + image_location.get(0));
            }

            String url = table3.getValue("url", i);
            System.out.println("image_url in table : " + url);
            boolean flag20 = true;
            for (int j = 0; j < list_url.size(); j++) {
                String temp = list_url.get(j);
                System.out.println("image_url in database : " + temp);
                if (url.equals(temp)) {
                    flag20 = false;
                    break;
                }
            }
            if (flag20) {
                excel2DBLog.writeLine("The image_url is changed to : " + tag + " in image_location " + image_location.get(0));
            }

            String license = table3.getValue("license", i);
            System.out.println("image_license in table : " + license);
            boolean flag21 = true;
            for (int j = 0; j < list_license.size(); j++) {
                String temp = list_license.get(j);
                System.out.println("image_license in database : " + temp);
                if (license.equals(temp)) {
                    flag21 = false;
                    break;
                }
            }
            if (flag21) {
                excel2DBLog.writeLine("The image_license is changed to : " + license + " in image_location " + image_location.get(0));
            }

            String photographer = table3.getValue("photographer", i);
            System.out.println("image_photographer in table : " + photographer);
            boolean flag22 = true;
            for (int j = 0; j < list_photographer.size(); j++) {
                String temp = list_photographer.get(j);
                System.out.println("image_photographer in database : " + temp);
                if (photographer.equals(temp)) {
                    flag22 = false;
                    break;
                }
            }
            if (flag22) {
                excel2DBLog.writeLine("The image_photographer is changed to : " + photographer + " in image_location " + image_location.get(0));
            }

            String source = table3.getValue("source", i);
            System.out.println("image_source in table : " + source);
            boolean flag23 = true;
            for (int j = 0; j < list_source.size(); j++) {
                String temp = list_source.get(j);
                System.out.println("image_source in database : " + temp);
                if (source.equals(temp)) {
                    flag23 = false;
                    break;
                }
            }
            if (flag23) {
                excel2DBLog.writeLine("The image_source is changed to : " + source + " in image_location " + image_location.get(0));
            }


        }


        Table table4 = schema.getTable("link");
        ArrayList<String> list_mirror = database.get_result_more("select link from link where dataset_id=" + dataset_id + " and is_primary=TRUE", "link");
        int size4 = table4.recordList.size();
        if (size4 == 0 && list_mirror.size() > 0)
            excel2DBLog.writeLine("You may forgot type ext_acc_mirror, the old one has it");
        for (int i = 0; i < size4; i++) {
            String mirror = table4.getValue("link", i);
            String is_primary = table4.getValue("is_primary", i);
            if (is_primary == "TRUE") {
                System.out.println("ext_acc_mirror in table : " + mirror);
                boolean flag24 = true;
                for (int j = 0; j < list_mirror.size(); j++) {
                    String temp = list_mirror.get(j);
                    System.out.println("ext_acc_mirror in database : " + temp);
                    if (mirror.equals(temp)) {
                        flag24 = false;
                        break;
                    }
                }
                if (flag24) {
                    excel2DBLog.writeLine("The ext_acc_mirror is changed to : " + mirror);
                }
            }
        }


        ArrayList<String> list_link = database.get_result_more("select link from link where dataset_id=" + dataset_id + " and is_primary=FALSE", "link");
        int size5 = table4.recordList.size();
        if (size5 == 0 && list_link.size() > 0)
            excel2DBLog.writeLine("You may forgot type ext_acc_link, the old one has it");
        for (int i = 0; i < size5; i++) {
            String link = table4.getValue("link", i);
            String is_primary = table4.getValue("is_primary", i);
            if (is_primary == "FALSE") {
                System.out.println("ext_acc_link in table : " + link);
                boolean flag25 = true;
                for (int j = 0; j < list_link.size(); j++) {
                    String temp = list_link.get(j);
                    System.out.println("ext_acc_link in database : " + temp);
                    if (link.equals(temp)) {
                        flag25 = false;
                        break;
                    }
                }
                if (flag25) {
                    excel2DBLog.writeLine("The ext_acc_link is changed to : " + link);
                }
            }
        }

        Table table6 = schema.getTable("project");
        ArrayList<String> list_project = database.get_result_more("SELECT project.url FROM project,dataset_project WHERE dataset_project.project_id = project.id AND dataset_project.dataset_id=" + dataset_id, "url");
        int size6 = table6.recordList.size();
        if (projectid.size() < 1 && list_project.size() > 0)
            excel2DBLog.writeLine("You may forgot type project_url, the old one has it");
        for (int i = 0; i < projectid.size(); i++) {
            String url_temp = projectid.get(i);
            String url = database.get_result("select url from project where id=" + url_temp, "url");
            if (url == null) {
                for (int k = 0; k < size6; k++) {
                    String url_flag = table6.getValue("id", k);
                    if (url_flag.equals(url_temp)) {
                        url = table6.getValue("url", k);
                        break;
                    }
                }
            }
            System.out.println("project_url in table : " + url);
            boolean flag26 = true;
            for (int j = 0; j < list_project.size(); j++) {
                String temp = list_project.get(j);
                System.out.println("project_url in database : " + temp);
                if (url.equals(temp)) {
                    flag26 = false;
                    break;
                }
            }
            if (flag26) {
                excel2DBLog.writeLine("The project_url is changed to : " + url);
            }
        }


        Table table7 = schema.getTable("type");
        ArrayList<String> list_type = database.get_result_more("select type.name from type,dataset_type where dataset_type.type_id=type.id and dataset_type.dataset_id=" + dataset_id, "name");
        int size7 = table7.recordList.size();
        if (typeid.size() < 1 && list_type.size() > 0)
            excel2DBLog.writeLine("You may forgot type dataset_type, the old one has it");
        for (int i = 0; i < typeid.size(); i++) {
            String type_temp = typeid.get(i);
            String type = database.get_result("select name from type where id=" + type_temp, "name");
            if (type == null) {
                for (int k = 0; k < size7; k++) {
                    String type_flag = table7.getValue("type_id", k);
                    if (type_flag.equals(type_temp)) {
                        type = table7.getValue("name", k);
                        break;
                    }
                }
            }

            System.out.println("dataset_type in table : " + type);
            boolean flag27 = true;
            for (int j = 0; j < list_type.size(); j++) {
                String temp = list_type.get(j);
                System.out.println("dataset_type in database : " + temp);
                if (type.equals(temp)) {
                    flag27 = false;
                    break;
                }
            }
            if (flag27) {
                excel2DBLog.writeLine("The dataset_type is changed to : " + type);
                excel2DBLog.writeLine("You need new XML file : dataset_type has changed");
            }
        }

        Table table8 = schema.getTable("relation");
        ArrayList<String> list_related_doi = database.get_result_more("select related_doi from relation where dataset_id=" + dataset_id, "related_doi");
        ArrayList<String> list_doi_relationship = database.get_result_more("select relationship from relation where dataset_id=" + dataset_id, "relationship");
        int size8 = table8.recordList.size();
        if (size8 == 0 && list_related_doi.size() > 0)
            excel2DBLog.writeLine("You may forgot type related_doi or doi_relationship , the old one has it");
        for (int i = 0; i < size8; i++) {
            String related = table8.getValue("related_doi", i);
            String relationship = table8.getValue("relationship", i);
            System.out.println("related_doi in table : " + related);
            System.out.println("doi_relationship in table : " + relationship);
            boolean flag281 = true;
            boolean flag282 = true;
            for (int j = 0; j < list_related_doi.size(); j++) {
                String temp = list_related_doi.get(j);
                System.out.println("related_doi in database : " + temp);
                if (related.equals(temp)) {
                    flag281 = false;
                    break;
                }
            }
            if (flag281) {
                excel2DBLog.writeLine("The related_doi is changed to : " + related);
                excel2DBLog.writeLine("You need new XML file : related_doi has changed");
            }

            for (int j = 0; j < list_related_doi.size(); j++) {
                String temp = list_doi_relationship.get(j);
                System.out.println("doi_relationship in database : " + temp);
                if (relationship.equals(temp)) {
                    flag282 = false;
                    break;
                }
            }
            if (flag282) {
                excel2DBLog.writeLine("The doi_relationship is changed to : " + relationship + " in " + related);
                excel2DBLog.writeLine("You need new XML file : doi_relationship has changed");
            }


        }


    }


    public void compare_file(String identifier) throws SQLException, IOException {


        Table table = schema.getTable("file");
        int size = table.recordList.size();
        ArrayList<String> file_location = database.get_result_more("select location from file where dataset_id=" + dataset_id, "location");


        for (int i = 0; i < size; i++) {
            String content1 = table.getValue("location", i);
            boolean flag = true;
            for (int j = 0; j < file_location.size(); j++) {
                String temp = file_location.get(j);
                if (content1.equals(temp)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                excel2DBLog.writeLine("The file location: '" + content1 +
                        " is new in dataset");
                excel2DBLog.writeLine("You need new XML file : the sum of file size has changed");

            }
        }
    }

    public void fillTable_file_format() throws IOException, SQLException {
        setInsertOrder("file_format");
        Table table = schema.getTable("file_format");
        int temp = database.getid("file_format");
        HashSet<String> previousRecords = new HashSet<String>();

        for (int row = 2; row < filesSheet.getLastRowNum() + 1; row++) {

            if (filesSheet == null) {
                System.out.print("sheet empty");
            }

            if (!next(filesSheet, row)) {
                System.out.print("empt row y: " + row);
                break;
            }
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, table.attributeList.size());
            Cell cell = HelpFunctions.getCell(filesSheet, row, 0);
            String content;
            if (cell.getCellType() == 0) {
                content = HelpFunctions.trim(Double.toString(cell.getNumericCellValue()));
            } else
                content = HelpFunctions.trim(cell.getStringCellValue());

            String file_name = getFile_name(content);
            System.out.println("file_Name: " + file_name);
            String file_extension = getFile_extension(file_name);
            System.out.println("file_extension: " + file_extension);
            String file_format = getFile_format(file_extension);
            if (previousRecords.contains(file_format)) {
                continue;
            } else {

                previousRecords.add(file_format);
            }
            String flag = database.checkcontain1("file_format", "name", file_format, "id");
            System.out.println("flag in file_format:   " + flag);
            if (flag == null) {

                record.set(table.getIndex("id"), String.valueOf(temp));
                record.set(table.getIndex("name"), file_format);
                record.set(table.getIndex("description"), "");

                map_file_format.put(file_format, temp);
                temp = temp + 1;
                table.recordList.add(record);
            } else
                map_file_format.put(file_format, Integer.parseInt(flag));
        }

    }

    public void fillTable_file_type() throws IOException, SQLException {
        setInsertOrder("file_type");
        Table table = schema.getTable("file_type");
        Table table1 = schema.getTable("type");
        int temp = database.getid("file_type");
        HashSet<String> previousRecords = new HashSet<String>();

        for (int row = 2; row < filesSheet.getLastRowNum() + 1; row++) {

            if (filesSheet == null) {
                System.out.print("sheet empty");
            }

            if (!next(filesSheet, row)) {
                System.out.print("empt row y: " + row);
                break;
            }
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, table.attributeList.size());
            Cell cell = HelpFunctions.getCell(filesSheet, row, 1);
            String content;
            if (cell.getCellType() == 0) {
                content = HelpFunctions.trim(Double.toString(cell.getNumericCellValue()));
            } else
                content = HelpFunctions.trim(cell.getStringCellValue());
            String type_name = table1.getValue("name", 0);

            if (previousRecords.contains(content)) {
                continue;
            } else {

                previousRecords.add(content);
            }

            if (content == "Alignments" || content == "Allele frequencies" || content == "Coding sequence"
                    || content == "InDels" || content == "Genome assembly" || content == "Haplotypes" || content == "SNPS"
                    || content == "SVs") {
                if (type_name != "Genomic") {
                    excel2DBLog.writeLine("ValidationTest for file_type: " + "  dataset_type should include 'Genomic'");
                }
            }

            if (content == "Transcriptome") {
                if (type_name != "Transcriptomic") {
                    excel2DBLog.writeLine("ValidationTest for file_type: " + "  dataset_type should include 'Transcriptomic'");
                }
            }

            if (content == "Methlome") {
                if (type_name != "Epigenomic") {
                    excel2DBLog.writeLine("ValidationTest for file_type: " + "  dataset_type should include 'Epigenomic'");
                }
            }

            String flag = database.checkcontain1("file_type", "name", content, "id");
            System.out.println("flag in file_type:   " + flag);

            if (flag == null) {
                System.out.println("temp:  " + temp);
                System.out.println("Index:  " + table.getIndex("id"));
                System.out.println("Index:  " + table.getIndex("name"));
                record.set(table.getIndex("id"), String.valueOf(temp));
                record.set(table.getIndex("name"), content);
                record.set(table.getIndex("description"), "");
                map_file_type.put(content, temp);
                temp = temp + 1;
                table.recordList.add(record);
            } else
                map_file_type.put(content, Integer.valueOf(flag));
        }

    }

    public void fillTable_file_sample() throws IOException, SQLException {

        setInsertOrder("file_sample");
        Table table = schema.getTable("file_sample");
        ArrayList<Integer> indexList = new ArrayList<Integer>();

        System.out.println("colNum: " + filesSheet.getLastRowNum());

        int index_id = table.getIndex("id");
        int index_file_id = table.getIndex("file_id");
        int index_sample_id = table.getIndex("sample_id");


        int temp = database.getid("file_sample");

        for (int row = 2; row < filesSheet.getLastRowNum() + 1; row++) {

            if (filesSheet == null) {
                System.out.print("sheet empty");
            }

            if (!next(filesSheet, row)) {
                System.out.println("empt row y: " + row);
                break;
            }
            // we need to set the size of record, it's the same with

            ArrayList<String> record = new ArrayList<String>();
            Cell cell = HelpFunctions.getCell(filesSheet, row, 3);
            Cell cell1 = HelpFunctions.getCell(filesSheet, row, 0);
            String content = null;
            if (cell.getCellType() == 0) {
                content = HelpFunctions.trim(Double.toString(cell.getNumericCellValue()));

            } else
                content = HelpFunctions.trim(cell.getStringCellValue());
            String file_location = HelpFunctions.trim(cell1.getStringCellValue());
            String[] aa = content.split(";");
            for (String sample : aa) {
                initRecord(record, table.attributeList.size());
                System.out.println("sample name: " + sample);
                if (sample.equals("") || sample.equals(null) || sample == "") {
                    continue;
                }
                if (map_sample.get(sample) == null) {
                    continue;
                }
                int sample_id = map_sample.get(sample);
                int file_id = map_file_sample.get(file_location);
                record.set(index_id, String.valueOf(temp));
                record.set(index_sample_id, String.valueOf(sample_id));
                record.set(index_file_id, String.valueOf(file_id));
                table.recordList.add(record);
                temp += 1;
            }

        }//rows loop


    }

    public void fillTable_file() throws IOException, SQLException {
        setInsertOrder("file");
        Table table = schema.getTable("file");
        ArrayList<Integer> indexList = new ArrayList<Integer>();

        setInsertOrder("file_attributes");
        Table table1 = schema.getTable("file_attributes");

        // read records
        // getLastRowNum() start from 0 ?
        System.out.println("colNum: " + filesSheet.getLastRowNum());
        int file_id = database.getid("file");
        int file_attribute_id = database.getid("file_attributes");


        for (int row = 2; row < filesSheet.getLastRowNum() + 1; row++) {

            if (filesSheet == null) {
                System.out.print("sheet empty");
            }

            if (!next(filesSheet, row)) {
                System.out.println("empt row y: " + row);
                break;
            }
            // we need to set the size of record, it's the same with

            ArrayList<String> record = new ArrayList<String>();
            // init record
            initRecord(record, table.attributeList.size());
            String location_pk = null;


            for (int col = 0; col < 6; col++) {
                Cell cell = HelpFunctions.getCell(filesSheet, row, col);

                System.out.println(col);
                String content = null;
                if (cell == null) {
                    continue;
                }
                if (cell.getCellType() == 0) {
                    content = HelpFunctions.trim(Double.toString(cell.getNumericCellValue()));

                } else
                    content = HelpFunctions.trim(cell.getStringCellValue());

                //file_type
                if (col == 1) {
                    int value = (Integer) map_file_type.get(content);
                    System.out.println(content);
                    record.set(table.getIndex("type_id"), String.valueOf(value));

                }
                //sample_id
                if (col == 3) {
                    if (content.isEmpty()) {
                        Cell cell_temp = HelpFunctions.getCell(filesSheet, row, 0);
                        String content_temp;
                        if (cell.getCellType() == 0) {
                            content_temp = HelpFunctions.trim(Double.toString(cell_temp.getNumericCellValue()));
                        } else
                            content_temp = HelpFunctions.trim(cell_temp.getStringCellValue());

                        String file_name_t = getFile_name(content_temp);
                        excel2DBLog.writeLine("ValidationTest for file/sample_id: " + "The file name: " + file_name_t + " No reference any sample/sample_id");
                    } else {


                        record.set(table.getIndex("code"), content);

                    }

                }

                // file_location file_path
                if (col == 0
                        && !content.contains("ftp")) {
                    String ftp_site = schema.getTable("dataset").getValue(
                            "ftp_site", 0);
                    map_file_sample.put(content, file_id);
                    if (content.startsWith("/") && ftp_site.endsWith("/"))
                        content = content.substring(1);
                    content = ftp_site + content;
                    record.set(table.getIndex("location"), content);
                    location_pk = content;

                    record.set(table.getIndex("id"), String.valueOf(file_id));
                    //}
                    // remove the duplicate/
                }

                if (col == 2) {
                    if (content != null)
                        record.set(table.getIndex("description"), content);
                    else
                        record.set(table.getIndex("description"), "");

                    System.out.println(content);
                }

                if (col > 3) {
                    if (content != null) {
                        ArrayList<String> record1 = new ArrayList<String>();

                        initRecord(record1, table1.attributeList.size());
                        String temp1[] = content.split(":");
                        if (temp1.length < 2) {
                            continue;
                        }
                        int att_id = database.getattribute_id(temp1[0]);
                        if (att_id == 0) {

                            att_id = database.add_attribute(temp1[0]);
                            System.out.println("！！！！！！！！！！！！！！！new add: " + temp1[0] + " " + att_id);

                            curationlog.add("File_attribute;;" + temp1[0] + ";;" + att_id);

                        }


                        // for http: https:
                        String attribute_value = temp1[1];
                        if (temp1.length > 2) {
                            attribute_value = temp1[1] + temp1[2];
                        }
                        //

                        record1.set(table1.getIndex("file_id"), String.valueOf(file_id));
                        record1.set(table1.getIndex("id"), String.valueOf(file_attribute_id));
                        record1.set(table1.getIndex("attribute_id"), String.valueOf(att_id));
                        record1.set(table1.getIndex("value"), attribute_value);  // change for the https before is temp1[1]
                        table1.recordList.add(record1);

                        file_attribute_id++;


                    }
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                System.out.println(dateFormat.format(date)); //2016/11/16
                record.set(table.getIndex("date_stamp"), String.valueOf(dateFormat.format(date)));


            }

            // calculate some attributes. file_type file_size file_extension
            int index_path = table.getIndex("location");
            String file_path = record.get(index_path);
            // get file name from file_path
            String file_name = getFile_name(file_path);
            int index_name = table.getIndex("name");
            record.set(index_name, file_name);
            // get identifier
            String identifier = schema.getTable("dataset").getValue(
                    "id", 0);
            int index_identifier = table.getIndex("dataset_id");
            record.set(index_identifier, identifier);
            String file_extension = getFile_extension(file_name);
            int index_extension = table.getIndex("extension");
            record.set(index_extension, file_extension);
            // calculate file_format
            int index_format = table.getIndex("format_id");
            String file_format = getFile_format(file_extension);
            int value = (Integer) map_file_format.get(file_format);
            record.set(index_format, String.valueOf(value));
            //System.out.println("file_format"+ file_format);
            if (file_format.equalsIgnoreCase("UNKNOWN")) {
                excel2DBLog.writeLine("ValidationTest for file_format: " + file_extension +
                        "Warning: file_format is unknown");
            }

            // calculate file_size
            int index_location = table.getIndex("location");
            String file_location = record.get(index_location);
            System.out.println("llllllllll" + file_location);
            long file_size = 0; //temp use for vpn
            if (file_size < 1) {
                excel2DBLog.writeLine("ValidationTest for file_size: " + file_name +
                        "  Warning: file_size < 1");

            }
            int index_size = table.getIndex("size");
            record.set(index_size, Long.toString(file_size));
            record.set(table.getIndex("download_count"), String.valueOf('0'));
            // add record to table
            table.recordList.add(record);
            file_id = file_id + 1;

        }// for
    }

    public void fillTable_external_link_type() throws IOException, SQLException {
        setInsertOrder("external_link_type");
        Table table = schema.getTable("external_link_type");
        String name = "additional_information";
        String content = getContent(studySheet, name);
        if (content.length() != 0) {
            String[] urlArray = content.split(",");
            int temp = database.getid("external_link_type");
            for (int i = 0; i < urlArray.length; i++) {
                // we need to set the size of record, it's the same with
                external_link_add_id.add(String.valueOf(temp));
                ArrayList<String> record = new ArrayList<String>();
                initRecord(record, table.attributeList.size());
                record.set(table.getIndex("id"), String.valueOf(temp));
                record.set(table.getIndex("name"), "Additional information");
                table.recordList.add(record);
                temp = temp + 1;
            }
        }

        String name1 = "genome_browser";
        String content1 = getContent(studySheet, name1);
        if (content1.length() != 0) {
            String[] urlArray = content1.split(",");
            int temp = database.getid("external_link_type");
            for (int i = 0; i < urlArray.length; i++) {
                // we need to set the size of record, it's the same with
                external_link_gen_id.add(String.valueOf(temp));
                ArrayList<String> record = new ArrayList<String>();
                initRecord(record, table.attributeList.size());
                record.set(table.getIndex("id"), String.valueOf(temp));
                record.set(table.getIndex("name"), "Genome browser");
                table.recordList.add(record);
                temp = temp + 1;
            }
        }


    }

    public void fillTable_external_link() throws IOException, SQLException {
        setInsertOrder("external_link");
        Table table = schema.getTable("external_link");
        String type = "additional_information";
        String content = getContent(studySheet, type);
        String type_id;
        int temp = database.getid("external_link");

        if (content.length() != 0) {

            String[] urlArray = null;
            if (content.contains(",")) {
                urlArray = content.split(",");
            } else {
                urlArray = content.split(";");
            }

            type_id = database.checkcontain1("external_link_type", "name", "Additional information", "id");
            for (int i = 0; i < urlArray.length; i++) {
                // we need to set the size of record, it's the same with
                ArrayList<String> record = new ArrayList<String>();
                // init record
                initRecord(record, table.attributeList.size());
                String flag = database.get_result("select id from external_link where dataset_id= " + dataset_id + " and url= " + "'" + HelpFunctions.trim(urlArray[i]) + "'", "id");
                if (flag == null) {
                    record.set(table.getIndex("id"), String.valueOf(temp));
                    temp = temp + 1;
                } else
                    record.set(table.getIndex("id"), flag);
                record.set(table.getIndex("dataset_id"), String.valueOf(dataset_id));
                record.set(table.getIndex("external_link_type_id"), String.valueOf(type_id));
                record.set(table.getIndex("url"), HelpFunctions
                        .trim(urlArray[i]));
                table.recordList.add(record);

            }
        }
        // add github
        type = "GitHub Repository URL";
        content = getContent(studySheet, type);
        if (content.length() != 0) {
            String[] urlArray = content.split(",");
            //int temp= database.getid("external_link");
            type_id = database.checkcontain1("external_link_type", "name", "Github links", "id");
            for (int i = 0; i < urlArray.length; i++) {
                // we need to set the size of record, it's the same with
                ArrayList<String> record = new ArrayList<String>();
                // init record
                initRecord(record, table.attributeList.size());
                String flag = database.get_result("select id from external_link where dataset_id= " + dataset_id + " and url= " + "'" + HelpFunctions.trim(urlArray[i]) + "'", "id");
                if (flag == null) {
                    record.set(table.getIndex("id"), String.valueOf(temp));
                    temp = temp + 1;
                } else
                    record.set(table.getIndex("id"), flag);
                record.set(table.getIndex("dataset_id"), String.valueOf(dataset_id));
                record.set(table.getIndex("external_link_type_id"), String.valueOf(type_id));
                record.set(table.getIndex("url"), HelpFunctions
                        .trim(urlArray[i]));
                table.recordList.add(record);
            }
        }
    }

    public void fillTable_curationlog() throws SQLException {

        setInsertOrder("curation_log");
        log_id = database.getid("curation_log");
        Table table3 = schema.getTable("curation_log");
        String message = "";
        if (curationlog.isEmpty() == false) {
            for (String temp : curationlog) {
                System.out.println("loglogloglogloglogloglog: " + temp);
                String[] parameters = temp.split(";;");

                message = message + "New " + parameters[0] + " Name:" + parameters[1] + " ID: " + parameters[2] + "/n";
                System.out.println(message);

            }
            String log_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            ArrayList<String> record_t = new ArrayList<String>();
            initRecord(record_t, table3.attributeList.size());
            record_t.set(table3.getIndex("id"), String.valueOf(log_id));
            record_t.set(table3.getIndex("dataset_id"), String.valueOf(dataset_id));
            record_t.set(table3.getIndex("creation_date"), log_date);
            record_t.set(table3.getIndex("created_by"), "System");
            record_t.set(table3.getIndex("action"), "Comment");
            record_t.set(table3.getIndex("comments"), message);
            table3.recordList.add(record_t);
        }

    }

    public void fillTable_sample() throws SQLException {
        setInsertOrder("sample");
        setInsertOrder("dataset_sample");
        setInsertOrder("sample_attribute");
        Table table = schema.getTable("sample");
        Table table1 = schema.getTable("dataset_sample");
        Table table2 = schema.getTable("sample_attribute");
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        int index1 = table.getIndex("id");
        indexList.add(index1);
        String sample_id = null;
        String species = null;
        int species_id = 0;
        String name = null;


        //new part

        int rownumber = 0;
        int temp_sampleid = database.getid("sample");
        int temp_datasetsampleid = database.getid("dataset_sample");
        int temp_attributeid = database.getid("sample_attribute");
        ArrayList<String> items = new ArrayList<String>();

        ArrayList<Integer> attributes_id = new ArrayList<Integer>();
        if (samplesSheet.getLastRowNum() > 0) {//new add if
            for (Row row : samplesSheet) {
                System.out.println(" ");
                System.out.println(" ");
                //System.out.println(row.getRowNum());
                if (row.getRowNum() == 0) {
                    for (Cell cell : row) {

                        int columnIndex = cell.getColumnIndex();
                        if (columnIndex > 2) {
                            String value = cell.getStringCellValue();

                            //System.out.println("one"+ value);
                            int att_id = database.getattribute_id(value.trim());
                            if (att_id == 0) {

                                att_id = database.add_attribute(value.trim());
                                System.out.println("！！！！！！！！！！！！！！！new add: " + value + " " + att_id);
                                String message = "Sample_attribute;;" + value.trim() + ";;" + att_id;
                                curationlog.add(message);

                            }
                            attributes_id.add(att_id);
                        }

                    }
                    System.out.println(attributes_id);


                } else {

                    for (Cell cell : row) {


                        int columnIndex = cell.getColumnIndex();
                        if (columnIndex == 0) {
                            if (cell.getCellType() == 0) {
                                sample_id = String.valueOf(cell.getNumericCellValue());
                                if (sample_id.endsWith(".0")) {
                                    sample_id = sample_id.replace(".0", "");
                                }

                                System.out.println(sample_id);

                            } else {
                                sample_id = cell.getStringCellValue();
                                System.out.println(sample_id);
                            }
                            if (sample_id == null || sample_id == "") {
                                break;
                            }
                        }
                        if (columnIndex == 1) {
                            if (cell.getCellType() == 0) // It's a numeric
                            {
                                species_id = (int) cell.getNumericCellValue();
                            } else if (cell.getCellType() == 2) //It's a formula
                            {
                                System.out.println("cell type: " + cell.getCellType());
                                CellValue cellValue = evaluator.evaluate(cell);
                                String temp_species_id = "";
                                switch (cellValue.getCellType()) {
                                    case Cell.CELL_TYPE_BOOLEAN:
                                        break;
                                    case Cell.CELL_TYPE_NUMERIC:
                                        species_id = (int) cellValue.getNumberValue();
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        temp_species_id = cellValue.getStringValue();
                                        temp_species_id = temp_species_id.replace(" ", "");
                                        species_id = Integer.valueOf(temp_species_id);
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        break;
                                    case Cell.CELL_TYPE_ERROR:
                                        break;
                                    case Cell.CELL_TYPE_FORMULA:
                                        break;
                                }
                                System.out.println(species_id);

                            } else {
                                System.out.println("cell type: " + cell.getCellType());
                                String temp_species_id = cell.getStringCellValue().trim();
                                temp_species_id = temp_species_id.replace(" ", "");
                                System.out.println(temp_species_id);
                                species_id = Integer.valueOf(temp_species_id);
                            }


                            System.out.println(species_id);
                            species_id = database.getspecies_id(species_id);

                            if (species_id == 0) {
                                System.out.println("no species: " + row.getCell(2).getStringCellValue());
                                species_id = database.addspeciestable((int) row.getCell(1).getNumericCellValue(), row.getCell(2).getStringCellValue());
                            }

                            System.out.println(species_id);
                            ArrayList<String> record = new ArrayList<String>();
                            initRecord(record, table.attributeList.size());
                            record.set(table.getIndex("species_id"), String.valueOf(species_id));
                            record.set(table.getIndex("id"), String.valueOf(temp_sampleid));
                            record.set(table.getIndex("name"), sample_id);
                            table.recordList.add(record);

                            map_sample.put(sample_id, temp_sampleid);


                            ArrayList<String> record1 = new ArrayList<String>();
                            initRecord(record1, table1.attributeList.size());
                            record1.set(table1.getIndex("dataset_id"), String.valueOf(dataset_id));
                            record1.set(table1.getIndex("id"), String.valueOf(temp_datasetsampleid));
                            record1.set(table1.getIndex("sample_id"), String.valueOf(temp_sampleid));
                            table1.recordList.add(record1);


                        }
                        if (columnIndex == 2) {
                            name = cell.getStringCellValue();
                            System.out.println(name);
                        }

                        if (columnIndex > 2) {

                            String value = "";
                            if (cell.getCellType() == 0) {
                                value = String.valueOf(cell.getNumericCellValue());
                                if (value.endsWith(".0")) {
                                    value = value.replace(".0", "");
                                }

                                System.out.println(value);

                            } else {
                                value = cell.getStringCellValue().trim();
                                System.out.println(value);
                            }

                            if (value.contains("::")) {

                            } else if (value == "" || value.isEmpty() || value == null) {
                                continue;
                            } else {

                                ArrayList<String> record2 = new ArrayList<String>();
                                initRecord(record2, table2.attributeList.size());
                                record2.set(table2.getIndex("attribute_id"), String.valueOf(attributes_id.get(columnIndex - 3)));
                                record2.set(table2.getIndex("sample_id"), String.valueOf(temp_sampleid));
                                record2.set(table2.getIndex("id"), String.valueOf(temp_attributeid));
                                record2.set(table2.getIndex("value"), String.valueOf(value));
                                table2.recordList.add(record2);
                                temp_attributeid++;


                            }


                        }

                    }

                    temp_sampleid++;
                    temp_datasetsampleid++;
                }
            }
        }//new add if

        //end new part
    }

    //test if a row is a blank one , just test the first cell of the row
    public boolean next(Sheet sheet, int row) {

        Cell cell = HelpFunctions.getCell(sheet, row, 0);
        if (cell == null) {
            System.out.println("row: " + row);
            return false;
        }
        if (cell.getCellType() == 5 || cell.getCellType() == 3)
            return false;
        if (cell.getCellType() == 0) {
            if (HelpFunctions.trim(Double.toString(cell.getNumericCellValue())).length() == 0)
                return false;

            System.out.println("This is numeric type");
        }
        if (cell.getCellType() == 1) {
            if (HelpFunctions.trim(cell.getStringCellValue()).length() == 0)
                return false;
        }
        return true;
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

    public void fillTable_species() throws SQLException {
        setInsertOrder("species");
        Table table = schema.getTable("species");
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        int temp = table.getIndex("id");
        indexList.add(temp);
        // find the position of each cell in the attributeList
        for (int col = 1; col < 3; col++) {
            Cell cell = HelpFunctions.getCell(samplesSheet, 1, col);

            // the release-data is data-stamp
            String attribute = HelpFunctions.trim(cell.getStringCellValue());
            //System.out.println("Attribute:" + attribute);
            if (attribute.equals("species"))
                attribute = "tax_id";
            else if (attribute.equals("species_common_name"))
                attribute = "common_name";
            if (attribute.length() == 0)
                break;
            int index = table.getIndex(attribute);
            indexList.add(index);
        }

        // read records
        // there maybe duplicate records here,so we need to add a judge here
        HashSet<String> previousRecords = new HashSet<String>();
        temp = database.getid("species");
        for (int row = 2; row < samplesSheet.getLastRowNum() + 1; row++) {
            if (!next(samplesSheet, row))
                break;
            ArrayList<String> record = new ArrayList<String>();
            //init the size of the record
            initRecord(record, table.attributeList.size());
            boolean valid = true;
            for (int col = 1; col < 3; col++) {
                Cell cell = HelpFunctions.getCell(samplesSheet, row, col);

                String content = HelpFunctions.trim(HelpFunctions.getCellString(cell));
                if (content.length() == 0) {
                    valid = false;
                    break;
                }
                if (col == 1 && previousRecords.contains(content)) {
                    valid = false;
                    break;
                } else {

                    previousRecords.add(content);
                }
                try {
                    if (col == 1) {

                        System.out.println("all species id " + temp);
                        String flag = database.checkcontain("species", "tax_id", content, "id");
                        if (flag == null) {
                            System.out.println("temp in species:  " + temp);
                            System.out.println("Index in species:  " + table.getIndex("id"));
                            System.out.println("Index in species:  " + table.getIndex("tax_id"));
                            record.set(table.getIndex("id"), String.valueOf(temp));
                            record.set(table.getIndex("tax_id"), content);
                            record.set(table.getIndex("genbank_name"), "");
                            record.set(table.getIndex("common_name"), "");
                            map_species.put(content, temp);
                            temp = temp + 1;
                        } else {
                            System.out.println("flag  " + flag);
                            int log = Integer.valueOf(flag);
                            map_species.put(content, log);
                            valid = false;
                            break;
                        }
                    }
                    if (col == 2) {
                        // here is a trick -1
                        record.set(table.getIndex("scientific_name"), content);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
            //
            if (valid) {
                table.recordList.add(record);
                System.out.println("all record" + record);
            }
        }

        for (Object o : map_species.keySet()) {

            String key = o.toString();
            int value = (Integer) map_species.get(o);
            System.out.println("value " + value);
            System.out.println("this is map key " + key);
            System.out.println("this is map value " + value);

        }
    }

    public void fillTable_image() throws IOException, SQLException {
        setInsertOrder("image");
        Table table = schema.getTable("image");
        int size = table.attributeList.size();
        ArrayList<String> record = new ArrayList<String>();
        String key = "_" + getIdentifier();

        for (int i = 0; i < size; i++) {
            // image_location from map
            String attribute = table.attributeList.get(i);
            if (attribute.equals("id")) {
                image_id = database.getid("image");
                System.out.println("id number " + image_id);


            }
            if (attribute.equals("location")) {
                attribute = "image_location";
            }
            if (attribute.equals("tag")) {
                attribute = "image_tag";
            }
            if (attribute.equals("url")) {
                attribute = "image_url";
            }
            if (attribute.equals("license")) {
                attribute = "image_license";
            }
            if (attribute.equals("photographer")) {
                attribute = "image_photographer";
            }
            if (attribute.equals("source")) {
                attribute = "image_source";
            }
            Cell cell = HelpFunctions.findCell(studySheet, attribute);
            String content = getContent(studySheet, attribute);
            // we can't find the attribute.
            if (cell == null) {
                if (attribute.equals("id")) {
                    content = String.valueOf(image_id);
                    record.add(content);
                    continue;
                } else {
                    record.add(null);
                    continue;
                }
            }

            //add part start
            if (attribute.equals("image_url")) {
                if (content.contains("http") == false) {
                    content = "http://192.168.174.43/images/data/cropped/" + content;
                    record.add(content);
                    continue;
                }
            }

            //end
            if (content == null || content.equals("")) {

                Excel2Database.excel2DBLog.writeLine("Warning:  " + attribute + "not exist");
                record.add("");

            } else
                record.add(content);
        }

        table.recordList.add(record);
    }

    public void fillTable_dataset_project() throws SQLException {
        setInsertOrder("dataset_project");
        Table table = schema.getTable("dataset_project");
        int size = projectid.size();
        int temp = database.getid("dataset_project");
        for (int i = 0; i < size; i++) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, 3);
            int index_id = table.getIndex("id");
            int index_dataset_id = table.getIndex("dataset_id");
            int index_project_id = table.getIndex("project_id");

            String flag = database.get_result("select id from dataset_project where dataset_id= " + dataset_id + " and project_id= " + projectid.get(i), "id");
            if (flag == null) {
                record.set(index_id, String.valueOf(temp));
                temp = temp + 1;
            } else
                record.set(index_id, flag);
            record.set(index_dataset_id, String.valueOf(dataset_id));
            record.set(index_project_id, projectid.get(i));


            table.recordList.add(record);
        }

    }

    public void fillTable_project() throws SQLException {
        setInsertOrder("project");
        Projects projects = null;
        try {
            projects = new Projects();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            e.printStackTrace(excel2DBLog.printWriter);
        }
        Table table = schema.getTable("project");
        int size = table.attributeList.size();
        String content = getContent(studySheet, "project_URL");
        if (content.equals(""))
            return;
        String[] project_urls = getProjectUrls(content);
        int temp = database.getid("project");
        for (int i = 0; i < project_urls.length; i++) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, size);
            String url = project_urls[i];
            System.out.println("fillTable_project url: " + url);
            HashMap<String, String> details = projects.getDetails(url);
            int index_id = table.getIndex("id");
            int index_location = table.getIndex("image_location");
            int index_name = table.getIndex("name");
            int index_url = table.getIndex("url");

            String flag = database.checkcontain1("project", "url", details.get("project_url"), "id");
            if (flag == null) {
                record.set(index_id, String.valueOf(temp));
                record.set(index_location, details.get("project_image_location"));
                record.set(index_name, details.get("project_name"));
                record.set(index_url, details.get("project_url"));
                projectid.add(String.valueOf(temp));
                temp = temp + 1;
                table.recordList.add(record);
            } else
                projectid.add(flag);

        }
    }

    String getIdentifier() {
        return getContent(studySheet, "identifier");
    }

    static String[] getProjectUrls(String content) {
        ArrayList<String> list = new ArrayList<String>();
        int count = 0;
        int beginIndex = 0;
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '"')
                count++;
            if (content.charAt(i) == ',') {
                if (count > 0 && count % 2 == 0) {
                    String url = content.substring(beginIndex, i);
                    list.add(HelpFunctions.trim(url));
                    beginIndex = i + 1;
                }
            }
        }
        // don't forget the last one
        String url = content.substring(beginIndex);
        list.add(url);
        // System.out.println(list.toString());
        return list.toArray(new String[list.size()]);
    }

    public void fillTable_gigadb_user() throws SQLException {
        setInsertOrder("gigadb_user");
        Table table = schema.getTable("gigadb_user");
        int size = table.attributeList.size();
        Boolean valid = false;
        ArrayList<String> record = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            String attribute = table.attributeList.get(i);
            if (attribute.equals("first_name"))
                attribute = "submitter_first_name";
            if (attribute.equals("last_name"))
                attribute = "submitter_last_name";
            if (attribute.equals("password"))
                attribute = "submitter_password";
            if (attribute.equals("affiliation"))
                attribute = "submitter_affiliation";
            if (attribute.equals("email"))
                attribute = "submitter_email";
            if (attribute.equals("username"))
                attribute = "submitter_email";


            String content = getContent(studySheet, attribute);
            if (attribute.equals("id")) {
                user_id = database.getid("gigadb_user");
                System.out.println("id number " + user_id);
                content = String.valueOf(user_id);

            }
            if (attribute.equals("is_activated")) {
                content = "FALSE";

            }
            if (attribute.equals("newsletter")) {
                content = "FALSE";

            }
            if (attribute.equals("previous_newsletter_state")) {
                content = "FALSE";

            }
            if (attribute.equals("preferred_link")) {
                content = "EBI";

            }


            // because in submitter there is not null attributes.
			/*
			if (content == null && (attribute.equals("password")||attribute.equals("role")))
			{
				record.add("");
			}
			else
				record.add(content);*/

            if (attribute.equals("facebook_id") || attribute.equals("twitter_id") || attribute.equals("linkedin_id") || attribute.equals("google_id") || attribute.equals("orcid_id")) {
                record.add(null);
            } else if (content == null) {
                record.add("");
            } else
                record.add(content);

            if (attribute.equals("submitter_last_name")) {
                submitter_name = content;
            }
            if (attribute.equals("submitter_email")) {
                email = content;
                String flag = database.checkcontain1("gigadb_user", "email", content, "id");
                System.out.println("gigadb_user: " + flag);
                if (flag == null)
                    valid = true;
                else
                    user_id = Integer.parseInt(flag);
            }

        }
        // we should add record first to avoid null pointer error
        if (valid)
            table.recordList.add(record);

    }


    public void fillTable_manuscript() throws HttpException, IOException, SQLException {
        setInsertOrder("manuscript");
        Table table = schema.getTable("manuscript");
        int size = table.attributeList.size();
        String content = getContent(studySheet, "related_manuscript");
        if (content == "")
            return;
        String[] valueList = content.split(",");
        int temp = database.getid("manuscript");
        for (int i = 0; i < valueList.length; i++) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, size);
            String value = HelpFunctions.trim(valueList[i]);
            String doi = null;
            String pmid = null;

            // a doi should contains /
            if (value.contains("/")) {
                // get pmid
                doi = value;
            } else {
                pmid = value;
            }
            // fill
            int index_doi = table.getIndex("identifier");
            int index_pmid = table.getIndex("pmid");
            int index_id = table.getIndex("id");
            int index_dataset_id = table.getIndex("dataset_id");
            record.set(index_doi, doi);
            record.set(index_pmid, pmid);
            String flag = database.get_result("select id from manuscript where dataset_id= " + dataset_id + " and identifier= " + "'" + doi + "'", "id");
            if (flag == null) {
                record.set(index_id, String.valueOf(temp));
                temp = temp + 1;
            } else
                record.set(index_id, flag);

            record.set(index_dataset_id, String.valueOf(dataset_id));
            table.recordList.add(record);
        }
        // test
        // table.print();
    }

    public void fillTable_publisher() throws IOException, SQLException {
        setInsertOrder("publisher");
        Table table = schema.getTable("publisher");

        ArrayList<String> record = new ArrayList<String>();
        initRecord(record, 3);
        String value = getContent(studySheet, "publisher");

        String flag = database.checkcontain1("publisher", "name", value, "id");
        System.out.println("flag  " + flag);
        if (flag == null) {
            publisher_id = database.getid("publisher");
            int index_id = table.getIndex("id");
            int index_name = table.getIndex("name");
            int index_description = table.getIndex("description");

            record.set(index_id, String.valueOf(publisher_id));
            record.set(index_name, value);
            record.set(index_description, "");
            table.recordList.add(record);
        } else
            publisher_id = Integer.valueOf(flag);
    }


    @SuppressWarnings("null")
    public void fillattribute_keyword() throws SQLException {
        setInsertOrder("dataset_attributes");
        Table table = schema.getTable("dataset_attributes");

        int size = table.attributeList.size();
        String keyword = getContent(studySheet, "keywords");


        if (keyword != null || !keyword.equals("")) {


            int index_id = table.getIndex("id");
            int index_dataset = table.getIndex("dataset_id");
            int index_attribute = table.getIndex("attribute_id");
            int value = table.getIndex("value");

            dataset_attribute_id = database.getid("dataset_attributes");
            String[] list = keyword.split(";");
            String attribute_id = database.checkcontain1("attribute", "structured_comment_name", "keywords", "id");
            for (int i = 0; i < list.length; ++i) {
                ArrayList<String> record = new ArrayList<String>();
                initRecord(record, size);
                String key = list[i].trim();


                record.set(index_id, String.valueOf(dataset_attribute_id));
                record.set(index_dataset, String.valueOf(dataset_id));
                record.set(index_attribute, String.valueOf(attribute_id));
                record.set(value, key);
                table.recordList.add(record);
                dataset_attribute_id = dataset_attribute_id + 1;


            }
        }


    }

    @SuppressWarnings("null")
    public void fillTable_dataset() throws IOException, SQLException, NoSuchAlgorithmException {
        setInsertOrder("dataset");
        Table table = schema.getTable("dataset");
        int size = table.attributeList.size();
        ArrayList<String> record = new ArrayList<String>();

        String key = "_" + getIdentifier();

        for (int i = 0; i < size; i++) {
            String attribute = table.attributeList.get(i);
            String content = getContent(studySheet, attribute);
            if (attribute.equals("id")) {
                String value = getContent(studySheet, "identifier");
                String flag = database.checkcontain1("dataset", "identifier", value, "id");
                if (flag == null)
                    dataset_id = database.getid("dataset");
                else
                    dataset_id = Integer.parseInt(flag);

                content = String.valueOf(dataset_id);
                // dataset_attributes


            }
            if (attribute.equals("submitter_id")) {
                content = String.valueOf(user_id);
            }

            if (attribute.equals("image_location")) {
                record.add(locationMap.get(key));
                if (locationMap.get(key) == null) {
                    String image_name = getContent(studySheet, attribute);
                    Excel2Database.excel2DBLog.writeLine(
                            "Warning: " + image_name + " image file does not exist");
                }
                continue;
            }
            if (attribute.equals("image_id")) {
                content = String.valueOf(image_id);
            }

            if (attribute.equals("excelfile")) {

                String fileName = file.getName();
                content = fileName.substring(0, fileName.length() > 50 ? 49 : fileName.length() - 1);
            }

            if (attribute.equals("excelfile_md5")) {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                        file));
                MessageDigest md = null;
                md = MessageDigest.getInstance("MD5");
                md.reset();
                int len = 0;
                byte[] buffer = new byte[8192];
                while ((len = bis.read(buffer)) > -1) {
                    md.update(buffer, 0, len);
                }
                content = format(md.digest());
            }


            //from here
            if (attribute.equals("identifier")) {
                email_identifier = content;
                //database.assigndoitest();
                if (content == null || content.equals("")) {
                    //String query = "select max(identifier) from dataset;";
                    content = database.assigndoi();//How to automatic assign new doi using last doi in database +1
                    //.add(content);
                }
            }

            if (attribute.equals("publisher_id")) {
                content = String.valueOf(publisher_id);
            }

            if (attribute.equals("ftp_site")) {
                if (content == null || content.equals("")) {
                    content = "ftp://climb.genomics.cn/pub/10.5524/100001_101000/doi";
                    //record.add(content);
                } else if (content.endsWith("/") == false) {
                    content = content + "/";
                }
            }

            if (attribute.equals("upload_status")) {
                if (content.equalsIgnoreCase("Published")) {
                    if (database.checkemail(email_identifier))
                        send_email = true;
                }
            }

            if (attribute.equals("publication_date")) {
                publication_year = content;
                content = null;
            }
            if (attribute.equals("title"))
                title = content;

            if (attribute.equals("dataset_size")) {

                //System.out.println(content);
                content = content.replaceAll(" ", "");
                Pattern p = Pattern.compile("(\\d+)(\\w+)");
                //Pattern   p   =   Pattern.compile("(\\d+\\.?\\d+?)(\\w+)");
                Matcher m = p.matcher(content);
                String KB = new String("KB");
                String MB = new String("MB");
                String GB = new String("GB");
                String TB = new String("TB");
                double dataset_size = 0;
                if (m.find()) {
                    String num_letter = m.group(1);
                    String union = m.group(2);

                    if (KB.equalsIgnoreCase(union)) {
                        dataset_size = Double.parseDouble(num_letter);
                        dataset_size *= (1024);
                        System.out.println("dataset_size: " + dataset_size);


                    }

                    if (MB.equalsIgnoreCase(union)) {
                        dataset_size = Double.parseDouble(num_letter);
                        dataset_size *= (1024 * 1024);
                        System.out.println("dataset_size: " + dataset_size);

                    }

                    if (GB.equalsIgnoreCase(union)) {
                        dataset_size = Double.parseDouble(num_letter);
                        dataset_size *= (1024 * 1024 * 1024);
                        System.out.println("dataset_size: " + dataset_size);


                    }
                    if (TB.equalsIgnoreCase(union)) {
                        dataset_size = Double.parseDouble(num_letter);
                        dataset_size *= (1024 * 1024 * 1024 * 1024);
                        System.out.println("dataset_size: " + dataset_size);


                    }
                }
                Pattern pattern = Pattern.compile("\\d+(.\\d+)?$");
                Matcher m1 = pattern.matcher(content);
                if (m1.find()) {
                    dataset_size = Integer.valueOf(content);
                    dataset_size *= (1024 * 1024 * 1024);
                    System.out.println("dataset_size: " + dataset_size);
                }

                dataset_size = (long) dataset_size;
                long long_size = new Double(dataset_size).longValue();

                content = String.valueOf(long_size);
            }
            record.add(content);
        }

        table.recordList.add(record);
    }

    public void fillTable_author() throws SQLException {
        setInsertOrder("author");
        Table table = schema.getTable("author");
        int size = table.attributeList.size();
        // add author names
        String[] nameList = getContent(studySheet, "author_list").replace(" [", "[").replace("[ ", "[").replace(" ]", "]").replace("] ", "]").split(
                ";| +and +");
        int temp = database.getid("author");
        for (int i = 0; i < nameList.length; i++) {

            System.out.println("author info no " + (i + 1) + "/" + nameList.length);
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, size);
            String author_name = HelpFunctions.trim(nameList[i]);


            int start_index = author_name.indexOf(" ");
            String first_name = author_name.substring(0, start_index);
            int middle_index = author_name.indexOf(" ", start_index + 1);
            int orcid_index = author_name.indexOf("[");
            System.out.println(middle_index);
            String middle_name = "";
            String surname = "";
            String orcid = "";
            if (middle_index != -1) {
                middle_name = author_name.substring(start_index + 1, middle_index);

                if (orcid_index != -1) {
                    surname = author_name.substring(middle_index + 1, orcid_index);
                    int temp_index = author_name.indexOf("]");
                    orcid = author_name.substring(orcid_index + 1, temp_index);

                } else {
                    surname = author_name.substring(middle_index + 1);
                }
                System.out.println(first_name + " " + middle_name + " " + surname);
            }
            if (middle_index == -1) {

                if (orcid_index != -1) {
                    surname = author_name.substring(start_index + 1, orcid_index);
                    int temp_index = author_name.indexOf("]");
                    orcid = author_name.substring(orcid_index + 1, temp_index);
                    middle_name = "";//middle name content null value
                } else {

                    surname = author_name.substring(start_index + 1);
                }

            }
            int rank = i + 1;
            String flag = null;
            if (orcid_index != -1) {
                flag = database.get_result("select id from author where orcid= " + "'" + orcid + "'", "id");
            }

            if (flag == null) {
                authorid.add(String.valueOf(temp));

                int index_id = table.getIndex("id");
                int index_surname = table.getIndex("surname");
                int index_first_name = table.getIndex("first_name");
                int index_middle_name = table.getIndex("middle_name");
                int index_orcid = table.getIndex("orcid");


                record.set(index_id, String.valueOf(temp));
                record.set(index_surname, surname);
                record.set(index_first_name, first_name);
                if (middle_index == -1) {
                    record.set(index_middle_name, "");
                } else
                    record.set(index_middle_name, middle_name);
                if (orcid_index == -1) {
                    record.set(index_orcid, "");
                } else
                    record.set(index_orcid, orcid);
                temp = temp + 1;
                table.recordList.add(record);
            } else
                authorid.add(flag);

        }
        // test
        // table.print();
    }

    public void fillTable_dataset_author() throws SQLException {

        setInsertOrder("dataset_author");
        Table table = schema.getTable("dataset_author");
        int size = authorid.size();
        int temp = database.getid("dataset_author");
        for (int i = 0; i < size; i++) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, 4);
            int index_id = table.getIndex("id");
            int index_dataset_id = table.getIndex("dataset_id");
            int index_author_id = table.getIndex("author_id");
            int index_rank = table.getIndex("rank");

            record.set(index_id, String.valueOf(temp));
            temp = temp + 1;

            record.set(index_dataset_id, String.valueOf(dataset_id));
            record.set(index_author_id, authorid.get(i));
            int rank = i + 1;
            record.set(index_rank, String.valueOf(rank));


            table.recordList.add(record);

        }
    }

    public void fillTable_dataset_sample() throws SQLException {

        setInsertOrder("dataset_sample");
        Table table = schema.getTable("dataset_sample");
        //int size = map_sample.size();
        int temp = database.getid("dataset_sample");
        for (Object o : map_sample.values()) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, 3);

            int index_id = table.getIndex("id");
            int index_dataset_id = table.getIndex("dataset_id");
            int index_sample_id = table.getIndex("sample_id");
            String sample_id = String.valueOf((Integer) o);
            String flag = database.get_result("select id from dataset_sample where dataset_id= " + dataset_id + " and sample_id= " + sample_id, "id");
            if (flag == null) {
                record.set(index_id, String.valueOf(temp));
                temp = temp + 1;
            } else
                record.set(index_id, flag);

            record.set(index_dataset_id, String.valueOf(dataset_id));
            record.set(index_sample_id, sample_id);


            System.out.println("sample_map: " + sample_id);

            table.recordList.add(record);
        }
    }

    public void fillTable_sample_attribute() throws SQLException, IOException {

        setInsertOrder("sample_attribute");
        Table table = schema.getTable("sample_attribute");
        //int size = map_sample.size();
        int temp = database.getid("sample_attribute");
        for (String key : map_sample_attribute.keySet()) {
            int index_id = table.getIndex("id");
            int index_attribute_id = table.getIndex("attribute_id");
            int index_sample_id = table.getIndex("sample_id");
            int index_unit_id = table.getIndex("unit_id");
            int index_value = table.getIndex("value");

            String total = map_sample_attribute.get(key);
            if (total.equals(null) || total == null || total.equals(",,,,") || total == ",,,," || total.endsWith(",,,,")) {
                continue;
            }
            int sample_id = map_sample.get(key);
            System.out.println("key: " + key);
            System.out.println("the sample_id: " + sample_id);
            System.out.println("the attributes: " + total);


            String[] aa = total.split(";;");
            for (int i = 0; i < aa.length; ++i) {
                ArrayList<String> record = new ArrayList<String>();
                initRecord(record, 5);
                String[] bb = aa[i].split(",,");
                String attribute_key = bb[0];
                String attribute_value = bb[1];
                String attribute_unit = null;
                if (bb.length > 2)
                    attribute_unit = bb[2];

                String flag = database.checkcontain1("attribute", "attribute_name", attribute_key, "id");
                if (flag == null) {
                    String flag2 = database.checkcontain1("attribute", "structured_comment_name", attribute_key, "id");
                    if (flag2 == null) {
                        excel2DBLog.writeLine("can't find in attribute table " + attribute_key);
                        record.set(index_attribute_id, String.valueOf(-1));
                    } else
                        record.set(index_attribute_id, flag2);

                } else {
                    record.set(index_attribute_id, flag);
                }
                if (attribute_value != null) {
                    record.set(index_value, attribute_value);
                }

                if (attribute_unit != null) {
                    record.set(index_unit_id, attribute_unit);
                }

                record.set(index_sample_id, String.valueOf(sample_id));
                record.set(index_id, String.valueOf(temp));
                temp++;
                table.recordList.add(record);
            }
        }
    }

    //add part  dataset_relation

    public void fillTable_relation() throws SQLException {

        setInsertOrder("relation");
        Table table = schema.getTable("relation");
        int size = table.attributeList.size();
        String relateddoi = getContent(studySheet, "related_doi");
        if (relateddoi == null || relateddoi.equals(""))
            return;
        String doirelationship = getContent(studySheet, "doi_relationship");
        ArrayList<String> record = new ArrayList<String>();
        initRecord(record, size);
        relation_id = database.getid("relation");
        int index_id = table.getIndex("id");
        int index_identifier = table.getIndex("dataset_id");
        int index_related_DOI = table.getIndex("related_doi");
        int index_DOI_relationship = table.getIndex("relationship");


        String flag = database.get_result("select id from relation where dataset_id= " + dataset_id + " and related_doi= " + "'" + relateddoi + "'" + " and relationship= " + "'" + doirelationship + "'", "id");
        if (flag == null)
            record.set(index_id, String.valueOf(relation_id));
        else
            record.set(index_id, flag);
        record.set(index_identifier, String.valueOf(dataset_id));
        record.set(index_related_DOI, relateddoi);
        record.set(index_DOI_relationship, doirelationship);
        table.recordList.add(record);

    }

    public void fillTable_dataset_funder() throws SQLException {

        setInsertOrder("dataset_funder");
        Table table = schema.getTable("dataset_funder");
        int size = table.attributeList.size();
        String fundinginfo = getContent(studySheet, "funding_information");
        if (fundinginfo == null || fundinginfo.equals(""))
            return;
        String[] bb = fundinginfo.split(";");
        int dataset_funder_id = database.getid("dataset_funder");
        for (int i = 0; i < bb.length; ++i) {
            System.out.println("funding info no " + (i + 1) + "/" + bb.length);

            String[] aa = bb[i].split(",");
            String fundername = aa[0].trim().toLowerCase().replace("'", "''");
            String fundingid = database.get_result("select id from funder_name where lower(primary_name_display)= " + "'" + fundername + "';", "id");
            if (fundingid == null) {
                fundingid = String.valueOf(database.add_fundername(aa[0].trim()));
            }
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, size);
            int index_id = table.getIndex("id");
            int index_identifier = table.getIndex("dataset_id");
            int index_funder_id = table.getIndex("funder_id");
            int index_grant_award = table.getIndex("grant_award");
            int index_comments = table.getIndex("comments"); //programm name
            int index_awardee = table.getIndex("awardee");  // awardee


            record.set(index_id, String.valueOf(dataset_funder_id));
            record.set(index_identifier, String.valueOf(dataset_id));
            dataset_funder_id += 1;
            record.set(index_funder_id, fundingid);
            if (aa.length > 2) {
                if (aa[2] != null) {
                    System.out.println("grant: " + aa[2]);
                    record.set(index_grant_award, aa[2].trim());
                }
                if (aa[1] != null) {
                    System.out.println("programm: " + aa[1]);
                    record.set(index_comments, aa[1].trim());
                }
                if (aa[3] != null) {
                    System.out.println("PI: " + aa[3]);
                    record.set(index_awardee, aa[3].trim());
                }
            }
            table.recordList.add(record);
        }

    }

    public void fillTable_type() throws SQLException {

        setInsertOrder("type");
        Table table = schema.getTable("type");
        ArrayList<String> record = new ArrayList<String>();
        initRecord(record, 3);


        String value = getContent(studySheet, "dataset_type");
        String[] attributeList = null;
        int size = 0;
        if (value.length() != 0) {
            if (value.contains(",")) {
                attributeList = value.split(",");
            } else {
                attributeList = value.split(";");
            }
            size = attributeList.length;
        }
        int type_id = 0;
        type_id = database.getid("type");
        for (int i = 0; i < size; i++) {
            String flag = database.checkcontain1("type", "name", attributeList[i].replaceAll(" ", ""), "id");

            if (flag == null) {

                typeid.add(String.valueOf(type_id));
                int index_id = table.getIndex("id");
                int index_name = table.getIndex("name");
                int index_description = table.getIndex("description");

                record.set(index_id, String.valueOf(type_id));
                System.out.println("type_id: " + String.valueOf(type_id));
                record.set(index_name, attributeList[i].replaceAll(" ", ""));
                System.out.println("dataset_type: " + attributeList[i].replaceAll(" ", ""));
                record.set(index_description, "");
                type_id = type_id + 1;
                table.recordList.add(record);
            } else
                typeid.add(flag);
        }
    }

    public void fillTable_dataset_type() throws SQLException {

        setInsertOrder("dataset_type");
        Table table = schema.getTable("dataset_type");

        int temp = database.getid("dataset_type");

        int size = 0;
        size = typeid.size();
        for (int i = 0; i < size; i++) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, 3);
            int index_id = table.getIndex("id");
            int index_dataset_id = table.getIndex("dataset_id");
            int index_type_id = table.getIndex("type_id");
            String flag = database.get_result("select id from dataset_type where dataset_id= " + dataset_id + " and type_id= " + typeid.get(i), "id");
            if (flag == null) {
                System.out.println("pk temp:.................. " + temp);
                record.set(index_id, String.valueOf(temp));
                temp = temp + 1;
            } else {
                record.set(index_id, flag);
            }

            record.set(index_dataset_id, String.valueOf(dataset_id));
            record.set(index_type_id, String.valueOf(typeid.get(i)));
            table.recordList.add(record);

        }


    }


    public void fillTable_link() throws HttpException, IOException, SQLException {
        setInsertOrder("link");
        Table table = schema.getTable("link");
        String content = "";
        int size_number = 0;

        content = getContent(studySheet, "ext_acc_mirror");

        String[] attributeList = null;
        if (content.length() != 0) {
            if (content.contains(",")) {
                attributeList = content.split(",");
                size_number = attributeList.length;
            } else {
                attributeList = content.split(";");
                size_number = attributeList.length;
            }
        }
        int index_link = table.getIndex("link");
        int index_id = table.getIndex("id");
        int index_dataset_id = table.getIndex("dataset_id");
        int index_is_primary = table.getIndex("is_primary");
        int temp = database.getid("link");

        for (int i = 0; i < size_number; i++) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, 4);
            String value = HelpFunctions.trim(attributeList[i]);
            value = value.replace(" ", "");
            record.set(index_link, value);
            record.set(index_dataset_id, String.valueOf(dataset_id));
            String flag = database.get_result("select id from link where dataset_id= " + dataset_id + " and link= " + "'" + value + "'", "id");
            if (flag == null) {
                record.set(index_id, String.valueOf(temp));
                temp = temp + 1;
            } else
                record.set(index_id, flag);
            record.set(index_is_primary, "TRUE");
            table.recordList.add(record);

        }

        String content1 = getContent(studySheet, "ext_acc_link");
        String[] attributeList1 = null;
        int size_number1 = 0;
        if (content1.length() != 0) {
            if (content1.contains(",")) {
                attributeList1 = content1.split(",");
                size_number1 = attributeList1.length;
            } else {
                attributeList1 = content1.split(";");
                size_number1 = attributeList1.length;
            }
        }


        for (int i = 0; i < size_number1; i++) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, 4);
            String value = HelpFunctions.trim(attributeList1[i]);
            value = value.replace(" ", "");
            record.set(index_link, value);
            record.set(index_dataset_id, String.valueOf(dataset_id));
            String flag = database.get_result("select id from link where dataset_id= " + dataset_id + " and link= " + "'" + value + "'", "id");
            if (flag == null) {
                record.set(index_id, String.valueOf(temp));
                temp = temp + 1;
            } else
                record.set(index_id, flag);
            record.set(index_is_primary, "FALSE");
            table.recordList.add(record);

        }


    }


    void fillTable_multiAttribute(String attribute) throws IOException {

        String tableName = null;
        if (attribute.equals("dataset_type"))
            tableName = "datasettype";
        else
            tableName = "dataset" + attribute;
        setInsertOrder(tableName);
        Table table = schema.getTable(tableName);
        String identifier = schema.getTable("dataset")
                .getValue("identifier", 0);
        // attribute=attribute.toLowerCase();
        String content = getContent(studySheet, attribute);
        String[] attributeList = null;
        // size the number of records
        int size = 0;
        // blank
        if (content.length() != 0) {
            attributeList = content.split(",");
            size = attributeList.length;
        }
        int index_attribute = table.getIndex(attribute);
        int index_identifer = table.getIndex("identifier");
        for (int i = 0; i < size; i++) {
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, 2);
            String value = HelpFunctions.trim(attributeList[i]);
            record.set(index_attribute, value);
            record.set(index_identifer, identifier);
            table.recordList.add(record);
        }
        // test
        // table.print();
    }

    // relation
    public void fillTable_dataset_(String tableName2) {
        String tableName = "dataset_" + tableName2;
        setInsertOrder(tableName);
        Table table = schema.getTable(tableName);
        Table table2 = schema.getTable(tableName2);
        ArrayList<String> attributeList = table.attributeList;
        String attribute2 = null;
        for (String attribute : attributeList) {
            if (!attribute.equals("identifier")) {
                attribute2 = attribute;
                break;
            }
        }
        String identifier = schema.getTable("dataset")
                .getValue("identifier", 0);
        for (int i = 0; i < table2.recordList.size(); i++) {
            String value = table2.getValue(attribute2, i);
            ArrayList<String> record = new ArrayList<String>();
            initRecord(record, 2);
            int index_attribute = table.getIndex(attribute2);
            int index_identifer = table.getIndex("identifier");
            record.set(index_attribute, value);
            record.set(index_identifer, identifier);
            table.recordList.add(record);
        }

    }

    public String getContent(Sheet sheet, String attribute) {
        Cell cell = HelpFunctions.findCell(sheet, attribute);
        // we can't find the attribute.
        if (cell == null) {
            return null;
        }
        int row = cell.getRowIndex();
        int col = cell.getColumnIndex();
        // calcualte the position of the value
        col += 2;
        Cell value = HelpFunctions.getCell(sheet, row, col);
        String content = null;
        // value.
        if (attribute.equals("title") || attribute.equals("description")) {
            RichTextString richTextString = value.getRichStringCellValue();
            if (richTextString instanceof HSSFRichTextString) {
                content = HelpFunctions.replaceItalics((HSSFRichTextString) richTextString, workbook);
            } else {
                content = HelpFunctions.replaceItalics((XSSFRichTextString) richTextString);
            }
            System.out.println("italics found: " + content);
        } else {
            if (HelpFunctions.getCellString(value) != null)
                content = HelpFunctions.getCellString(value);
        }
        content = content.replaceAll("\\u00a0", " ");
        content = content.replaceAll("\\ufffd", " ");
        return content;
    }

    // 𩄍瑕𩄍extension
    public String getFile_extension(String file_name) throws IOException {
        // file_name="asdfasd.txt";
        String[] extensionArray = file_name.split("\\.");
        String extension = "";
        int length = extensionArray.length;
        if (length == 1) {
            excel2DBLog.writeLine("Getting file_extension, file_name: "
                    + file_name + " Warning there is not '.' in it!");
            return "unknown";
        }
        //the first one shouldn't be extension
        for (int i = 1; i < length; i++) {
            String temp = HelpFunctions.trim(extensionArray[i]);
            // all extension are lower case in map, so when camparing,
            // I need to change temp to lowercase
            //if readme then the extension before it is removed
            if (temp.equals("readme")) {
                extension = "";
                continue;
            }
            if (extension2Type.map.keySet().contains(temp.toLowerCase())) {
                if (extension != "" && temp.equals("txt"))
                    continue;
                extension = temp;
            }
        }
        if (extension == "") {
            int index = length - 1;
            while (compressExtension.contains(extensionArray[index]
                    .toLowerCase()))
                index--;
            extension = extensionArray[index];
        }
        return extension;
    }

    public static String getFile_name(String file_path) {
        int beginIndex = file_path.lastIndexOf("/") + 1;
        return file_path.substring(beginIndex);
    }

    public String getFile_format(String file_extension) {
        //to lower case
        if (extension2Type.map.containsKey(file_extension.toLowerCase()))
            return extension2Type.map.get(file_extension.toLowerCase());
        else
            return "UNKNOWN";
    }

    // size the number of attributes in a record
    public void initRecord(ArrayList<String> record, int size) {
        for (int i = 0; i < size; i++)
            record.add(null);
    }


    public static long getFile_size(String file_location) throws IOException {

        String ftp_site = "parrot.genomics.cn";//ftp
        //String ftp_site = "192.168.174.43";//vpn
        // String path = ftp_site + file_location;
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftp_site);
        long fileSize = -1;
        ftpClient.login("", "");
        int beginIndex = 0;
        if (file_location.indexOf(ftp_site) != -1)
            beginIndex = file_location.indexOf(ftp_site) + ftp_site.length();
        String location = file_location.substring(beginIndex);
        String request = "SIZE " + location + "\r\n";
        ftpClient.sendCommand(request);
        try {
            String temp = ftpClient.getReplyString();
            int status = ftpClient.getReplyCode();
            if (status == 213) {
                String msg = ftpClient.getReplyString();
                fileSize = Long.parseLong(msg.substring(3).trim());
            } else {
                excel2DBLog.writeLine("We can't get the file, please check its path: ");
                excel2DBLog.writeLine(file_location);
            }

        } catch (IOException e) {
            e.printStackTrace();
            excel2DBLog.writeLine(e.toString());
        }
        ftpClient.disconnect();
        ;
        return fileSize;
    }


    public boolean fillTable() throws IOException {
        boolean result = true;
        try {
            fillTable_gigadb_user();
            fillTable_image();
            fillTable_publisher();
            fillTable_dataset();
            fillTable_dataset_funder();
            fillattribute_keyword();
            fillTable_author();
            fillTable_dataset_author();
            fillTable_relation();
            fillTable_link();
            fillTable_type();
            fillTable_dataset_type();
            //fillTable_external_link_type();
            fillTable_external_link();

            //fillTable_multiAttribute("ext_acc_mirror");
            //fillTable_multiAttribute("ext_acc_link");
            //fillTable_multiAttribute("dataset_type");
            fillTable_manuscript();

            //fillTable_dataset_("manuscript");
            //fillTable_species();
            //fillTable_dataset_("species");
            fillTable_sample();
            //fillTable_dataset_sample();
            //fillTable_sample_attribute();

            fillTable_file_type();
            fillTable_file_format();
            fillTable_file();

            fillTable_file_sample();//create to upload file sample
            //fillTable_curationlog(); //add curation log
            // fillTable_dataset_("file");
            //fillTable_external_link();
            //fillTable_dataset_("external_link");
            fillTable_project();
            fillTable_dataset_project();
            //fillTable_dataset_("project");
            //comparison();
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace(Excel2Database.excel2DBLog.printWriter);
            Excel2Database.excel2DBLog.printWriter.flush();
            result = false;
        }
        // format records
        return result;
    }

    public ArrayList<String> createupdateStmt() throws IOException,
            SQLException {

        return schema.naive_createUpdateStmt();

    }

    public ArrayList<String> createInsertStmt(boolean ignoreExist) throws IOException {
        return schema.createInsertStmt(ignoreExist);
    }

    public ArrayList<String> naive_createUpdateStmt() throws IOException, SQLException {
        return schema.naive_createUpdateStmt();
    }

    public static void main(String[] args) throws Exception {
    }

}
