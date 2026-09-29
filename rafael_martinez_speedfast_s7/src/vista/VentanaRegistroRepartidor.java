package vista;

import dao.RepartidorDAO;
import modelo.Repartidor;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;


public class VentanaRegistroRepartidor extends JFrame {

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final JTextField campoNombre = new JTextField();

    public VentanaRegistroRepartidor() {
        super("Registrar repartidor");

        setSize(320, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelFormulario = new JPanel(new GridLayout(1, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(campoNombre);
        add(panelFormulario, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar en la base de datos");
        btnGuardar.addActionListener(e -> guardarRepartidor());

        JPanel panelInferior = new JPanel();
        panelInferior.add(btnGuardar);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void guardarRepartidor() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede quedar vacío.",
                    "Datos inválidos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Repartidor repartidor = new Repartidor(nombre);
            repartidorDAO.guardar(repartidor);

            JOptionPane.showMessageDialog(this,
                    "Repartidor #" + repartidor.getId() + " (" + nombre + ") guardado en la base de datos.",
                    "Repartidor guardado", JOptionPane.INFORMATION_MESSAGE);

            campoNombre.setText("");
            campoNombre.requestFocus();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el repartidor en la base de datos:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
