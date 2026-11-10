package com.abrigo.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abrigo.service.RelatorioService.Coluna;
import com.abrigo.service.RelatorioService.Indicador;
import com.abrigo.service.RelatorioService.Relatorio;
import com.abrigo.service.RelatorioService.Secao;
import com.abrigo.service.RelatorioService.Tipo;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public final class RelatorioExportador {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private static final DateTimeFormatter DATA_BR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATA_HORA_BR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private static final int[] ROSA = {216, 27, 122};
    private static final int[] ROSA_CLARO = {252, 228, 236};
    private static final int[] ZEBRA = {255, 248, 251};
    private static final int[] BORDA = {241, 223, 230};
    private static final int[] TEXTO = {51, 37, 31};
    private static final int[] CINZA = {138, 116, 123};

    private RelatorioExportador() {
    }

    public static String formatar(Object valor, Tipo tipo) {

        if (valor == null) {
            return "";
        }

        switch (tipo) {

            case MOEDA:
                return NumberFormat.getCurrencyInstance(PT_BR)
                        .format(((Number) valor).doubleValue());

            case DATA:
                return valor instanceof LocalDate d
                        ? d.format(DATA_BR)
                        : valor.toString();

            case INTEIRO:
                return String.valueOf(((Number) valor).longValue());

            case DECIMAL:
                NumberFormat nf = NumberFormat.getNumberInstance(PT_BR);
                nf.setMaximumFractionDigits(2);
                return nf.format(((Number) valor).doubleValue());

            default:
                return valor.toString();
        }
    }

    public static void exportarExcel(Relatorio rel, File arquivo)
            throws IOException {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            Estilos estilos = new Estilos(wb);

            boolean primeira = true;
            int indice = 0;

            for (Secao secao : rel.secoes()) {

                Sheet sheet = wb.createSheet(
                        nomeAba(secao.nome(), indice)
                );

                montarAba(
                        wb,
                        sheet,
                        indice,
                        rel,
                        secao,
                        estilos,
                        primeira
                );

                primeira = false;
                indice++;
            }

            wb.setForceFormulaRecalculation(true);

            try (FileOutputStream out = new FileOutputStream(arquivo)) {
                wb.write(out);
            }
        }
    }

    private static void montarAba(
            XSSFWorkbook wb,
            Sheet sheet,
            int indiceAba,
            Relatorio rel,
            Secao secao,
            Estilos est,
            boolean comIndicadores) {

        List<Coluna> colunas = secao.colunas();

        int nCols = colunas.size();

        List<Indicador> kpis = comIndicadores
                ? rel.indicadores()
                : List.of();

        int largura = Math.max(nCols, kpis.size());

        sheet.setDisplayGridlines(false);

        int r = 0;

        Row titulo = sheet.createRow(r);
        titulo.setHeightInPoints(34);

        mesclar(sheet, r, largura);

        Cell ct = titulo.createCell(0);

        ct.setCellValue(
                "SOPRAP  •  " + rel.titulo()
                + (rel.secoes().size() > 1
                ? "  –  " + secao.nome()
                : "")
        );

        for (int c = 1; c < largura; c++) {
            titulo.createCell(c).setCellStyle(est.titulo);
        }

        ct.setCellStyle(est.titulo);

        r++;

        Row sub = sheet.createRow(r);
        sub.setHeightInPoints(20);

        mesclar(sheet, r, largura);

        Cell cs = sub.createCell(0);
        cs.setCellValue(rel.subtitulo());
        cs.setCellStyle(est.subtitulo);

        r++;

        Row ger = sheet.createRow(r);

        mesclar(sheet, r, largura);

        Cell cg = ger.createCell(0);

        cg.setCellValue(
                "Gerado em "
                + LocalDateTime.now().format(DATA_HORA_BR)
        );

        cg.setCellStyle(est.geracao);

        r++;

        sheet.createRow(r).setHeightInPoints(8);
        r++;

        if (!kpis.isEmpty()) {

            Row rotulos = sheet.createRow(r);
            Row valores = sheet.createRow(r + 1);

            rotulos.setHeightInPoints(18);
            valores.setHeightInPoints(28);

            for (int i = 0; i < kpis.size(); i++) {

                Indicador k = kpis.get(i);

                Cell l = rotulos.createCell(i);
                l.setCellValue(k.rotulo());
                l.setCellStyle(est.kpiRotulo);

                Cell v = valores.createCell(i);

                preencher(v, k.valor(), k.tipo());

                v.setCellStyle(est.kpiValor(k.tipo()));
            }

            r += 2;

            sheet.createRow(r).setHeightInPoints(10);
            r++;
        }

        int linhaCabecalho = r;

        Row cab = sheet.createRow(r);
        cab.setHeightInPoints(24);

        for (int c = 0; c < nCols; c++) {

            Cell cell = cab.createCell(c);

            cell.setCellValue(colunas.get(c).titulo());

            cell.setCellStyle(
                    est.cabecalho(colunas.get(c).tipo())
            );
        }

        r++;

        int primeiraLinhaDados = r;

        int[] maxLen = new int[nCols];

        for (int c = 0; c < nCols; c++) {
            maxLen[c] = colunas.get(c).titulo().length() + 4;
        }

        int n = 0;

        for (List<Object> linha : secao.linhas()) {

            Row row = sheet.createRow(r++);

            boolean zebra = n++ % 2 == 1;

            for (int c = 0; c < nCols; c++) {

                Object valor = c < linha.size()
                        ? linha.get(c)
                        : null;

                Tipo tipo = colunas.get(c).tipo();

                Cell cell = row.createCell(c);

                preencher(cell, valor, tipo);

                cell.setCellStyle(est.dado(tipo, zebra));

                maxLen[c] = Math.max(
                        maxLen[c],
                        formatar(valor, tipo).length() + 2
                );
            }
        }

        int ultimaLinhaDados = r - 1;

        if (secao.linhas().isEmpty()) {

            Row vazio = sheet.createRow(r++);

            mesclar(sheet, vazio.getRowNum(), nCols);

            Cell cv = vazio.createCell(0);

            cv.setCellValue("Nenhum registro encontrado.");

            cv.setCellStyle(est.vazio);
        }

        if (secao.totais() && !secao.linhas().isEmpty()) {

            Row tot = sheet.createRow(r++);

            tot.setHeightInPoints(22);

            for (int c = 0; c < nCols; c++) {

                Cell cell = tot.createCell(c);

                Tipo tipo = colunas.get(c).tipo();

                if (c == 0) {
                    cell.setCellValue("TOTAL");
                }

                if (tipo == Tipo.MOEDA) {

                    String col =
                            CellReference.convertNumToColString(c);

                    cell.setCellFormula(
                            "SUM(" + col
                            + (primeiraLinhaDados + 1)
                            + ":"
                            + col
                            + (ultimaLinhaDados + 1)
                            + ")"
                    );
                }

                cell.setCellStyle(est.total(tipo));
            }
        }

        for (int c = 0; c < Math.max(nCols, kpis.size()); c++) {

            int chars = c < nCols ? maxLen[c] : 16;

            chars = Math.max(14, Math.min(chars, 48));

            sheet.setColumnWidth(c, chars * 256);
        }

        sheet.createFreezePane(0, linhaCabecalho + 1);

        if (!secao.linhas().isEmpty()) {

            sheet.setAutoFilter(
                    new CellRangeAddress(
                            linhaCabecalho,
                            ultimaLinhaDados,
                            0,
                            nCols - 1
                    )
            );
        }

        sheet.setFitToPage(true);

        PrintSetup ps = sheet.getPrintSetup();

        ps.setLandscape(true);
        ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
        ps.setFitWidth((short) 1);
        ps.setFitHeight((short) 0);

        sheet.setRepeatingRows(
                new CellRangeAddress(
                        linhaCabecalho,
                        linhaCabecalho,
                        -1,
                        -1
                )
        );

        Footer footer = sheet.getFooter();

        footer.setCenter("SOPRAP - Página &P de &N");
    }

    private static void mesclar(
            Sheet sheet,
            int linha,
            int colunas) {

        if (colunas > 1) {

            sheet.addMergedRegion(
                    new CellRangeAddress(
                            linha,
                            linha,
                            0,
                            colunas - 1
                    )
            );
        }
    }

    private static void preencher(
            Cell cell,
            Object valor,
            Tipo tipo) {

        if (valor == null) {
            return;
        }

        switch (tipo) {

            case MOEDA:
            case DECIMAL:
            case INTEIRO:
                cell.setCellValue(
                        ((Number) valor).doubleValue()
                );
                break;

            case DATA:

                if (valor instanceof LocalDate d) {
                    cell.setCellValue(d);
                } else {
                    cell.setCellValue(valor.toString());
                }

                break;

            default:
                cell.setCellValue(valor.toString());
        }
    }

    private static String nomeAba(
            String nome,
            int indice) {

        String limpo = nome == null
                ? ""
                : nome.replaceAll("[\\\\/?*\\[\\]:]", " ").trim();

        if (limpo.isEmpty()) {
            limpo = "Relatório " + (indice + 1);
        }

        return limpo.length() > 31
                ? limpo.substring(0, 31)
                : limpo;
    }

    private static final class Estilos {

        private final XSSFWorkbook wb;

        private final Map<String, XSSFCellStyle> cache =
                new HashMap<>();

        final XSSFCellStyle titulo;
        final XSSFCellStyle subtitulo;
        final XSSFCellStyle geracao;
        final XSSFCellStyle kpiRotulo;
        final XSSFCellStyle vazio;

        Estilos(XSSFWorkbook wb) {

            this.wb = wb;

            this.titulo = base(
                    fonte(true, 16, new int[]{255, 255, 255}),
                    ROSA,
                    null,
                    HorizontalAlignment.LEFT
            );

            this.titulo.setIndention((short) 1);

            this.subtitulo = base(
                    fonte(true, 11, TEXTO),
                    null,
                    null,
                    HorizontalAlignment.LEFT
            );

            this.geracao = base(
                    fonte(false, 9, CINZA),
                    null,
                    null,
                    HorizontalAlignment.LEFT
            );

            this.kpiRotulo = base(
                    fonte(true, 9, CINZA),
                    ROSA_CLARO,
                    BORDA,
                    HorizontalAlignment.CENTER
            );

            this.vazio = base(
                    fonte(false, 10, CINZA),
                    null,
                    null,
                    HorizontalAlignment.CENTER
            );
        }

        XSSFCellStyle kpiValor(Tipo tipo) {

            return cache.computeIfAbsent("kpi" + tipo, k -> {

                XSSFCellStyle s = base(
                        fonte(true, 14, TEXTO),
                        ROSA_CLARO,
                        BORDA,
                        HorizontalAlignment.CENTER
                );

                s.setDataFormat(formato(tipo));

                return s;
            });
        }

        XSSFCellStyle cabecalho(Tipo tipo) {

            return cache.computeIfAbsent("cab" + tipo, k ->
                    base(
                            fonte(
                                    true,
                                    10,
                                    new int[]{255, 255, 255}
                            ),
                            ROSA,
                            ROSA,
                            alinhamento(tipo, true)
                    )
            );
        }

        XSSFCellStyle dado(
                Tipo tipo,
                boolean zebra) {

            return cache.computeIfAbsent(
                    "dado" + tipo + zebra,
                    k -> {

                        XSSFCellStyle s = base(
                                fonte(false, 10, TEXTO),
                                zebra ? ZEBRA : null,
                                BORDA,
                                alinhamento(tipo, false)
                        );

                        s.setDataFormat(formato(tipo));

                        s.setWrapText(tipo == Tipo.TEXTO);

                        return s;
                    }
            );
        }

        XSSFCellStyle total(Tipo tipo) {

            return cache.computeIfAbsent("total" + tipo, k -> {

                XSSFCellStyle s = base(
                        fonte(true, 11, TEXTO),
                        ROSA_CLARO,
                        ROSA,
                        alinhamento(tipo, false)
                );

                s.setDataFormat(formato(tipo));

                s.setBorderTop(BorderStyle.MEDIUM);

                return s;
            });
        }

        private HorizontalAlignment alinhamento(
                Tipo tipo,
                boolean cabecalho) {

            switch (tipo) {

                case MOEDA:
                case DECIMAL:
                case INTEIRO:
                    return HorizontalAlignment.RIGHT;

                case DATA:
                    return HorizontalAlignment.CENTER;

                default:
                    return HorizontalAlignment.LEFT;
            }
        }

        private short formato(Tipo tipo) {

            switch (tipo) {

                case MOEDA:
                    return wb.createDataFormat()
                            .getFormat("\"R$\" #,##0.00");

                case DATA:
                    return wb.createDataFormat()
                            .getFormat("dd/mm/yyyy");

                case INTEIRO:
                    return wb.createDataFormat()
                            .getFormat("#,##0");

                default:
                    return 0;
            }
        }

        private XSSFFont fonte(
                boolean negrito,
                int tamanho,
                int[] rgb) {

            XSSFFont f = wb.createFont();

            f.setFontName("Calibri");
            f.setBold(negrito);
            f.setFontHeightInPoints((short) tamanho);
            f.setColor(cor(rgb));

            return f;
        }

        private XSSFCellStyle base(
                XSSFFont fonte,
                int[] fundo,
                int[] borda,
                HorizontalAlignment h) {

            XSSFCellStyle s = wb.createCellStyle();

            s.setFont(fonte);
            s.setAlignment(h);
            s.setVerticalAlignment(VerticalAlignment.CENTER);

            if (fundo != null) {

                s.setFillForegroundColor(cor(fundo));

                s.setFillPattern(
                        FillPatternType.SOLID_FOREGROUND
                );
            }

            if (borda != null) {

                XSSFColor c = cor(borda);

                s.setBorderTop(BorderStyle.THIN);
                s.setBorderBottom(BorderStyle.THIN);
                s.setBorderLeft(BorderStyle.THIN);
                s.setBorderRight(BorderStyle.THIN);

                s.setTopBorderColor(c);
                s.setBottomBorderColor(c);
                s.setLeftBorderColor(c);
                s.setRightBorderColor(c);
            }

            return s;
        }

        private static XSSFColor cor(int[] rgb) {

            return new XSSFColor(
                    new byte[]{
                            (byte) rgb[0],
                            (byte) rgb[1],
                            (byte) rgb[2]
                    },
                    null
            );
        }
    }

    public static void exportarPdf(
            Relatorio rel,
            File arquivo) throws Exception {

        int maxColunas = rel.secoes()
                .stream()
                .mapToInt(s -> s.colunas().size())
                .max()
                .orElse(1);

        Rectangle pagina = maxColunas > 5
                ? PageSize.A4.rotate()
                : PageSize.A4;

        Document doc = new Document(
                pagina,
                32,
                32,
                36,
                40
        );

        try (FileOutputStream out =
                     new FileOutputStream(arquivo)) {

            PdfWriter.getInstance(doc, out);

            Font fRodape = FontFactory.getFont(
                    FontFactory.HELVETICA,
                    8,
                    Font.NORMAL,
                    awt(CINZA)
            );

            HeaderFooter rodape = new HeaderFooter(
                    new Phrase("SOPRAP  -  Página ", fRodape),
                    true
            );

            rodape.setAlignment(Element.ALIGN_CENTER);
            rodape.setBorder(Rectangle.NO_BORDER);

            doc.setFooter(rodape);

            doc.open();

            Font fTitulo = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    18,
                    Font.BOLD,
                    awt(ROSA)
            );

            Font fSub = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    10,
                    Font.BOLD,
                    awt(TEXTO)
            );

            Font fGerado = FontFactory.getFont(
                    FontFactory.HELVETICA,
                    8,
                    Font.NORMAL,
                    awt(CINZA)
            );

            doc.add(new Paragraph(
                    "SOPRAP - Sociedade Protetora dos Animais de Apucarana",
                    fGerado
            ));

            Paragraph titulo = new Paragraph(
                    rel.titulo(),
                    fTitulo
            );

            titulo.setSpacingBefore(2);

            doc.add(titulo);

            doc.add(new Paragraph(
                    rel.subtitulo(),
                    fSub
            ));

            Paragraph gerado = new Paragraph(
                    "Gerado em "
                    + LocalDateTime.now().format(DATA_HORA_BR),
                    fGerado
            );

            gerado.setSpacingAfter(10);

            doc.add(gerado);

            if (!rel.indicadores().isEmpty()) {
                doc.add(tabelaIndicadores(rel.indicadores()));
            }

            for (Secao secao : rel.secoes()) {

                Font fSecao = FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        12,
                        Font.BOLD,
                        awt(TEXTO)
                );

                Paragraph p = new Paragraph(
                        secao.nome(),
                        fSecao
                );

                p.setSpacingBefore(16);
                p.setSpacingAfter(6);

                doc.add(p);

                if (secao.linhas().isEmpty()) {

                    doc.add(new Paragraph(
                            "Nenhum registro encontrado.",
                            fGerado
                    ));

                } else {

                    doc.add(tabelaSecao(secao));
                }
            }

            doc.close();
        }
    }

    private static PdfPTable tabelaIndicadores(
            List<Indicador> kpis) {

        PdfPTable t = new PdfPTable(kpis.size());

        t.setWidthPercentage(100);

        Font fRot = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                8,
                Font.BOLD,
                awt(CINZA)
        );

        Font fVal = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                13,
                Font.BOLD,
                awt(TEXTO)
        );

        for (Indicador k : kpis) {

            PdfPCell c = new PdfPCell();

            c.setBackgroundColor(awt(ROSA_CLARO));
            c.setBorderColor(awt(BORDA));
            c.setBorderWidth(1f);
            c.setPadding(8);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph rot = new Paragraph(
                    k.rotulo().toUpperCase(PT_BR),
                    fRot
            );

            rot.setAlignment(Element.ALIGN_CENTER);

            Paragraph val = new Paragraph(
                    formatar(k.valor(), k.tipo()),
                    fVal
            );

            val.setAlignment(Element.ALIGN_CENTER);

            c.addElement(rot);
            c.addElement(val);

            t.addCell(c);
        }

        return t;
    }

    private static PdfPTable tabelaSecao(
            Secao secao) throws Exception {

        List<Coluna> colunas = secao.colunas();

        int n = colunas.size();

        PdfPTable t = new PdfPTable(n);

        t.setWidthPercentage(100);
        t.setHeaderRows(1);

        float[] pesos = new float[n];

        for (int c = 0; c < n; c++) {

            int max = colunas.get(c).titulo().length();

            for (List<Object> l : secao.linhas()) {

                Object v = c < l.size()
                        ? l.get(c)
                        : null;

                max = Math.max(
                        max,
                        formatar(v, colunas.get(c).tipo()).length()
                );
            }

            pesos[c] = Math.max(7, Math.min(max, 36));
        }

        t.setWidths(pesos);

        Font fCab = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                9,
                Font.BOLD,
                Color.WHITE
        );

        Font fDado = FontFactory.getFont(
                FontFactory.HELVETICA,
                9,
                Font.NORMAL,
                awt(TEXTO)
        );

        Font fTotal = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                9,
                Font.BOLD,
                awt(TEXTO)
        );

        for (Coluna col : colunas) {

            PdfPCell c = new PdfPCell(
                    new Phrase(col.titulo(), fCab)
            );

            c.setBackgroundColor(awt(ROSA));
            c.setBorderColor(awt(ROSA));
            c.setPadding(6);

            c.setHorizontalAlignment(
                    alinhamentoPdf(col.tipo())
            );

            t.addCell(c);
        }

        int i = 0;

        double[] somas = new double[n];

        for (List<Object> linha : secao.linhas()) {

            boolean zebra = i++ % 2 == 1;

            for (int c = 0; c < n; c++) {

                Coluna col = colunas.get(c);

                Object v = c < linha.size()
                        ? linha.get(c)
                        : null;

                if (col.tipo() == Tipo.MOEDA
                        && v instanceof Number num) {

                    somas[c] += num.doubleValue();
                }

                PdfPCell cell = new PdfPCell(
                        new Phrase(
                                formatar(v, col.tipo()),
                                fDado
                        )
                );

                cell.setBorderColor(awt(BORDA));
                cell.setPadding(5);

                cell.setHorizontalAlignment(
                        alinhamentoPdf(col.tipo())
                );

                if (zebra) {
                    cell.setBackgroundColor(awt(ZEBRA));
                }

                t.addCell(cell);
            }
        }

        if (secao.totais()) {

            for (int c = 0; c < n; c++) {

                Coluna col = colunas.get(c);

                String texto = c == 0
                        ? "TOTAL"
                        : col.tipo() == Tipo.MOEDA
                        ? formatar(somas[c], Tipo.MOEDA)
                        : "";

                if (c == 0 && col.tipo() == Tipo.MOEDA) {
                    texto = formatar(somas[c], Tipo.MOEDA);
                }

                PdfPCell cell = new PdfPCell(
                        new Phrase(texto, fTotal)
                );

                cell.setBackgroundColor(awt(ROSA_CLARO));
                cell.setBorderColor(awt(ROSA));
                cell.setPadding(6);

                cell.setHorizontalAlignment(
                        alinhamentoPdf(col.tipo())
                );

                t.addCell(cell);
            }
        }

        return t;
    }

    private static int alinhamentoPdf(Tipo tipo) {

        switch (tipo) {

            case MOEDA:
            case DECIMAL:
            case INTEIRO:
                return Element.ALIGN_RIGHT;

            case DATA:
                return Element.ALIGN_CENTER;

            default:
                return Element.ALIGN_LEFT;
        }
    }

    private static Color awt(int[] rgb) {

        return new Color(
                rgb[0],
                rgb[1],
                rgb[2]
        );
    }
}
