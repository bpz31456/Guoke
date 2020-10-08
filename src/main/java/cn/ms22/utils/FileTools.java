package cn.ms22.utils;

import cn.ms22.entity.CodeOrder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Baopz
 * @date 2018/05/04
 */
public final class FileTools {

    public static final String FILE_TYPE_TXT = ".txt";
    public static final String FILE_TYPE_XLS = ".xls";
    public static final String FILE_TYPE_DOC = ".doc";

    /**
     * 写入信息到文件
     *
     * @param path
     * @param info
     * @throws IOException
     */
    public static void appendInfo(Path path, String info) throws IOException {
        if (path == null || path.getParent() == null) {
            throw new IOException("文件不能为空," + path);
        }
        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("utf-8"), StandardOpenOption.APPEND);
        writer.newLine();
        writer.append(info);
        writer.close();
    }

    public static void appendExcelInfo(Path path, List<CodeOrder> list, Class clazz) throws IOException, IllegalAccessException {
        if (path == null || path.getParent() == null) {
            throw new IOException("文件不能为空," + path);
        }
        //判断是否存在，如果不存在
        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
            writeExcel0(path, list, clazz);
        } else {
            List<CodeOrder> historicalData = readExcel0(path, clazz);
            //
            historicalData.addAll(list);
            Files.delete(path);
            writeExcel0(path, historicalData, clazz);
        }
    }

    private static List<CodeOrder> readExcel0(Path path, Class clazz) {
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
        return readFile0(workbook, clazz);
    }

    /**
     * @param path
     * @param collection
     * @param clazz
     * @throws IllegalAccessException
     * @throws IOException
     * @since 0.3.2
     */
    private static void writeExcel0(Path path, List<CodeOrder> collection, Class clazz) throws IllegalAccessException, IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("结果集");
        for (int i = 0; i < collection.size(); i++) {
            XSSFRow row = sheet.createRow(i);
            Field[] declaredFields = clazz.getDeclaredFields();
            CodeOrder entity = collection.get(i);
            if (entity==null){
                continue;
            }
            for (int j = 0; j < declaredFields.length; j++) {
                Field field = declaredFields[j];
                //list扩展属性，直接加
                if (field.getType().isAssignableFrom(List.class)) {
                    List<String> codeExs = entity.getCodeExs();
                    for (int k = 0; codeExs != null && k < codeExs.size(); k++) {
                        XSSFCell cell = row.createCell(j + k);
                        cell.setCellValue(codeExs.get(k));
                    }
                } else {
                    field.setAccessible(true);
                    XSSFCell cell = row.createCell(j);
                    if (entity == null) {
                        cell.setCellValue("空");
                    } else {
                        cell.setCellValue(String.valueOf(field.get(entity)));
                    }
                }
            }
        }
        workbook.write(new FileOutputStream(path.toFile()));
        workbook.close();
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
                results.add(null);
                boolean isExs = false;
                List<String> exCodes = null;
                for (int j = 0; j < length; j++) {
                    Cell cell = row.getCell(j);
                    String value = cell.getStringCellValue();
                    if (!isExs) {
                        if (fields[j].getType().isAssignableFrom(List.class)) {
                            isExs = true;
                            exCodes = new ArrayList<>();
                            entity.setCodeExs(exCodes);
                            exCodes.add(value);
                            continue;
                        }
                        try {
                            fields[j].setAccessible(true);
                            fields[j].set(entity, value == null ? "" : value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        exCodes.add(value);
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

    /**
     * 删除目录下的所有文件
     *
     * @param filepath
     * @return
     */
    @Deprecated
    public static boolean deleteFiles(String filepath) {
        File path = new File(filepath);
        boolean deleted = true;
        //清空下载的文件
        if (path.isDirectory()) {
            File[] fs = path.listFiles();
            assert fs != null;
            for (File file1 : fs) {
                if (!(deleted = file1.delete())) {
                    break;
                }
            }
        }
        return deleted;
    }

    /**
     * 删除某个文件
     *
     * @param filename
     * @return
     */
    public static boolean deleteFile(String filename) {
        return deleteFile(new File(filename));
    }

    public static boolean deleteFile(File file) {
        boolean deleted = true;
        //清空下载的文件
        if (file.exists() && file.isFile()) {
            deleted = file.delete();
        }
        return deleted;
    }

    /**
     * url解码
     *
     * @param files
     */
    public static void rename(File[] files) throws UnsupportedEncodingException, InterruptedException {
        for (File file : files) {
            String txt = URLDecoder.decode(file.getAbsolutePath(), "UTF-8");
            file.renameTo(new File(txt));
        }
        TimeUnit.SECONDS.sleep(2);
    }

}
