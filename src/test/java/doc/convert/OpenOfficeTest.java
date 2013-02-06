package doc.convert;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.sun.star.beans.PropertyValue;

public class OpenOfficeTest {
	public static void main(String[] args) {
		String src = "/Users/Godwin/work/docview/wordconvert/test.docx";
		String dest = "/Users/Godwin/work/docview/wordconvert/dest/test.html";
		OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
		officeManager.start();
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
		System.out.println("Start converting " + src + " -> " + dest);

		Map<String, Object> loadProperties = new HashMap<String, Object>();

		PropertyValue[] loadProps = new PropertyValue[6];
		for (int i = 0; i < loadProps.length; i++) {
			loadProps[i] = new PropertyValue();
		}

		loadProps[0].Name = "PublishMode";
		loadProps[0].Value = 1;
		loadProps[1].Name = "IsExportContentsPage";
		loadProps[1].Value = new Boolean(true);
		loadProps[2].Name = "Hidden";
		loadProps[2].Value = new Boolean(true);
		loadProps[3].Name = "Width";
		loadProps[3].Value = 1280;
		loadProps[4].Name = "IsExportNotes";
		loadProps[4].Value = new Boolean(true);
		loadProps[5].Name = "IndexURL";
		loadProps[5].Value = "index.html";

		loadProperties.put("FilterData", loadProps);

		converter.setDefaultLoadProperties(loadProperties);
		converter.convert(new File(src), new File(dest));
		officeManager.stop();
		System.out.println("Done!");
	}
}
