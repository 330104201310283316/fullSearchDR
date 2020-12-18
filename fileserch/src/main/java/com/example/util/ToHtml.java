package com.example.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import net.minidev.asm.ex.ConvertException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import sun.security.util.Password;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToHtml {

    public static final int EXCEL_HTML = 44;

    public static final int WORD_HTML = 8;
    // 创建excel对象
    public static ActiveXComponent appExcelComponent = null;
    // 创建word对象
    public static ActiveXComponent appWordComponent = null;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHMMss");

    public ToHtml() {
    }

    /**
     * word转html
     *
     * @param sourcePath
     * @param targetPath
     */
    public static boolean wordToHtml(String sourcePath, String targetPath) {
        ComThread.InitSTA();
        if (appWordComponent == null || appWordComponent.m_pDispatch == 0) {
            appWordComponent = new ActiveXComponent("Word.Application");
        }
        try {
            // 设置word应用程序不可见
            appWordComponent.setProperty("Visible", new Variant(false));
            // documents表示word程序的所有文档窗口，（word是多文档应用程序）
            Dispatch docs = appWordComponent.getProperty("Documents").toDispatch();
            // 打开要转换的word文件
            //加载带密码的文件，输入原密码
            Dispatch doc = Dispatch.call(docs, "Open", sourcePath, false,// ConfirmConversions
                    true, false, new Variant("123")).toDispatch();
            // 作为html格式保存到临时文件
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{targetPath, new Variant(WORD_HTML)},
                    new int[1]);
            // 关闭word文件
            Dispatch.call(docs, "Close", new Variant(false));
        } catch (Exception e) {
            e.printStackTrace();
            appWordComponent = null;
            return false;
        } finally {
            // 关闭word应用程序
            appWordComponent.invoke("Quit", new Variant[]{});
            ComThread.Release();
            ComThread.quitMainSTA();
        }
        return true;
    }

    /**
     * excel转html
     *
     * @param sourcePath
     * @param targetPath
     */
    public static boolean excelToHtml(String sourcePath, String targetPath) {
        ComThread.InitSTA();
        try {
            if (appExcelComponent == null || appExcelComponent.m_pDispatch == 0)
                appExcelComponent = new ActiveXComponent("Excel.Application");
            appExcelComponent.setProperty("Visible", new Variant(false));
            Dispatch excels = appExcelComponent.getProperty("Workbooks").toDispatch();
            Dispatch excel = Dispatch.invoke(excels, "Open", Dispatch.Method, new Object[]{sourcePath, new Variant(false), new Variant(true), "1", "6sQEHFkaay"},
                    new int[1]).toDispatch();
//				Dispatch sheet = Dispatch.invoke(excel, "sheet(0)", arg2, arg3, arg4)
            Dispatch.invoke(excel, "SaveAs", Dispatch.Method, new Object[]{
                    targetPath, new Variant(EXCEL_HTML)}, new int[1]);
            Dispatch.call(excel, "Close", new Variant(false));
        } catch (Exception e) {
            e.printStackTrace();
            appExcelComponent = null;
            return false;
        } finally {
            // 关闭excel应用程序
            //appExcelComponent.invoke("Quit", new Variant[]{});
            ComThread.Release();
            ComThread.quitMainSTA();
        }
        return true;
    }

    /**
     * ppt转html
     *
     * @param sourcePath
     * @param targetPath
     */
    public static boolean pptToPdf(String sourcePath, String targetPath) {
        ComThread.InitSTA();
        ActiveXComponent appPptComponent = new ActiveXComponent("PowerPoint.Application");
        try {
            Dispatch dispatch = appPptComponent.getProperty("Presentations")
                    .toDispatch();
            Dispatch dispatch1 = Dispatch.call(dispatch, "Open", sourcePath,
                    new Variant(-1), new Variant(-1), new Variant(0))
                    .toDispatch();
            Dispatch.call(dispatch1, "SaveAs", targetPath, new Variant(12));
            Variant variant = new Variant(-1);
            Dispatch.call(dispatch1, "Close");
        } catch (Exception exception) {
            System.out.println("|||" + exception.toString());
            return false;
        } finally {
            appPptComponent.invoke("Quit", new Variant[0]);
            ComThread.Release();
            ComThread.quitMainSTA();
        }
        return true;
    }

    /**
     * pdf转html
     *
     * @param sourcePath
     * @param targetPath
     */
    public static void pdfToHtml(String sourcePath, String targetPath) {
        StringBuffer buffer = new StringBuffer();
        FileOutputStream fos;
        PDDocument document;
        File pdfFile;
        int size;
        BufferedImage image;
        FileOutputStream out;
        Long randStr = 0l;
        //PDF转换成HTML保存的文件夹
        File htmlsDir = new File(targetPath);
        if (!htmlsDir.exists()) {
            htmlsDir.mkdirs();
        }
        File htmlDir = new File(targetPath + "/");
        if (!htmlDir.exists()) {
            htmlDir.mkdirs();
        }
        try {
            //遍历处理pdf附件
            randStr = System.currentTimeMillis();
            buffer.append("<!doctype html>\r\n");
            buffer.append("<head>\r\n");
            buffer.append("<meta charset=\"UTF-8\">\r\n");
            buffer.append("</head>\r\n");
            buffer.append("<body style=\"background-color:gray;\">\r\n");
            buffer.append("<style>\r\n");
            buffer.append("img {background-color:#fff; text-align:center; width:100%; max-width:100%;margin-top:6px;}\r\n");
            buffer.append("</style>\r\n");
            document = new PDDocument();
            //pdf附件
            pdfFile = new File(sourcePath);
            document = PDDocument.load(pdfFile);
            size = document.getNumberOfPages();
            Long start = System.currentTimeMillis(), end = null;
            System.out.println("===>pdf : " + pdfFile.getName() + " , size : " + size);
            PDFRenderer reader = new PDFRenderer(document);
            for (int i = 0; i < size; i++) {
                //image = newPDFRenderer(document).renderImageWithDPI(i,130,ImageType.RGB);
                image = reader.renderImage(i, 1.5f);
                //生成图片,保存位置
                out = new FileOutputStream(targetPath + "/" + "image" + "_" + i + ".jpg");
                ImageIO.write(image, "png", out); //使用png的清晰度
                //将图片路径追加到网页文件里
                buffer.append("<img src=\"" + targetPath + "/" + "image" + "_" + i + ".jpg\"/>\r\n");
                image = null;
                out.flush();
                out.close();
            }
            reader = null;
            document.close();
            buffer.append("</body>\r\n");
            buffer.append("</html>");
            end = System.currentTimeMillis() - start;
            System.out.println("===> Reading pdf times: " + (end / 1000));
            start = end = null;
            //生成网页文件
            fos = new FileOutputStream(targetPath + ".html");
            System.out.println(targetPath + randStr + ".html");
            fos.write(buffer.toString().getBytes());
            fos.flush();
            fos.close();
            buffer.setLength(0);
        } catch (Exception e) {
            System.out.println("===>Reader parse pdf to jpg error : " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 判读文件格式，返回文件名
     *
     * @param filePath
     * @param fileTemp
     * @return
     */
    public static String toHtml(String filePath, String fileTemp) {
        File toHmlFile = new File(filePath);
        //文件的创建时间,文件字节长度拼接到文件的路径
        String str = fileTemp + SubStrUtil.subBeginString(toHmlFile.getName(), ".", 0) + sdf.format(new Date(toHmlFile.lastModified())) + toHmlFile.length() + ".html";
        File htmlFile = new File(str);
        try {
            //判断文件是否存在，如果存在则直接返回文件名称
            if (!htmlFile.exists()) {
                String fileNameSub = toHmlFile.getName();
                String type = "";
                if (fileNameSub.lastIndexOf(".") != -1) {
                    type = SubStrUtil.subLastString(fileNameSub, ".", 0);
                }
                switch (type) {
                    case ".doc":
                    case ".docx":
                    case ".pdf":
                        wordToHtml(toHmlFile.getAbsolutePath(), htmlFile.getAbsolutePath());
                        break;
                    case ".ppt":
                    case ".pptx":
                        pptToPdf(toHmlFile.getAbsolutePath(), htmlFile.getAbsolutePath());
                        break;
                    case ".xls":
                    case ".xlsx":
                        excelToHtml(toHmlFile.getAbsolutePath(), htmlFile.getAbsolutePath());
                        break;
                    default:
                        System.out.println("暂时不支持转换");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("转换失败，返回null");
            return null;
        }
        if (!htmlFile.exists()) {
            System.out.println("转换失败，文件不存在,返回null");
            return null;
        }
        return htmlFile.getName();
    }

    public static void main(String[] args) {
        System.out.println(toHtml("D:\\luceneSource\\新建文件夹\\新建 PPT 演示文稿.pdf", "D:\\luceneSource\\fullsearchApi\\"));
    }

    /**
     * 检验文件密码是否正确
     *
     * @param pwd
     * @return
     */
    public static Boolean checkPwd(String filePath, String pwd) {
        FileInputStream inp = null;
        try {
            inp = new FileInputStream(filePath);
            POIFSFileSystem pfs = new POIFSFileSystem(inp);
            EncryptionInfo encInfo = new EncryptionInfo(pfs);
            Decryptor decryptor = Decryptor.getInstance(encInfo);
            if (!decryptor.verifyPassword(pwd)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
