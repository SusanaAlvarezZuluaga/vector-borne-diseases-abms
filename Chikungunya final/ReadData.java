package humans_2303;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import antlr.collections.List;
import repast.simphony.random.RandomHelper;

public class ReadData {
	
	/*public static void main(String[] args) throws IOException {
		ArrayList<Integer> p = loadFromExcel();
		System.out.println(p);
		System.out.println(p.size());
		
	}*/
	
	//private static final int MONTH_COL = 0;
	//private static final int TEMP_MEDIA_COL = 1;
	private static final int TEMP_MAX_COL = 2;
	private static final int TEMP_MIN_COL = 3;
	//private static final int PRECIPITATION_COL = 4;
	//private static final int SUN_HOURS_COL = 5;

	public static ArrayList<ArrayList<Integer>> loadFromExcel() throws IOException {
		ArrayList<Integer> list_tempMax = new ArrayList<Integer>();
		ArrayList<Integer> list_tempMin = new ArrayList<Integer>();
		// open the excel file
		FileInputStream fis = new FileInputStream("C:/Users/Asus/Documents/MAJO/Universidad/SEMESTRE 6/PRACTICA INVESTIGATIVA 1/temperatura y precipitacion bello.xlsx");
		Workbook book = new XSSFWorkbook(fis);
		// get the first worksheet
		Sheet sheet = book.getSheetAt(0);
		// iterate over the rows, skipping the first one
		for (Row row : sheet) {
			if (row.getRowNum() > 0) {
				int temp_max = (int) row.getCell(TEMP_MAX_COL).getNumericCellValue();
				int temp_min = (int) row.getCell(TEMP_MIN_COL).getNumericCellValue();
				list_tempMax.add(temp_max);
				list_tempMin.add(temp_min);
			}
		}
		ArrayList<ArrayList<Integer>> listofList = new ArrayList<ArrayList<Integer>>();
		listofList.add(list_tempMax);
		listofList.add(list_tempMin);
		return listofList;
	}
}