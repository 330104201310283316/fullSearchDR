package com.example.util;


import java.util.Arrays;
import java.util.List;

import com.example.Entity.FileEntity;
import com.example.Entity.FileEntityList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.DocumentProperties;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.wltea.analyzer.lucene.IKAnalyzer;


import java.io.*;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LunceneUtil {

    // 创建Analyzer词法分析器，注意SimpleAnalyzer和StandardAnalyzer的区别
    private static Analyzer analyzer = null;
    // 创建directory,保存索引,可以保存在内存中也可以保存在硬盘上
    private static Directory directory = null;
    // 创建indexWriter创建索引
    private static IndexWriter indexWriter = null;
    //定义全局变量，判断是否有密码
    private static String isLock = "0";

    /**
     * 递归查询文件
     *
     * @Title: creatIndex
     * @author hegg
     * @date 2017年11月6日 下午8:29:37 返回类型 void
     */
    public static void circularCreatIndex(File[] files) {
        File[] subfiles;
        try {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    File fileDirs = new File(files[i].getAbsolutePath());
                    subfiles = fileDirs.listFiles();
                    circularCreatIndex(subfiles);
                } else {
                    String fileNameIndexOf = files[i].getName();
                    String type = "";
                    String contents = "";
                    if (fileNameIndexOf.lastIndexOf(".") != -1) {
                        type = SubStrUtil.subLastString(fileNameIndexOf, ".", 0);
                    }
                    //获取文件后缀
                    if (".txt".equalsIgnoreCase(type)) {
                        contents += LunceneUtil.readTxt(files[i]);
                    } else if (".doc".equalsIgnoreCase(type)) {
                        contents += LunceneUtil.readWord(files[i], ".doc");
                    } else if (".docx".equalsIgnoreCase(type)) {
                        contents += LunceneUtil.readWord(files[i], ".docx");
                    } else if (".xls".equalsIgnoreCase(type)) {
                        contents += LunceneUtil.readExcel(files[i], ".xls", "6sQEHFkaay");
                    } else if (".xlsx".equalsIgnoreCase(type)) {
                        contents += LunceneUtil.readExcel(files[i], ".xlsx", "6sQEHFkaay");
                    } else if (".pdf".equalsIgnoreCase(type)) {
                        contents += LunceneUtil.readPDF(files[i]);
                    } else if (".ppt".equalsIgnoreCase(type)) {
                        //  contents += readHtml(files[i]);
                        contents += LunceneUtil.readPPT(files[i]);
                    } else if (".pptx".equalsIgnoreCase(type)) {
                        //  contents += readHtml(files[i]);
                        contents += LunceneUtil.readPPTX(files[i]);
                    } else if (".html".equalsIgnoreCase(type)) {
                        //  contents += readHtml(files[i]);
                        continue;
                    }
                    Document document = new Document();
                    document.add(new Field("content", contents, TextField.TYPE_STORED));
                    document.add(new Field("fileName", files[i].getName(), TextField.TYPE_STORED));
                    document.add(new Field("filePath", files[i].getPath(), TextField.TYPE_STORED));
                    document.add(new Field("updateTime", files[i].lastModified() + "", TextField.TYPE_STORED));
                    document.add(new Field("rid", CreateId.generate(), TextField.TYPE_STORED));
                    document.add(new Field("isLock", isLock, TextField.TYPE_STORED));
                    indexWriter.addDocument(document);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建索引
     *
     * @Title: creatIndex
     * @author hegg
     * @date 2020年11月18日 下午8:29:31返回类型 void
     */
    public static void creatIndex(String INDEX_DIR, String Source_DIR) {
        Date begin = new Date();
        try {
            analyzer = new StandardAnalyzer();
//            analyzer = new SimpleAnalyzer();
            analyzer = new IKAnalyzer(true);
//            directory = FSDirectory.open(new File(INDEX_DIR));
            directory = FSDirectory.open(FileSystems.getDefault().getPath(INDEX_DIR));
            // 创建indexwriterConfig,并指定分词器版本
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            // 创建IndexWriter,需要使用IndexWriterConfig,
            indexWriter = new IndexWriter(directory, config);

            indexWriter.deleteAll();

            File docDirectory = new File(Source_DIR);
            File[] files = docDirectory.listFiles();
            circularCreatIndex(files);
            indexWriter.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (analyzer != null) analyzer.close();
                if (indexWriter != null) indexWriter.close();
                if (directory != null) directory.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Date end = new Date();
        System.out.println("创建索引-----耗时：" + (end.getTime() - begin.getTime()) + "ms\n");
    }

    /**
     * 查找索引，返回符合条件的文件
     *
     * @param keyWord 返回类型 void
     * @Title: searchIndex
     * @author hegg
     * @date 2020年11月18日 下午8:29:31
     */
    public static FileEntityList searchIndex(String keyWord, String INDEX_DIR, String webApiImg) {
        Date begin = new Date();
        // 1、创建Analyzer词法分析器，注意SimpleAnalyzer和StandardAnalyzer的区别
        Analyzer analyzer = null;
        // 2、创建索引在的文件夹
        Directory indexDirectory = null;
        // 3、创建DirectoryReader
        DirectoryReader directoryReader = null;
        FileEntity fileEntity = new FileEntity();
        FileEntityList fileEntityList = new FileEntityList();
        List<FileEntity> fileEntities = new ArrayList<>();
        try {
//            analyzer = new StandardAnalyzer();
//            analyzer = new SimpleAnalyzer();
            analyzer = new IKAnalyzer(true);
//            indexDirectory = FSDirectory.open(new File(INDEX_DIR));
            indexDirectory = FSDirectory.open(FileSystems.getDefault().getPath(INDEX_DIR));
            directoryReader = DirectoryReader.open(indexDirectory);
            // 3:根据DirectoryReader创建indexSeacher
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
            // 4创建搜索用的query,指定搜索域
            String[] fields = {"fileName", "content"}; // 要搜索的字段，一般搜索时都不会只搜索一个字段
            // 字段之间的与或非关系，MUST表示and，MUST_NOT表示not，SHOULD表示or，有几个fields就必须有几个clauses
            BooleanClause.Occur[] clauses = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
            Query query2 = MultiFieldQueryParser.parse(keyWord, fields, clauses, analyzer);
            // 5、根据searcher搜索并且返回TopDocs
            TopDocs topDocs = indexSearcher.search(query2, 100); // 搜索前100条结果
            System.out.println("共找到匹配处：" + topDocs.totalHits); // totalHits和scoreDocs.length的区别还没搞明白
            ///6、根据TopDocs获取ScoreDoc对象
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            System.out.println("共找到匹配文档数：" + scoreDocs.length);
            QueryScorer scorer = new QueryScorer(query2, "content");
            // 7、自定义高亮代码
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span style=\"backgroud-color:black;color:red;font-size:14px;\">", "</span>");
            Highlighter highlighter = new Highlighter(htmlFormatter, scorer);
            highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));
            for (ScoreDoc scoreDoc : scoreDocs) {
                ///8、根据searcher和ScoreDoc对象获取具体的Document对象
                Document document = indexSearcher.doc(scoreDoc.doc);
                fileEntity = new FileEntity();
                fileEntityList = new FileEntityList();
                fileEntity.setFileName(SubStrUtil.subBeginString(document.get("fileName"), ".", 0));
                fileEntity.setFilePath(document.get("filePath"));
                fileEntity.setRId(document.get("rid"));
                fileEntity.setIsLock(document.get("isLock"));
                String type = SubStrUtil.subLastString(document.get("fileName"), ".", 0);
                if (type.equalsIgnoreCase(".doc") || type.equalsIgnoreCase(".docx")) {
                    fileEntity.setImg(webApiImg + "WORD.png");
                } else if (type.equalsIgnoreCase(".xls") || type.equalsIgnoreCase(".xlsx")) {
                    fileEntity.setImg(webApiImg + "ECEL.png");
                } else if (type.equalsIgnoreCase(".ppt")) {
                    fileEntity.setImg(webApiImg + "PPT.png");
                } else {
                    fileEntity.setImg(webApiImg + "pdf.png");
                }
                if (highlighter.getBestFragment(analyzer, "content", document.get("content")) == null) {
                    fileEntity.setFileContent(document.get("content"));
                } else {
                    fileEntity.setFileContent(highlighter.getBestFragment(analyzer, "content", document.get("content")));
                }

                fileEntities.add(fileEntity);
            }
            fileEntityList.setResult(fileEntities);
            fileEntityList.setRowCount(scoreDocs.length);
            System.out.println("======》查询时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (IOException | InvalidTokenOffsetsException | ParseException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        System.out.println("搜索耗时：" + (float) (end.getTime() - begin.getTime()) / 1000 + " 秒/s");
        return fileEntityList;
    }

    /**
     * 删除文件目录下的所有文件
     *
     * @param
     * @return 返回类型 boolean
     * @Title: deleteDir
     * @author hegg
     * @date 2020年11月18日
     */
    public static boolean delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();
            return true;
        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取txt文件的内容
     *
     * @param file
     * @return 返回类型 String
     * @Title: readTxt
     * @author hegg
     * @date 2020年11月18日
     */
    public static String readTxt(File file) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                result = result + "\n" + s;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取Word内容，包括03格式和07格式
     *
     * @param file
     * @param type
     * @return 返回类型 String
     * @Title: readWord
     * @author hegg
     * @date 2020年11月18日
     */
    public static String readWord(File file, String type) {
        String result = "";
        XWPFDocument doc = null;
        InputStream dataStream = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            if (".doc".equals(type)) {
                HWPFDocument hwDoc = new HWPFDocument(fis);
                Range rang = hwDoc.getRange();
                result += rang.text();
            }
            if (".docx".equals(type)) {
                if (SubStrUtil.subBeginAndLastString(file.getName(), ".", "_", 0, 1).equals("lock")) {
                    dataStream = LunceneUtil.readlock(type, fis, "123");
                    doc = new XWPFDocument(dataStream);
                    isLock = "1";
                } else {
                    doc = new XWPFDocument(fis);
                    isLock = "0";
                }
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                result += extractor.getText();
            }

            fis.close();
        } catch (Exception e) {
            System.out.println("某个文件可能携带锁不能解析，更改文件格式");
        }
        return result;
    }

    /**
     * 读取Excel文件内容，包括03格式和07格式
     *
     * @param file
     * @param type
     * @return 返回类型 String
     * @Title: readExcel
     * @author hegg
     * @date 2020年11月18日
     */
    public static String readExcel(File file, String type, String password) {
        String result = "";
        XSSFWorkbook xwb = null;
        InputStream dataStream = null;
        try {
            StringBuilder sb = new StringBuilder();
            FileInputStream fis = new FileInputStream(file);
            if (".xlsx".equals(type)) {
                if (SubStrUtil.subBeginAndLastString(file.getName(), ".", "_", 0, 1).equals("lock")) {
                    dataStream = LunceneUtil.readlock(type, fis, "6sQEHFkaay");
                    xwb = new XSSFWorkbook(dataStream);
                    dataStream.close();
                    isLock = "1";
                } else {
                    xwb = new XSSFWorkbook(fis);
                    isLock = "0";
                }

                for (int i = 0; i < xwb.getNumberOfSheets(); i++) {
                    XSSFSheet sheet = xwb.getSheetAt(i);
                    for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                        XSSFRow row = sheet.getRow(j);
                        for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                            row.getCell(k).setCellType(Cell.CELL_TYPE_STRING);
                            XSSFCell cell = row.getCell(k);
                            sb.append(cell.getRichStringCellValue());
                        }
                    }
                }
            }
            if (".xls".equals(type)) {
                HSSFWorkbook hwb = null;
                if (SubStrUtil.subBeginAndLastString(file.getName(), ".", "_", 0, 1).equals("lock")) {
                    LunceneUtil.readlock(type, fis, "6sQEHFkaay");
                    hwb = (HSSFWorkbook) WorkbookFactory.create(fis);
                    isLock = "1";
                } else {
                    hwb = new HSSFWorkbook(fis);
                    isLock = "0";
                }
                // 得到Excel工作簿对象

                for (int i = 0; i < hwb.getNumberOfSheets(); i++) {
                    HSSFSheet sheet = hwb.getSheetAt(i);
                    for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                        HSSFRow row = sheet.getRow(j);
                        for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                            row.getCell(k).setCellType(Cell.CELL_TYPE_STRING);
                            HSSFCell cell = row.getCell(k);
                            sb.append(cell.getRichStringCellValue());
                        }
                    }
                }
            }

            fis.close();
            result += sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取pdf文件内容
     *
     * @param file
     * @return 返回类型 String
     * @Title: readPDF
     * @author hegg
     * @date 2020年11月18日
     */
    public static String readPDF(File file) {
        isLock = "0";
        String result = null;
        FileInputStream is = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(file);

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);
            result = stripper.getText(document);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 读取html文件内容
     *
     * @param file
     * @return 返回类型 String
     * @Title: readHtml
     * @author hegg
     * @date 2020年11月18日 下午8:29:31 下午8:13:08
     */
    public static String readHtml(File file) {
        isLock = "0";
        StringBuffer content = new StringBuffer("");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // 读取页面
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"));//这里的字符编码要注意，要对上html头文件的一致，否则会出乱码
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line + "\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 读取PPT文件内容
     *
     * @param file
     * @return 返回类型 String
     * @Title: readHtml
     * @author hegg
     * @date 2020年11月18日 下午8:29:31 下午8:13:08
     */
    public static String readPPT(File file) {
        isLock = "0";
        PowerPointExtractor extractor = null;
        try {
            FileInputStream fin = new FileInputStream(file);
            extractor = new PowerPointExtractor(fin);
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractor.getText();
    }

    /**
     * 读取PPTX文件内容
     *
     * @param
     * @return 返回类型 String
     * @Title: readHtml
     * @author hegg
     * @date 2020年11月18日 下午8:29:31 下午8:13:08
     */
    public static String readPPTX(File file) {
        isLock = "0";
        StringBuffer content = new StringBuffer();
        try {
            InputStream is = new FileInputStream(file);
            XMLSlideShow xmlSlideShow = new XMLSlideShow(is);
            List<XSLFSlide> slides = Arrays.asList(xmlSlideShow.getSlides());            //获得每一张幻灯片
            for (XSLFSlide slide : slides) {
                CTSlide rawSlide = slide.getXmlObject();
                CTGroupShape spTree = rawSlide.getCSld().getSpTree();
                List<CTShape> spList = spTree.getSpList();
                for (CTShape shape : spList) {
                    CTTextBody txBody = shape.getTxBody();
                    if (null == txBody) {
                        continue;
                    }
                    List<CTTextParagraph> pList = txBody.getPList();
                    for (CTTextParagraph textParagraph : pList) {
                        List<CTRegularTextRun> textRuns = textParagraph.getRList();
                        for (CTRegularTextRun textRun : textRuns) {
                            content.append(textRun.getT());
                        }
                    }
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }


    /**
     * @param type
     * @param inp
     * @param pwd
     * @return
     */
    public static InputStream readlock(String type, FileInputStream inp, String pwd) {
        Decryptor decryptor = null;
        InputStream dataStream = null;
        try {

            if (type.equals(".docx") || type.equals(".xlsx")) {
                POIFSFileSystem pfs = new POIFSFileSystem(inp);
                EncryptionInfo encInfo = new EncryptionInfo(pfs);
                decryptor = Decryptor.getInstance(encInfo);
                decryptor.verifyPassword(pwd);
                dataStream = decryptor.getDataStream(pfs);
            } else {
                org.apache.poi.hssf.record.crypto.Biff8EncryptionKey
                        .setCurrentUserPassword(pwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataStream;
    }

    public static void main(String args[]) throws Exception {

        String excelPath = "D:\\luceneSource\\工作例会制度.pdf";
//        FileInputStream fis = new FileInputStream(new File(excelPath));
//        POIFSFileSystem pfs = new POIFSFileSystem(fis);
//        //  System.out.println( checkPwd(pfs,password));
        PDDocument document = PDDocument.load(excelPath);
        if (document.isEncrypted()) {
            //Then the pdf file is encrypeted.
            System.out.println("Then the pdf file is encrypeted");
        }
        System.out.println(readPDF(new File(excelPath)));

    }
}