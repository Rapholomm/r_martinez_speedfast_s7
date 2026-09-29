package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Ventana de inicio. Desde acá se abren las otras ventanas. A diferencia de
 * la semana 6, ya no guarda una lista de pedidos en memoria: los datos que
 * antes se compartían por lista ahora se comparten a través de la base de
 * datos (cada DAO abre su propia conexión cuando se necesita).
 */
public class VentanaPrincipal extends JFrame {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    public VentanaPrincipal() {
        super("SpeedFast - Gestión de entregas");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);

        JLabel titulo = new JLabel("Panel de gestión de pedidos", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(titulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));

        JButton btnRegistrarPedido = new JButton("Registrar pedido");
        JButton btnRegistrarRepartidor = new JButton("Registrar repartidor");
        JButton btnListar = new JButton("Listar pedidos");
        JButton btnAsignar = new JButton("Asignar repartidor / Iniciar entrega");

        btnRegistrarPedido.addActionListener(e -> new VentanaRegistroPedido().setVisible(true));
        btnRegistrarRepartidor.addActionListener(e -> new VentanaRegistroRepartidor().setVisible(true));
        btnListar.addActionListener(e -> new VentanaListaPedidos().setVisible(true));
        btnAsignar.addActionListener(e -> iniciarEntrega());

        panelBotones.add(btnRegistrarPedido);
        panelBotones.add(btnRegistrarRepartidor);
        panelBotones.add(btnListar);
        panelBotones.add(btnAsignar);

        add(panelBotones, BorderLayout.CENTER);
    }

    // Asigna un repartidor a un pedido pendiente y registra la entrega en la base de datos:
    // 1) consulta los pedidos PENDIENTE y los repartidores guardados,
    // 2) el usuario elige uno de cada,
    // 3) se actualiza el estado del pedido a ENTREGADO y se inserta la fila en "entrega".
    private void iniciarEntrega() {
        try {
            List<Pedido> pendientes = pedidoDAO.listarPendientes();
            if (pendientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay pedidos pendientes por despachar.",
                        "Sin pedidos pendientes", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Pedido pedido = seleccionarPedido(pendientes);
            if (pedido == null) {
                return; // canceló el diálogo
            }

            List<Repartidor> repartidores = repartidorDAO.listarTodos();
            if (repartidores.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Primero registra al menos un repartidor.",
                        "Sin repartidores", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Repartidor repartidor = seleccionarRepartidor(repartidores);
            if (repartidor == null) {
                return;
            }

            pedidoDAO.actualizarEstado(pedido.getIdPedido(), "ENTREGADO");

            Entrega entrega = new Entrega(pedido.getIdPedido(), repartidor.getId(), LocalDate.now(), LocalTime.now());
            entregaDAO.guardar(entrega);

            JOptionPane.showMessageDialog(this,
                    "Pedido #" + pedido.getIdPedido() + " entregado por " + repartidor.getNombre() + ".",
                    "Entrega registrada", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un problema con la base de datos:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Pedido seleccionarPedido(List<Pedido> pendientes) {
        String[] opciones = pendientes.stream()
                .map(p -> "#" + p.getIdPedido() + " - " + p.getDireccionEntrega())
                .toArray(String[]::new);

        String elegido = (String) JOptionPane.showInputDialog(this,
                "¿Qué pedido quieres despachar?", "Seleccionar pedido",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (elegido == null) {
            return null;
        }
        int indice = Arrays.asList(opciones).indexOf(elegido);
        return pendientes.get(indice);
    }

    private Repartidor seleccionarRepartidor(List<Repartidor> repartidores) {
        return (Repartidor) JOptionPane.showInputDialog(this,
                "Elige un repartidor:", "Asignar repartidor",
                JOptionPane.QUESTION_MESSAGE, null, repartidores.toArray(), repartidores.get(0));
    }
}
