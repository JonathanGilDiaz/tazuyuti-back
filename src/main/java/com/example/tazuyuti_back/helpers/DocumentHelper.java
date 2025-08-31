/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.helpers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.modules.DetalleVenta;
import com.example.tazuyuti_back.entities.modules.Venta;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;

@Service
public class DocumentHelper {

    @Value("${files.upload-directory-documento}")
    private String directoryDocumento;

    @Value("${files.upload-directory-imagenes:uploads/documentos/}")
    private String directoryImages;

    public Map<String, Object> createTicketVenta(Venta venta) {
        Map<String, Object> response = new HashMap<>();
        try {
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
            String archivoPdf = directoryDocumento + "ticket_" + venta.getFolio() + "_"
                    + LocalDateTime.now().format(formatoFecha) + ".pdf";
            if (new File(archivoPdf).exists()) {
                new File(archivoPdf).delete();
            }
            String logoBase64 = "data:" + Utils.getMime("png") + ";base64,"
                    + Utils.encodeFileToBase64Binary(directoryImages + "logo_ticket.png");
            StringBuilder productosHtml = new StringBuilder();
            for (DetalleVenta det : venta.getDetalleVentas()) {
                productosHtml.append("<tr>")
                        .append("<td>").append(det.getCantidad()).append("</td>")
                        .append("<td>").append(det.getProducto().getCodigo()).append("</td>")
                        .append("<td>").append(det.getProducto().getNombre()).append("</td>")
                        .append("<td>$").append(String.format("%.2f", det.getPrecio())).append("</td>")
                        .append("<td>$").append(String.format("%.2f", det.getSubtotal())).append("</td>")
                        .append("</tr>");
            }

            String html = "<html><head><meta charset='UTF-8'></head>"
                    + "<body style='font-family: monospace; font-size:12px; font-weight:900; text-align:center; width:80mm;'>"

                    // Logo
                    + "<div><img src='" + logoBase64 + "' style='width:65mm; margin-bottom:5px;'/></div>"

                    // Encabezado
                    + "<div style='font-size:13px; font-weight:900;'>Tazuyuti SA de CV</div>"
                    + "<div style='margin:2px 0;'>Sucursal: " + venta.getUsuario().getSucursal().getNombre() + "</div>"
                    + "<div style='margin:2px 0;'>Encargado: Señora Mara</div>"
                    + "<div style='margin:2px 0;'>Régimen simplificado de confianza</div>"
                    + "<div style='margin:2px 0;'>" + venta.getUsuario().getSucursal().getHorario() + "</div>"
                    + "<div style='margin:2px 0;'>" + venta.getUsuario().getSucursal().getDireccion() + "</div>"
                    + "<div style='margin:2px 0;'>" + venta.getUsuario().getSucursal().getTelefono() + "</div>"

                    + "<hr/>"

                    // Folio grande y visible
                    + "<div style='margin:4px 0; font-size:18px; font-weight:900;'>FOLIO: " + venta.getFolio()
                    + "</div>"

                    // Tabla productos
                    + "<table style='width:100%; font-size:12px; text-align:center; border-collapse:collapse;'>"
                    + "<thead><tr><th style='border-bottom:1px solid #000;'>Cant</th>"
                    + "<th style='border-bottom:1px solid #000;'>Código</th>"
                    + "<th style='border-bottom:1px solid #000;'>Desc</th>"
                    + "<th style='border-bottom:1px solid #000;'>Precio</th>"
                    + "<th style='border-bottom:1px solid #000;'>Importe</th></tr></thead>"
                    + "<tbody>" + productosHtml.toString() + "</tbody></table>"

                    + "<hr/>"

                    // Totales
                    + "<div style='text-align:right; font-size:13px; font-weight:900;'>TOTAL: $"
                    + String.format("%.2f", venta.getTotal()) + "</div>";

            if (venta.getFormaPago().toLowerCase().contains("efectivo")) {
                html += "<p style='text-align:right;'>Pago: $" + String.format("%.2f", venta.getPago()) + "</p>"
                        + "<p style='text-align:right;'>Cambio: $" + String.format("%.2f", venta.getCambio()) + "</p>";
            }

            html += "<br/><p style='margin-top:10px;'>*** Gracias por su compra ***</p>"
                    + "</body></html>";
            PdfWriter writer = new PdfWriter(archivoPdf);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(new PageSize(226, 800));
            Document document = new Document(pdfDoc);
            document.setMargins(5, 5, 5, 5);
            List<IElement> elements = HtmlConverter.convertToElements(html);
            for (IElement element : elements) {
                document.add((IBlockElement) element);
            }
            document.close();
response.put("archivo", Utils.encodeFileToBase64(archivoPdf));
            // new File(archivoPdf).delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
