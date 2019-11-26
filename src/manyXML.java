import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class manyXML {
	
	
	
	public static void main(String[] args) throws Exception {
		
		XmlGenerator xmlGenerator;
		xmlGenerator = new XmlGenerator();
		for(int i=101008; i<101040;i++)
		{
			xmlGenerator.generateXml(String.valueOf(i));
			
		}
		
		
	}
	
	
	

}
