package com.ferreteria.ferreteria.controller;


import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.ferreteria.ferreteria.model.Clientes;
import com.ferreteria.ferreteria.model.repository.ClientesRepository;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller

public class VistaClientes {
    @Autowired
    ClientesRepository clientesRepository;

    @GetMapping("vista/clientes")
    public String getAll(Model model) {
        model.addAttribute("clientes", clientesRepository.findAll());
        return "clientes";
    }

    @GetMapping("/vistaC/form")
    public String form(Model model)
    {
        model.addAttribute("cliente", new Clientes());
        return "clientes_form";
    }

    @PostMapping("/vistaC/save")
    public String save(@ModelAttribute Clientes clientes, RedirectAttributes ra)
    {
        clientesRepository.save(clientes);
        ra.addFlashAttribute("mensaje", "Cliente guardado correctamente");
        return "redirect:/vista/clientes";
    }

    @GetMapping("/vistaC/edit/{id}")
    public String edit(@PathVariable Long id, Model model)

    {
        Clientes clientes = clientesRepository.findById(id).orElse(null);
        model.addAttribute("cliente", clientes);
        return "clientes_form";
    }

    @PostMapping("/vistaC/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra)

    {
        clientesRepository.deleteById(id);
        ra.addFlashAttribute("mensaje", "Cliente eliminado correctamente");
        return "redirect:/vista/clientes";
    }

    @GetMapping("/vistaC/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception
    {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Clientes.pdf");

        List<Clientes> ClientesList = clientesRepository.findAll(); // Asegúrate de tener fichaRepository

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Lista de Clientes"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5); // ajusta las columnas necesarias
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // Encabezados
        table.addCell("ID Cliente");
        table.addCell("Nombre");
        table.addCell("Telefono");
        table.addCell("Direccion");
        table.addCell("Correo");

        // Filas
        for (Clientes f : ClientesList) {
            table.addCell(f.getId_cliente().toString());
            table.addCell(f.getNombre().toString());
            table.addCell(f.getTelefono());
            table.addCell(f.getDireccion());
            table.addCell(f.getCorreo());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/vistaC/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Clientes.xlsx");

        List<Clientes> clientesList = clientesRepository.findAll(); // Reemplaza con tu repositorio

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clientes");

        // Crear encabezado
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID Cliente");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Telefono");
        headerRow.createCell(3).setCellValue("Direccion");
        headerRow.createCell(4).setCellValue("Correo");

        // Agregar datos
        int rowNum = 1;
        for (Clientes clientes : clientesList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(clientes.getId_cliente());
            row.createCell(1).setCellValue(clientes.getNombre());
            row.createCell(2).setCellValue(clientes.getTelefono());
            row.createCell(3).setCellValue(clientes.getDireccion());
            row.createCell(4).setCellValue(clientes.getCorreo());
        }

        // Autoajustar columnas
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}

