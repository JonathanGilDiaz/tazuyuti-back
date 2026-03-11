/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Font;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Boleto;
import com.example.tazuyuti_back.entities.modules.DetalleBoleto;
import com.example.tazuyuti_back.entities.modules.DetalleEquipajeBoleto;
import com.example.tazuyuti_back.entities.modules.DetallePaquete;
import com.example.tazuyuti_back.entities.modules.DetalleVenta;
import com.example.tazuyuti_back.entities.modules.Paquete;
import com.example.tazuyuti_back.entities.modules.Venta;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import java.util.Set;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Collections;
import java.util.Date;

@Service
public class DocumentHelper {

    @Value("${files.upload-directory-documento}")
    private String directoryDocumento;

    @Value("${files.upload-directory-imagenes:uploads/documentos/}")
    private String directoryImages;

    @Value("${files.upload-directory-reporteExcel}")
    private String directoryExcel;

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
            document.setMargins(5, 15, 5, 15);
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

    public Map<String, Object> createTicketVentaFactura(Venta venta) {
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

                    + "<div style='margin:4px 0; font-size:18px; font-weight:900;'>FOLIO: " + venta.getFolio()
                    + "</div>"
                    + "<div style='margin:4px 0; font-size:12px; font-weight:700;'>CLIENTE: "
                    + venta.getCliente().getNombre() + " " + venta.getCliente().getApellidoPaterno() + " "
                    + venta.getCliente().getApellidoMaterno()
                    + "</div>"

                    + "<table style='width:100%; font-size:12px; text-align:center; border-collapse:collapse;'>"
                    + "<thead><tr><th style='border-bottom:1px solid #000;'>Cant</th>"
                    + "<th style='border-bottom:1px solid #000;'>Código</th>"
                    + "<th style='border-bottom:1px solid #000;'>Desc</th>"
                    + "<th style='border-bottom:1px solid #000;'>Precio</th>"
                    + "<th style='border-bottom:1px solid #000;'>Importe</th></tr></thead>"
                    + "<tbody>" + productosHtml.toString() + "</tbody></table>"

                    + "<hr/>"

                    + "<div style='text-align:right; font-size:13px; font-weight:900;'>SUBTOTAL: $"
                    + String.format("%.2f", venta.getTotal()) + "</div>";

            html += "<p style='text-align:right;'>IVA: $" + String.format("%.2f", venta.getTotal()*.16) + "</p>"
                    + "<p style='text-align:right;'>TOTAL: $" + String.format("%.2f", (venta.getTotal()*.16)+venta.getTotal()) + "</p>";

            html += "<br/><p style='margin-top:10px;'>*** Gracias por su compra ***</p>"
                    + "</body></html>";
            PdfWriter writer = new PdfWriter(archivoPdf);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(new PageSize(226, 800));
            Document document = new Document(pdfDoc);
            document.setMargins(5, 15, 5, 15);
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
                    String desc = det.getConcepto() != null ? det.getConcepto() : "Concepto";
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
            document.setMargins(5, 15, 5, 15);

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
            document.setMargins(5, 15, 5, 15);

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
                            + "<td style='border:1px solid #000; padding:5px;'>" + det.getConcepto()
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

    public Map<String, Object> createTicketBoleto(Boleto boleto) {
        Map<String, Object> response = new HashMap<>();
        try {
            DateTimeFormatter fmtNombre = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
            String nombrePdf = "ticket_boleto_" + (boleto.getFolio() != null ? boleto.getFolio().trim() : "SN")
                    + "_" + LocalDateTime.now().format(fmtNombre) + ".pdf";
            String archivoPdf = directoryDocumento + nombrePdf;

            File f = new File(archivoPdf);
            if (f.exists())
                f.delete();

            String logoBase64 = "data:" + Utils.getMime("png") + ";base64," +
                    Utils.encodeFileToBase64Binary(directoryImages + "logo_ticket.png");

            String sucursalNombre = boleto.getUsuario() != null && boleto.getUsuario().getSucursal() != null
                    ? boleto.getUsuario().getSucursal().getNombre()
                    : "";
            String sucursalHorario = boleto.getUsuario() != null && boleto.getUsuario().getSucursal() != null
                    ? boleto.getUsuario().getSucursal().getHorario()
                    : "";
            String sucursalDir = boleto.getUsuario() != null && boleto.getUsuario().getSucursal() != null
                    ? boleto.getUsuario().getSucursal().getDireccion()
                    : "";
            String sucursalTel = boleto.getUsuario() != null && boleto.getUsuario().getSucursal() != null
                    ? boleto.getUsuario().getSucursal().getTelefono()
                    : "";

            String fechaStr = boleto.getFechaCreacion() != null
                    ? boleto.getFechaCreacion().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"))
                    : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"));
            StringBuilder conceptos = new StringBuilder();
            for (DetalleBoleto det : boleto.getDetalleBoletos()) {
                String tipoB = "";
                if (det.isNino()) {
                    tipoB = "Boleto Niño";
                } else {
                    tipoB = "Boleto Adulto";
                }
                conceptos.append("<tr>")
                        .append("<td>").append(det.getCantidad()).append("</td>")
                        .append("<td>").append(tipoB).append("</td>")
                        .append("<td>$").append(String.format("%.2f", det.getPrecio())).append("</td>")
                        .append("<td>$").append(String.format("%.2f", det.getSubtotal())).append("</td>")
                        .append("</tr>");
            }
            for (DetalleEquipajeBoleto det : boleto.getDetalleEquipajeBoleto()) {
                conceptos.append("<tr>")
                        .append("<td>").append(det.getCantidad()).append("</td>")
                        .append("<td>").append(det.getPrecioEquipaje().getNombre()).append("</td>")
                        .append("<td>$").append(String.format("%.2f", det.getPrecio())).append("</td>")
                        .append("<td>$").append(String.format("%.2f", det.getSubtotal())).append("</td>")
                        .append("</tr>");
            }
            java.util.function.Function<Double, String> money = v -> "$"
                    + String.format(Locale.US, "%,.2f", (v != null ? v : 0d));
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
                    .append("<span>Folio: <b>").append(escapeHtml(boleto.getFolio())).append("</b></span>")
                    .append("</div>")

                    // Datos envío
                    .append("<div style='text-align:left; margin-top:6px;'>")
                    .append("<div>Atendió: ").append(escapeHtml(boleto.getUsuario().getNombre())).append("</div>")
                    .append("<div>Viaje: ").append(escapeHtml(boleto.getViaje())).append("</div>")
                    .append("<div>Salida: ").append(escapeHtml(boleto.getFechaSalida().toString())).append("</div>")
                    .append("<div>Asientos: ").append(escapeHtml(boleto.getAsientos())).append("</div>")
                    .append("<div>Titular: ").append(escapeHtml(boleto.getCliente())).append("</div>")
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
                    .append(money.apply(boleto.getTotal())).append("</div>");

            // Si fue efectivo, mostrar pago y cambio
            String fp = boleto.getFormaPago() != null ? boleto.getFormaPago() : "";
            if (fp.toLowerCase().contains("efectivo")) {
                html.append("<p style='text-align:right;'>Pago: ").append(money.apply(boleto.getPago())).append("</p>")
                        .append("<p style='text-align:right;'>Cambio: ").append(money.apply(boleto.getCambio()))
                        .append("</p>");
            }

            html.append("<br/><p style='margin-top:10px;'>*** Gracias por su preferencia ***</p>")
                    .append("</body></html>");

            // ---------- PDF ----------
            PdfWriter writer = new PdfWriter(archivoPdf);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(new PageSize(226, 600));
            Document document = new Document(pdfDoc);
            document.setMargins(5, 15, 5, 15);

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

    public void ReporteExcels(String filePath, String titulo, User usuario,
            List<Map.Entry<String, Boolean>> encabezados,
            List<List<Object>> datos) throws IOException {
        String fileUrl = directoryExcel + "reporteBase.xlsx";
        File file = new File(fileUrl);
        if (!file.exists()) {
            throw new FileNotFoundException("El archivo no existe en la ruta: " + fileUrl);
        }
        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row rowTitulo = sheet.getRow(2);
            if (rowTitulo == null)
                rowTitulo = sheet.createRow(2);
            Cell cellTitulo = rowTitulo.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cellTitulo.setCellValue(safeValue(titulo));
            Row rowUsuario = sheet.getRow(4);
            if (rowUsuario == null)
                rowUsuario = sheet.createRow(4);
            Cell cellUsuario = rowUsuario.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cellUsuario.setCellValue(usuario.getNombre());
            Cell cellFecha = rowUsuario.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            cellFecha.setCellValue(fechaActual);
            Row filaEncabezados = sheet.getRow(6);
            if (filaEncabezados == null)
                filaEncabezados = sheet.createRow(6);
            CellStyle estiloEncabezado = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            estiloEncabezado.setFont(font);
            estiloEncabezado.setAlignment(HorizontalAlignment.CENTER);
            estiloEncabezado.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloEncabezado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloEncabezado.setBorderTop(BorderStyle.THIN);
            estiloEncabezado.setBorderBottom(BorderStyle.THIN);
            estiloEncabezado.setBorderLeft(BorderStyle.THIN);
            estiloEncabezado.setBorderRight(BorderStyle.THIN);
            for (int i = 0; i < encabezados.size(); i++) {
                Cell celda = filaEncabezados.createCell(i + 1);
                celda.setCellValue(encabezados.get(i).getKey());
                celda.setCellStyle(estiloEncabezado);
            }
            Set<Integer> columnasNumericasValidas = new HashSet<>();
            for (int j = 0; j < encabezados.size(); j++) {
                if (!encabezados.get(j).getValue())
                    continue;
                boolean todosNumericos = true;
                for (List<Object> fila : datos) {
                    if (j >= fila.size() || !(fila.get(j) instanceof Number)) {
                        todosNumericos = false;
                        break;
                    }
                }
                if (todosNumericos)
                    columnasNumericasValidas.add(j);
            }
            int rowStart = 7;
            int colStart = 1;
            Map<Integer, Double> sumas = new HashMap<>();
            SimpleDateFormat formatterFecha = new SimpleDateFormat("dd/MM/yyyy");
            for (int i = 0; i < datos.size(); i++) {
                Row fila = sheet.getRow(rowStart + i);
                if (fila == null)
                    fila = sheet.createRow(rowStart + i);
                List<Object> filaDatos = datos.get(i);
                for (int j = 0; j < filaDatos.size(); j++) {
                    Object valor = filaDatos.get(j);
                    Cell celda = fila.createCell(colStart + j);
                    if (valor instanceof Number) {
                        double val = ((Number) valor).doubleValue();
                        celda.setCellValue(val);
                        celda.setCellStyle(createDecimalStyle(workbook));
                        if (columnasNumericasValidas.contains(j)) {
                            sumas.put(j, sumas.getOrDefault(j, 0.0) + val);
                        }
                    } else if (valor instanceof LocalDate) {
                        LocalDate localDate = (LocalDate) valor;
                        celda.setCellValue(formatterFecha.format(java.sql.Date.valueOf(localDate)));
                        celda.setCellStyle(createBorderStyle(workbook));
                    } else if (valor instanceof Date) {
                        celda.setCellValue(formatterFecha.format((Date) valor));
                        celda.setCellStyle(createBorderStyle(workbook));
                    } else {
                        celda.setCellValue(safeValue(valor != null ? valor.toString() : ""));
                        celda.setCellStyle(createBorderStyle(workbook));
                    }
                }
            }
            if (!sumas.isEmpty()) {
                int filaTotal = rowStart + datos.size();
                Row fila = sheet.createRow(filaTotal);
                int primerColumnaSuma = Collections.min(sumas.keySet());
                Cell celdaTexto = fila.createCell(colStart + primerColumnaSuma - 1);
                celdaTexto.setCellValue("Total:");
                celdaTexto.setCellStyle(createBorderStyle(workbook));
                for (Map.Entry<Integer, Double> entry : sumas.entrySet()) {
                    Cell celdaSuma = fila.createCell(colStart + entry.getKey());
                    celdaSuma.setCellValue(entry.getValue());
                    celdaSuma.setCellStyle(createCurrencyStyle(workbook));
                }
            }
            for (int i = 0; i < encabezados.size() + 2; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, (int) (currentWidth * 1.15));
            }
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    public String safeValue(String value) {
        return value != null ? value.trim() : "";
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createBorderStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        return style;
    }

    private CellStyle createBorderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDecimalStyle(Workbook workbook) {
        CellStyle style = createBorderStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#0.00"));
        return style;
    }
}
