package gr.watchful.permsyncer.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	public static ArrayList<ArrayList<String>> toArray(File file, int sheetNum) throws FileNotFoundException, IOException {
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();

		XSSFWorkbook workbook = new XSSFWorkbook(POIXMLDocument.openPackage(file.getAbsolutePath()));
		XSSFSheet sheet = workbook.getSheetAt(sheetNum);
		for(Row row : sheet) {
			ArrayList<String> temp = new ArrayList<String>();
			for(Cell cell : row) {
				if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
					temp.add(cell.getStringCellValue());
				} else {
					temp.add("");
				}
			}
			rows.add(temp);
		}
		return rows;
	}
}