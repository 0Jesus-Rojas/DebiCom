package Servicio;

import Modelo.dto.FacturaPDFDTO;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/** Generador sencillo de factura PDF usando Apache PDFBox. */
public final class FacturaPDFService {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final float MARGIN = 45f;
    private static final float PAGE_WIDTH = 595f;
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2);

    private FacturaPDFService() {
    }

    public static void generar(FacturaPDFDTO factura, OutputStream output) {
        if (factura == null) throw new IllegalArgumentException("La factura es obligatoria.");
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(document, page);
            try {
                float y = page.getMediaBox().getHeight() - MARGIN;
                y = texto(stream, "DebiCom", MARGIN, y, 18, true) - 22;
                y = texto(stream, "FACTURA DE VENTA DIRECTA", MARGIN, y, 12, true) - 18;

                y = linea(stream, y);
                y -= 16;
                y = texto(stream, "Factura: " + safe(factura.getNumeroFactura()), MARGIN, y, 10, false) - 14;
                y = texto(stream, "Fecha: " + (factura.getFechaEmision() == null ? "" : FECHA.format(factura.getFechaEmision())), MARGIN, y, 10, false) - 14;
                y = texto(stream, "Tienda: " + safe(factura.getTienda()) + "   NIT: " + safe(factura.getNitTienda()), MARGIN, y, 10, false) - 14;
                y = texto(stream, "Dirección: " + safe(factura.getDireccionTienda()), MARGIN, y, 10, false) - 18;
                y = texto(stream, "Comprador: " + safe(factura.getComprador()) + "   ID: " + safe(factura.getIdentificacionComprador()), MARGIN, y, 10, false) - 18;
                y = linea(stream, y);
                y -= 18;

                y = texto(stream, "Producto", MARGIN, y, 10, true);
                y = texto(stream, "Cant.", 370, y, 10, true);
                y = texto(stream, "Precio", 420, y, 10, true);
                y = texto(stream, "Subtotal", 490, y, 10, true) - 16;

                for (FacturaPDFDTO.Item item : factura.getItems()) {
                    if (y < 95) {
                        stream.close();
                        page = new PDPage();
                        document.addPage(page);
                        stream = new PDPageContentStream(document, page);
                        y = page.getMediaBox().getHeight() - MARGIN;
                    }
                    String nombre = truncar(safe(item.getNombre()), 48);
                    texto(stream, nombre, MARGIN, y, 9, false);
                    texto(stream, String.valueOf(item.getCantidad()), 370, y, 9, false);
                    texto(stream, money(item.getPrecioUnitario()), 420, y, 9, false);
                    texto(stream, money(item.getSubtotal()), 490, y, 9, false);
                    y -= 15;
                }

                y -= 10;
                y = linea(stream, y) - 18;
                texto(stream, "Subtotal:", 410, y, 10, true);
                texto(stream, money(factura.getSubtotal()), 490, y, 10, false);
                y -= 15;
                texto(stream, "Impuestos:", 410, y, 10, true);
                texto(stream, money(factura.getImpuestos()), 490, y, 10, false);
                y -= 17;
                texto(stream, "TOTAL:", 410, y, 12, true);
                texto(stream, money(factura.getTotal()), 490, y, 12, true);
                y -= 24;
                texto(stream, "Pago: " + safe(factura.getMetodoPago()), MARGIN, y, 10, false);
                y -= 14;
                texto(stream, "Referencia: " + safe(factura.getReferenciaPago()), MARGIN, y, 9, false);
                y -= 24;
                texto(stream, "Venta de contado. No genera crédito.", MARGIN, y, 9, false);
                y -= 15;
                texto(stream, "Documento generado automáticamente por DebiCom.", MARGIN, y, 8, false);
            } finally {
                stream.close();
            }
            document.save(output);
        } catch (IOException | COSVisitorException e) {
            throw new IllegalStateException("No fue posible generar el PDF.", e);
        }
    }

    private static float texto(PDPageContentStream stream, String value, float x, float y,
                               int size, boolean bold) throws IOException {
        stream.beginText();
        stream.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, size);
        stream.moveTextPositionByAmount(x, y);
        stream.drawString(value == null ? "" : value.replace("—", "-").replace("…", "..."));
        stream.endText();
        return y;
    }

    private static float linea(PDPageContentStream stream, float y) throws IOException {
        stream.drawLine(MARGIN, y, MARGIN + CONTENT_WIDTH, y);
        return y;
    }

    private static String safe(String value) {
        return value == null ? "" : value.replace("—", "-").replace("…", "...");
    }

    private static String money(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    private static String truncar(String value, int max) {
        return value.length() <= max ? value : value.substring(0, Math.max(0, max - 1)) + "...";
    }
}
