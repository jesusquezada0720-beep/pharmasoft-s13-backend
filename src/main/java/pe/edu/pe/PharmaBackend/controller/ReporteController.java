package pe.edu.pe.PharmaBackend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.pe.PharmaBackend.reportes.dto.ProductoMasVendidoDTO;
import pe.edu.pe.PharmaBackend.reportes.service.ReporteVentasService;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
@PreAuthorize("hasAuthority('REPORTES_VER')")
public class ReporteController {
    private final ReporteVentasService reporteVentasService;

    public ReporteController(ReporteVentasService reporteVentasService) {
        this.reporteVentasService = reporteVentasService;
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> productosMasVendidos() {
        return ResponseEntity.ok(reporteVentasService.productosMasVendidos());
    }
}
