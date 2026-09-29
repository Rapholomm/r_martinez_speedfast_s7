package vista;

import dao.PedidoDAO;
import modelo.Pedido;
import modelo.PedidoComida;
import modelo.PedidoEncomienda;
import modelo.PedidoExpress;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Formulario para registrar un nuevo pedido. A diferencia de la semana 6,
 * ya no guarda los pedidos en una lista en memoria: los inserta directamente
 * en la base de datos a través de PedidoDAO.
 */
public class VentanaRegistroPedido extends JFrame {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final JTextField campoCliente = new JTextField();
    private final JTextField campoDireccion = new JTextField();
    private final JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Comida", "Encomienda", "Express"});
    private final JTextField campoDistancia = new JTextField();
    private final JTextField campoPeso = new JTextField();
    private final JCheckBox checkPreparacion = new JCheckBox("Requiere preparación");

    public VentanaRegistroPedido() {
        super("Registrar nuevo pedido");

        setSize(380, 310);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelFormulario.add(new JLabel("Cliente:"));
        panelFormulario.add(campoCliente);

        panelFormulario.add(new JLabel("Dirección de entrega:"));
        panelFormulario.add(campoDireccion);

        panelFormulario.add(new JLabel("Tipo de pedido:"));
        panelFormulario.add(comboTipo);

        panelFormulario.add(new JLabel("Distancia (km):"));
        panelFormulario.add(campoDistancia);

        panelFormulario.add(new JLabel("Peso en kg (solo Encomienda):"));
        panelFormulario.add(campoPeso);

        panelFormulario.add(new JLabel("Solo Comida:"));
        panelFormulario.add(checkPreparacion);

        add(panelFormulario, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar en la base de datos");
        btnGuardar.addActionListener(e -> guardarPedido());

        JPanel panelInferior = new JPanel();
        panelInferior.add(btnGuardar);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void guardarPedido() {
        // --- Validaciones (igual que en la semana 6, pero ya sin el campo ID) ---
        String cliente = campoCliente.getText().trim();
        if (cliente.isEmpty()) {
            mostrarError("El nombre del cliente no puede quedar vacío.");
            return;
        }

        String direccion = campoDireccion.getText().trim();
        if (direccion.isEmpty()) {
            mostrarError("La dirección de entrega no puede quedar vacía.");
            return;
        }

        double distanciaKm;
        try {
            distanciaKm = Double.parseDouble(campoDistancia.getText().trim());
            if (distanciaKm < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            mostrarError("La distancia debe ser un número mayor o igual a 0.");
            return;
        }

        String tipo = (String) comboTipo.getSelectedItem();
        Pedido nuevoPedido;

        if ("Comida".equals(tipo)) {
            // El id se pone en 0 como valor de partida: todavía no existe en la base de
            // datos, así que no tiene un id real hasta que PedidoDAO.guardar() lo inserte.
            nuevoPedido = new PedidoComida(0, cliente, direccion, distanciaKm, checkPreparacion.isSelected());

        } else if ("Encomienda".equals(tipo)) {
            double pesoKg;
            try {
                pesoKg = Double.parseDouble(campoPeso.getText().trim());
                if (pesoKg < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                mostrarError("Para una encomienda, el peso debe ser un número mayor o igual a 0.");
                return;
            }
            nuevoPedido = new PedidoEncomienda(0, cliente, direccion, pesoKg, distanciaKm);

        } else { // Express
            nuevoPedido = new PedidoExpress(0, cliente, direccion, distanciaKm);
        }

        nuevoPedido.calcularTiempoEntrega(); // solo informativo: la tabla "pedido" no guarda este dato

        try {
            pedidoDAO.guardar(nuevoPedido); // acá queda con su id real, generado por MySQL

            JOptionPane.showMessageDialog(this,
                    "Pedido #" + nuevoPedido.getIdPedido() + " (" + tipo + ") guardado en la base de datos.\n"
                            + "Tiempo estimado: " + String.format("%.1f", nuevoPedido.getTiempoEntregaEstimado()) + " min.",
                    "Pedido guardado", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();

        } catch (SQLException ex) {
            mostrarError("No se pudo guardar el pedido en la base de datos:\n" + ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos inválidos", JOptionPane.ERROR_MESSAGE);
    }

    private void limpiarFormulario() {
        campoCliente.setText("");
        campoDireccion.setText("");
        campoDistancia.setText("");
        campoPeso.setText("");
        checkPreparacion.setSelected(false);
        comboTipo.setSelectedIndex(0);
        campoCliente.requestFocus();
    }
}