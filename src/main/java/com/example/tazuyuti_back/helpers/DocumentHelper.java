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
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.tazuyuti_back.entities.modules.DetallePaquete;
import com.example.tazuyuti_back.entities.modules.DetalleVenta;
import com.example.tazuyuti_back.entities.modules.Paquete;
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
            new File(archivoPdf).delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Map<String, Object> createTicketPaquete(Paquete paquete) {
        Map<String, Object> response = new HashMap<>();
        try {
            // ---------- nombres/formatos ----------
            DateTimeFormatter fmtNombre = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
            String nombrePdf = "ticket_paquete_" + (paquete.getFolio() != null ? paquete.getFolio().trim() : "SN")
                    + "_" + LocalDateTime.now().format(fmtNombre) + ".pdf";
            String archivoPdf = directoryDocumento + nombrePdf;

            File f = new File(archivoPdf);
            if (f.exists())
                f.delete();

            // ---------- recursos ----------
            String logoBase64 = "data:" + Utils.getMime("png") + ";base64," +
                    Utils.encodeFileToBase64Binary(directoryImages + "logo_ticket.png");

            // ---------- helpers de impresión ----------
            java.util.function.Function<Double, String> money = v -> "$"
                    + String.format(Locale.US, "%,.2f", (v != null ? v : 0d));

            String sucursalNombre = paquete.getUsuario() != null && paquete.getUsuario().getSucursal() != null
                    ? paquete.getUsuario().getSucursal().getNombre()
                    : "";
            String sucursalHorario = paquete.getUsuario() != null && paquete.getUsuario().getSucursal() != null
                    ? paquete.getUsuario().getSucursal().getHorario()
                    : "";
            String sucursalDir = paquete.getUsuario() != null && paquete.getUsuario().getSucursal() != null
                    ? paquete.getUsuario().getSucursal().getDireccion()
                    : "";
            String sucursalTel = paquete.getUsuario() != null && paquete.getUsuario().getSucursal() != null
                    ? paquete.getUsuario().getSucursal().getTelefono()
                    : "";

            String folio = paquete.getFolio() != null ? paquete.getFolio().trim() : "SN";
            String fechaStr = paquete.getFechaCreacion() != null
                    ? paquete.getFechaCreacion().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"))
                    : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"));

            String origen = sucursalNombre;
            String destino = paquete.getDestino() != null ? paquete.getDestino().getNombre() : "";
            String remitente = paquete.getRemitente() != null ? paquete.getRemitente() : "";
            String destinatario = paquete.getDestinatario() != null ? paquete.getDestinatario() : "";
            String recibe = paquete.getUsuario() != null ? paquete.getUsuario().getNombre() : ""; // quién atendió

            // ---------- tabla de conceptos ----------
            StringBuilder conceptos = new StringBuilder();
            if (paquete.getDetallePaquete() != null) {
                for (DetallePaquete det : paquete.getDetallePaquete()) {
                    String desc = det.getPrecioPaquete() != null ? det.getPrecioPaquete().getNombre() : "Concepto";
                    Double precio = det.getPrecio();
                    Double subtotal = det.getSubtotal();
                    Double cantidad = det.getCantidad();

                    conceptos.append("<tr>")
                            .append("<td>").append(String.format(Locale.US, "%.2f", cantidad != null ? cantidad : 0d))
                            .append("</td>")
                            .append("<td>").append(escapeHtml(desc)).append("</td>")
                            .append("<td>").append(money.apply(precio)).append("</td>")
                            .append("<td>").append(money.apply(subtotal)).append("</td>")
                            .append("</tr>");
                }
            }

            // ---------- HTML ----------
            StringBuilder html = new StringBuilder();
            html.append("<html><head><meta charset='UTF-8'></head>")
                    .append("<body style='font-family: monospace; font-size:12px; font-weight:900; text-align:center; width:80mm;'>")

                    // Logo
                    .append("<div><img src='").append(logoBase64)
                    .append("' style='width:65mm; margin-bottom:5px;'/></div>")

                    // Encabezado empresa/sucursal
                    .append("<div style='font-size:13px; font-weight:900;'>Tazuyuti SA de CV</div>")
                    .append("<div style='margin:2px 0;'>Sucursal: ").append(escapeHtml(sucursalNombre)).append("</div>")
                    .append("<div style='margin:2px 0;'>Encargado: Señora Mara</div>")
                    .append("<div style='margin:2px 0;'>Régimen simplificado de confianza</div>")
                    .append("<div style='margin:2px 0;'>").append(escapeHtml(sucursalHorario)).append("</div>")
                    .append("<div style='margin:2px 0;'>").append(escapeHtml(sucursalDir)).append("</div>")
                    .append("<div style='margin:2px 0;'>").append(escapeHtml(sucursalTel)).append("</div>")

                    .append("<hr/>")

                    // Fecha + Folio
                    .append("<div style='text-align:left; display:flex; justify-content:space-between; margin:2px 0;'>")
                    .append("<span>Fecha: ").append(escapeHtml(fechaStr)).append("</span>")
                    .append("<span>Folio: <b>").append(escapeHtml(folio)).append("</b></span>")
                    .append("</div>")

                    // Datos envío
                    .append("<div style='text-align:left; margin-top:6px;'>")
                    .append("<div>Recibe: ").append(escapeHtml(recibe)).append("</div>")
                    .append("<div>Origen: ").append(escapeHtml(origen)).append("</div>")
                    .append("<div>Remitente: ").append(escapeHtml(remitente)).append("</div>")
                    .append("<div>Destino: ").append(escapeHtml(destino)).append("</div>")
                    .append("<div>Destinatario: ").append(escapeHtml(destinatario)).append("</div>")
                    .append("</div>")

                    .append("<hr/>")

                    // Tabla conceptos
                    .append("<table style='width:100%; font-size:12px; text-align:center; border-collapse:collapse;'>")
                    .append("<thead>")
                    .append("<tr>")
                    .append("<th style='border-bottom:1px solid #000;'>Cant</th>")
                    .append("<th style='border-bottom:1px solid #000;'>Descripcion</th>")
                    .append("<th style='border-bottom:1px solid #000;'>Precio</th>")
                    .append("<th style='border-bottom:1px solid #000;'>Importe</th>")
                    .append("</tr>")
                    .append("</thead>")
                    .append("<tbody>").append(conceptos).append("</tbody>")
                    .append("</table>")

                    .append("<hr/>")

                    // Totales
                    .append("<div style='text-align:right; font-size:13px; font-weight:900;'>TOTAL: ")
                    .append(money.apply(paquete.getTotal())).append("</div>");

            // Si fue efectivo, mostrar pago y cambio
            String fp = paquete.getFormaPago() != null ? paquete.getFormaPago() : "";
            if (fp.toLowerCase().contains("efectivo")) {
                html.append("<p style='text-align:right;'>Pago: ").append(money.apply(paquete.getPago())).append("</p>")
                        .append("<p style='text-align:right;'>Cambio: ").append(money.apply(paquete.getCambio()))
                        .append("</p>");
            }

            html.append("<br/><p style='margin-top:10px;'>*** Gracias por su preferencia ***</p>")
                    .append("</body></html>");

            // ---------- PDF ----------
            PdfWriter writer = new PdfWriter(archivoPdf);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(new PageSize(226, 800)); // ~80mm de ancho
            Document document = new Document(pdfDoc);
            document.setMargins(5, 5, 5, 5);

            List<IElement> elements = HtmlConverter.convertToElements(html.toString());
            for (IElement element : elements) {
                document.add((IBlockElement) element);
            }
            document.close();

            response.put("archivo", Utils.encodeFileToBase64(archivoPdf));
            new File(archivoPdf).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private static String escapeHtml(String s) {
        if (s == null)
            return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public Map<String, Object> createTicketInterno(Paquete paquete) {
        Map<String, Object> response = new HashMap<>();
        try {
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String archivoPdf = directoryDocumento + "ticket_interno_" + paquete.getFolio() + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss")) + ".pdf";

            if (new File(archivoPdf).exists())
                new File(archivoPdf).delete();

            String logoBase64 = "data:" + Utils.getMime("png") + ";base64," +
                    Utils.encodeFileToBase64Binary(directoryImages + "logo_ticket.png");

            // 🔹 Calcular total de piezas
            int totalPiezas = paquete.getDetallePaquete().stream()
                    .mapToInt(det -> (int) det.getCantidad())
                    .sum();

            PdfWriter writer = new PdfWriter(archivoPdf);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(new PageSize(226, 600)); // 80mm de ancho aprox
            Document document = new Document(pdfDoc);
            document.setMargins(5, 5, 5, 5);

            int contador = 1;
            for (DetallePaquete det : paquete.getDetallePaquete()) {
                for (int i = 0; i < det.getCantidad(); i++) {
                    String html = "<html><head><meta charset='UTF-8'></head>"
                            + "<body style='font-family: monospace; font-size:13px; text-align:center; width:80mm;'>"

                            // Logo
                            + "<div><img src='" + logoBase64 + "' style='width:70mm; margin-bottom:5px;'/></div>"

                            // Encabezado
                            + "<div style='font-size:14px; font-weight:900;'>EMPRESA DE TURISMO Y PASAJE \"TAZUYUTI\", S.A. DE C.V.</div>"
                            + "<hr style='border:2px solid #000;'/>"

                            // Folio
                            + "<div style='font-size:16px; font-weight:900; text-align:right;'>No: "
                            + paquete.getFolio() + "</div>"

                            // Tabla de datos
                            + "<table style='width:100%; font-size:13px; text-align:left; border-collapse:collapse; margin-top:5px; font-weight:900;'>"
                            + "<tr><td style='border:1px solid #000; padding:5px;'>Lugar y fecha:</td>"
                            + "<td style='border:1px solid #000; padding:5px;'>"
                            + LocalDateTime.now().format(formatoFecha) + "</td></tr>"

                            + "<tr><td style='border:1px solid #000; padding:5px;'>Operador:</td>"
                            + "<td style='border:1px solid #000; padding:5px;'>" + paquete.getUsuario().getNombre()
                            + "</td></tr>"

                            + "<tr><td style='border:1px solid #000; padding:5px;'>Remitente:</td>"
                            + "<td style='border:1px solid #000; padding:5px;'>" + paquete.getRemitente() + "</td></tr>"

                            + "<tr><td style='border:1px solid #000; padding:5px;'>Destinatario:</td>"
                            + "<td style='border:1px solid #000; padding:5px;'>" + paquete.getDestinatario()
                            + "</td></tr>"

                            + "<tr><td style='border:1px solid #000; padding:5px;'>Destino:</td>"
                            + "<td style='border:1px solid #000; padding:5px;'>" + paquete.getDestino().getNombre()
                            + "</td></tr>"

                            + "<tr><td style='border:1px solid #000; padding:5px;'>Concepto:</td>"
                            + "<td style='border:1px solid #000; padding:5px;'>" + det.getPrecioPaquete().getNombre()
                            + "</td></tr>"

                            + "<tr><td style='border:1px solid #000; padding:5px;'>Costo envío:</td>"
                            + "<td style='border:1px solid #000; padding:5px;'>$"
                            + String.format("%.2f", det.getPrecio()) + "</td></tr>"
                            + "</table>"

                            // Pieza en GRANDE y negrita
                            + "<div style='margin-top:20px; font-size:24px; font-weight:900; text-align:center; border:2px solid #000; padding:10px;'>"
                            + "PIEZA: " + contador + " de " + totalPiezas + "</div>"

                            + "</body></html>";

                    List<IElement> elements = HtmlConverter.convertToElements(html);
                    for (IElement element : elements) {
                        document.add((IBlockElement) element);
                    }

                    if (contador < totalPiezas) {
                        document.add(new com.itextpdf.layout.element.AreaBreak()); // salto de página
                    }
                    contador++;
                }
            }

            document.close();
            response.put("archivo", Utils.encodeFileToBase64(archivoPdf));
            new File(archivoPdf).delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
