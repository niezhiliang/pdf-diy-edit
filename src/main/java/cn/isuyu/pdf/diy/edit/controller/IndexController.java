package cn.isuyu.pdf.diy.edit.controller;

import cn.isuyu.pdf.diy.edit.dto.Params;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.IRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @GitHub https://github.com/niezhiliang
 * @Date 2020-04-14 13:55
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/")
    public String index() {

        return "index";
    }

    @RequestMapping(value = "test")
    @ResponseBody
    public String test(HttpServletRequest request) {

        Map<String, String[]> map  = request.getParameterMap();
        for (String key : map.keySet()) {
            System.out.println(key + ":"+map.get(key)[0]);
        }

        return "SUCCESS";
    }

    /**
     * 内容填充
     * @param params
     * @return
     * @throws IOException
     */
    @PostMapping(value = "edit")
    @ResponseBody
    public String edit(@RequestBody Params params) throws IOException {

        String text =params.getText().replaceAll("&hc","\n").replaceAll("&nbsp","\u00A0");

        writeParagraph(85f,96f,536f,559f,text,"./data/1.pdf",1);

        return "SUCCESS";
    }



    /**
     * 计算出当前字体所占像素高度
     * @param doc
     * @param rect
     * @param p
     * @return
     */
    private static float resolveTextHeight(Document doc, Rectangle rect, Paragraph p) {
        IRenderer pRenderer = p.createRendererSubTree().setParent(doc.getRenderer());
        LayoutResult pLayoutResult = pRenderer.layout(new LayoutContext(new LayoutArea(0, rect)));

        Rectangle pBBox = pLayoutResult.getOccupiedArea().getBBox();
        return pBBox.getHeight();
    }

    private static void writeParagraph(float lx,float ly,float rx,float ry,String world,String sourcePath,int pageNo) throws IOException {

        FontProgram fontProgram = FontProgramFactory.createFont("./font/SIMKAI.TTF");
        // 编码使用 PdfEncodings.IDENTITY_H
        PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, true);

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourcePath),new PdfWriter("./write.pdf"));
        Document doc = new Document(pdfDoc);
        Rectangle rect = new Rectangle(lx, ly, rx - lx, ry - ly);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getPage(pageNo));
        canvas.setStrokeColor(ColorConstants.WHITE)
                .setLineWidth(0.5f)
                .rectangle(rect)
                .stroke();
        Paragraph p = new Paragraph(world);
        //设置行距
        p.setFixedLeading(14f);

        //p.setHeight(14f);

        p.setFont(font);//.setFirstLineIndent(24);
        p.setFontSize(14f);
        float width = resolveTextHeight(doc, rect, p);
        new Canvas(canvas, pdfDoc, rect)
                .add( p.setFixedPosition(lx, ry - width , rx - lx).setMargin(0));

        doc.close();
    }

}
