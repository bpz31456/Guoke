package cn.ms22;

import cn.ms22.entity.CodeOrder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReadTest {

    @Test
    public void test(){
        Path path = Paths.get("C:\\Users\\baopz\\Desktop\\guoke\\MIXTURE\\20190806德军总部：新秩序.xls");
        Class clazz = CodeOrder.class;
        Workbook workbook = null;
        if (path.toString().toLowerCase().endsWith("xls")) {

            try {
                workbook = new XSSFWorkbook(path.toFile());
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
        } else if (path.toString().toLowerCase().endsWith("xlsx")) {
            try {
                workbook = new HSSFWorkbook(new FileInputStream(path.toFile()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("文件名有误" + path.toString());
        }
        assert workbook != null;
        readFile0(workbook, clazz).forEach(System.out::println);
    }

    private static List<CodeOrder> readFile0(Workbook workbook, Class clazz) {
        List<CodeOrder> results = new ArrayList<>();
        try {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                CodeOrder entity = new CodeOrder();
                Row row = sheet.getRow(i);
                int length = row.getLastCellNum();
                Field[] fields = clazz.getDeclaredFields();
                results.add(entity);
                for (int j = 0; j < length; j++) {
                    Cell cell = row.getCell(j);
                    fields[j].setAccessible(true);
                    try {
                        String value = cell.getStringCellValue();
                        fields[j].set(entity, value == null ? "" : value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
