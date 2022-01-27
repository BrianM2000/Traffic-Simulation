import java.io.File;  
import java.io.FileInputStream;  
import java.util.Iterator;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 

//turn:lanes=* or lanes=* or highway=motorway or highway=trunk or highway=primary or highway=secondary or highway=tertiary or highway=unclassified or highway=residential or highway=traffic_signals
 
public class ExcelReader
{
    public static void excelReader() throws Exception{
        FileInputStream file2 = new FileInputStream(new File("C:\\Users\\thepa\\Desktop\\test4.xlsx"));
        //FileInputStream file1 = new FileInputStream(new File("C:\\Users\\thepa\\Desktop\\test3.xlsx"));
        //XSSFWorkbook wb1 = new XSSFWorkbook(file1);   
        XSSFWorkbook wb2 = new XSSFWorkbook(file2);
        //XSSFSheet sheet1 = wb1.getSheetAt(0);
        XSSFSheet sheet2 = wb2.getSheetAt(0);
        
        
        /*
        for(Row row1 : sheet2){
            for(Row row2 : sheet1){
                //System.out.println(row1.getCell(1).getStringCellValue() + " " + row2.getCell(3).getStringCellValue());
                
                if(row1.getCell(1).getStringCellValue().equals(row2.getCell(3).getStringCellValue())){
                   for(Cell cell1 : row1){
                       //System.out.println(cell1.getAddress());
                       if(cell1.getCellType() == Cell.CELL_TYPE_STRING){
                           System.out.print(cell1.getStringCellValue() + "\t\t\t");
                       }
                       else if(cell1.getCellType() == Cell.CELL_TYPE_NUMERIC){
                            System.out.print(cell1.getNumericCellValue() + "\t\t\t");
                       }
                   }
                   for(Cell cell2 : row2){
                    //System.out.println(cell1.getAddress());
                    if(cell2.getCellType() == Cell.CELL_TYPE_STRING){
                        System.out.print(cell2.getStringCellValue() + "\t\t\t");
                    }
                    else if(cell2.getCellType() == Cell.CELL_TYPE_NUMERIC){
                        System.out.print(cell2.getNumericCellValue() + "\t\t\t");
                    }
                   }
                   System.out.println("");
                }
            }
                
            
        }
        */
    }
}