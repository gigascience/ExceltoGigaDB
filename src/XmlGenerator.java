import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.mozilla.javascript.ast.NewExpression;

import Log.Log;

/**
 * 
 */

/**
 * @author ����������
 * 
 * @date 2012-5-1
 */
public class XmlGenerator {
	Database database;

	XmlGenerator() throws Exception {
		database = new Database();
	}

	//
	void generateXml(String identifier) throws SQLException, IOException {
		//String path = "metadataDir/" + identifier.replace("/", "_") + ".xml";
		String path = "metadataDir/" + identifier + ".xml";
		Log xmlFile = new Log(path, false);
		String id= database.get_result("select id from dataset where identifier="+"'"+identifier+"'", "id");
		
		//ArrayList<String> author_id= database.get_result_more("select author_id from dataset_author where dataset_id="+id, "author_id");
		ArrayList<String> creatorList = new ArrayList<String>();
		
		String statement = "select author.first_name, author.middle_name, author.surname, author.orcid from author, dataset_author where dataset_author.author_id= author.id and dataset_author.dataset_id="+id+" order by dataset_author.rank;";
		ResultSet resultSet1 = database.get_result_resultset(statement);
		
		while (resultSet1.next()) {
			String creator = resultSet1.getString(1) +" "+ resultSet1.getString(2)+" " + resultSet1.getString(3)+"##"+resultSet1.getString(4) ;
			creator = creator.replace("null", "");
			creatorList.add(creator);
		}
		
		// title
		String query = "select title from dataset where identifier='" + identifier
				+ "';";
		ResultSet resultSet = database.get_result_resultset(query);
		
		String title = null;
		while (resultSet.next()) {
			title = resultSet.getString(1);
			
			//title = title.replace("<", "&lt;");
			//title = title.replace(">", "&gt;");
			title= title.replace("<em>", "");
			title= title.replace("</em>", "");
		
			System.out.println("old title  " + title);
			
			title=title.replace(".","");
			
			System.out.println("new title  " + title);
		}
		// publisher
		
		String publisher_id= database.get_result("select publisher_id from dataset where id="+id, "publisher_id");
		query = "select name from publisher where id='" + publisher_id
				+ "';";
		resultSet = database.get_result_resultset(query);
		String publisher = null;
		while (resultSet.next()) {
			publisher = resultSet.getString(1);
		}
		// upload_status
		query = "select upload_status from dataset where identifier='" + identifier
				+ "';";
		resultSet = database.get_result_resultset(query);
		String upload_status = null;
		while (resultSet.next()) {
			upload_status = resultSet.getString(1);
		}
		// publication_year
		query = "select extract(year from  publication_date) from dataset where identifier='"
				+ identifier + "';";
		resultSet = database.get_result_resultset(query);
		String publicationYear = null;
		while (resultSet.next()) {
			publicationYear = resultSet.getString(1);
		}
		
		// publication_date
		query="select publication_date from dataset where identifier='"
				+ identifier + "';";
		resultSet = database.get_result_resultset(query);
		String publicationdate = null;
		while (resultSet.next()) {
			publicationdate = resultSet.getString(1);
		}
		
		// date
		query = "select modification_date from dataset where identifier='"
				+ identifier + "';";
		resultSet = database.get_result_resultset(query);
		String modification_date = null;
		while (resultSet.next()) {
			modification_date = resultSet.getString(1);
		}
		// language
		String language = "eng";
		ArrayList<String> subjects = new ArrayList<String>();
		// subject
		//String dataset_type_id=database.get_result("select type_id from dataset_type where dataset_id="+id, "type_id");
		query = "select type.name from type, dataset_type where dataset_type.type_id=type.id and dataset_type.dataset_id='"
				+ id + "';";
		resultSet = database.get_result_resultset(query);
		// String
		while (resultSet.next()) {
			subjects.add(resultSet.getString(1));
		}
		// manuscript+doi
		query = "select identifier from manuscript where dataset_id='"
				+ id + "';";
		resultSet = database.get_result_resultset(query);
		ArrayList<String> relatedDoiList = new ArrayList<String>();
		while (resultSet.next()) {
			String doi = resultSet.getString(1);
			relatedDoiList.add(doi);
		}
		// relationdoi
		query = "select related_doi from relation where dataset_id='"
				+ id + "';";
		resultSet = database.get_result_resultset(query);
		ArrayList<String> relatedDoiList1 = new ArrayList<String>();
		while (resultSet.next()) {
			String doi = resultSet.getString(1);
			relatedDoiList1.add(doi);
		}
		
		
		
		// relationship
		query = "select relationship.name from relation, relationship where relation.relationship_id=relationship.id and relation.dataset_id='"
				+ id + "';";
		resultSet = database.get_result_resultset(query);
		ArrayList<String> relationship = new ArrayList<String>();
		while (resultSet.next()) {
			String doi = resultSet.getString(1);
			relationship.add(doi);
		}
		
		// size
		query = "select sum(size) from file where dataset_id='"
				+ id + "';";
		
		resultSet = database.get_result_resultset(query);
		long size = 0;
		while (resultSet.next()) {
			size = resultSet.getLong(1);
			System.out.println("sum(size)=" + size);
		}
		// process size
		// 0: B, 1: KB, 2: MB 3: GB 4. TB
		int level = 0;
		String[] unit = { "B", "KB", "MB", "GB", "TB" };
		// long temp=size;
		while (size >= 512) {
			size = (size + 512) / 1024;
			System.out.println("level before" + level);
			level++;
			System.out.println("level after" + level);
			System.out.println(" size after =" + size);
			
		}
		String sizeString = size + " " + unit[level];
		
		// description
		query = "select description from dataset where identifier='"
				+ identifier + "';";
		resultSet = database.get_result_resultset(query);
		String description = null;
		while (resultSet.next()) {
			description = resultSet.getString(1);
			//description = description.replace("<", "&lt;");
			//description = description.replace(">", "&gt;");
		}
		
		description= description.replace("<em>", "");
		description= description.replace("</em>", "");
		
		
		//description= description.replaceAll("<a[^>]*>[\\d\\D]*?>", "");
		description= description.replaceAll("<a href=\"([^\"]*)\"*[\\d\\D]*?>", "");
		description= description.replace("<a >", "");
		description= description.replace("</a>", "");
		description= description.replace("<br>", "");
		
		System.out.print(description);
		// decription type
		String descriptionType = "Abstract";
		// write to file
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<resource xmlns=\"http://datacite.org/schema/kernel-4\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xsi:schemaLocation=\"http://datacite.org/schema/kernel-4 "
				+ "http://schema.datacite.org/meta/kernel-4/metadata.xsd\">\n";

		xml += "\t<identifier identifierType=\"DOI\">10.5524/@</identifier>\n".replace(
				"@", identifier);
		xml += "\t<creators>\n";
		for (String creator : creatorList) {
			String[] allvalue = creator.split("##");
			for(String i:allvalue)
			{
				System.out.println(i);
			}
			if(allvalue.length<2)
			{
				xml += ("\t\t<creator>\n\t\t\t<creatorName>@</creatorName>\n\t\t</creator>\n")
						.replace("@", allvalue[0]);
			}else
			xml += ("\t\t<creator>\n\t\t\t<creatorName>@</creatorName>\n\t\t\t<nameIdentifier schemeURI=\"http://orcid.org/\" nameIdentifierScheme=\"ORCID\">##</nameIdentifier>\n\t\t</creator>\n")
					.replace("@", allvalue[0]).replace("##", allvalue[1]);
		}
		xml += "\t</creators>\n";
		// title
		xml += "\t<titles>\n\t\t<title>@</title>\n\t</titles>\n".replace("@",
				title);
		xml += "\t<publisher>@</publisher>\n".replace("@", publisher);
		
		//year
		
		if(publicationYear==null)
		{
			xml += "\t<publicationYear>2018</publicationYear>\n";
			
		}
		else
		{
			xml += "\t<publicationYear>@</publicationYear>\n".replace("@",
			publicationYear);
		}
			
		
		//System.out.print(publicationYear);
		// date
		//xml += "\t<uploadstatus>@</uploadstatus>\n".replace("@", upload_status);
			
		// subject
		if(subjects.size()>0){
			xml += "\t<subjects>\n";
			for(int i=0;i<subjects.size();i++){
				xml+="\t\t<subject>"+subjects.get(i)+"</subject>\n";
			}
			xml += "\t</subjects>\n";
		}
		
		xml += "\t<dates>\n";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
		xml += "\t<date dateType=\"Available\">@</date>\n".replace("@",dateFormat.format(date));
		if (modification_date != null) {
			xml += "\t<date dateType=\"Updated\">@</date>\n".replace("@",
					modification_date);
		}
		xml += "\t</dates>\n";
		//language		
		xml += "\t<language>@</language>\n".replace("@", language);
		// resourceType
		xml += "\t<resourceType resourceTypeGeneral=\"Dataset\">GigaDB Dataset</resourceType>\n";
		
		if (relatedDoiList.size() != 0 || relatedDoiList1.size() != 0)
	{		
		xml += "\t<relatedIdentifiers>\n";
		if (relatedDoiList.size() != 0) {
			
			for (String relatedidentifier : relatedDoiList) {
				xml += ("\t\t<relatedIdentifier relatedIdentifierType=\"DOI\" relationType=\"IsReferencedBy\">"
						+ "@</relatedIdentifier>\n").replace("@",
						relatedidentifier);
			}
		}
			// Add part 
	
		if (relatedDoiList1.size() != 0) {
			int i=0;
			
			for (String relatedidentifier : relatedDoiList1) {
				String relationship1=relationship.get(i);
				i++;
				xml += ("\t\t<relatedIdentifier relatedIdentifierType=\"DOI\" relationType=\"$\">"
						+ "@</relatedIdentifier>\n").replace("@",
						relatedidentifier).replace("$",relationship1);
		}
		}
			xml += "\t</relatedIdentifiers>\n";
	}
		//}
		// // relatedIdentifierTYpe
	//	 xml +="\t<relatedIdentifierType> relatedIdentifierType=\"DOI\" relationType="@".replace(
		// "@", "DOI");
		// xml += "\t<relationType>@</relationType>\n".replace("@", "Cites");
		// size
		xml += "\t<sizes>\n\t\t<size>@</size>\n\t</sizes>\n".replace("@",
				sizeString);
		xml += "\t<rights>http://creativecommons.org/publicdomain/zero/1.0/</rights>\n";
		xml += "\t<descriptions>\n\t\t<description descriptionType=\"Abstract\">@</description>\n\t</descriptions>\n"
				.replace("@", description);

		//
		xml += "</resource>\n";
		xmlFile.writeLine(xml);
	}

	public static void main(String[] args) throws Exception {
		// XmlGenerator xmlGenerator = new XmlGenerator();
		// xmlGenerator.generateXml("10.5524/100003",
		// "metadataDir/metadata.xml");
		if (args.length > 0) {
			XmlGenerator xmlGenerator = new XmlGenerator();
			System.out.println("get outside arguments!");
			System.out.println(args[0]);
			String identifier = args[0];
			xmlGenerator.generateXml(identifier);
			return;
		}
		XmlGenerator xmlGenerator = new XmlGenerator();
		//xmlGenerator.generateXml("10.5524/100031");
		xmlGenerator.generateXml("102163");
	}
}
