package vista;

import dao.PedidoDAO;
import modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Muestra en una tabla los pedidos guardados en la base de datos.
 * A diferencia de la semana 6, ya no recibe una lista compartida en memoria:
 * cada vez que se refresca, vuelve a consultar la tabla "pedido" con PedidoDAO.
 */
public class VentanaListaPedidos extends JFrame {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final DefaultTableModel modeloTabla;

    // Solo estas 4 columnas: son las únicas que la tabla "pedido" guarda de verdad.
    private static final String[] COLUMNAS = {"ID", "Dirección", "Tipo", "Estado"};

    public VentanaListaPedidos() {
        super("Listado de pedidos (desde la base de datos)");

        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false; // la tabla es solo de lectura
            }
        };

        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> refrescarTabla());

        JPanel panelInferior = new JPanel();
        panelInferior.add(btnActualizar);
        add(panelInferior, BorderLayout.SOUTH);

        refrescarTabla(); // carga los datos apenas se abre la ventana
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0); // limpia las filas anteriores antes de volver a cargar

        try {
            List<Pedido> pedidos = pedidoDAO.listarTodos();
            for (Pedido pedido : pedidos) {
                modeloTabla.addRow(new Object[]{
                        pedido.getIdPedido(),
                        pedido.getDireccionEntrega(),
                        pedido.getTipo(),
                        pedido.getEstado()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar la base de datos:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
