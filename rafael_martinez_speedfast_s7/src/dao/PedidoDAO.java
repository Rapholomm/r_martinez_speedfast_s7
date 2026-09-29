package dao;

import modelo.Pedido;
import modelo.PedidoComida;
import modelo.PedidoEncomienda;
import modelo.PedidoExpress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    // Inserta un pedido nuevo y le asigna el id que MySQL generó (AUTO_INCREMENT).
    public void guardar(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedido (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, pedido.getDireccionEntrega());
            statement.setString(2, pedido.getTipo().toUpperCase());
            statement.setString(3, pedido.getEstado());
            statement.executeUpdate();

            try (ResultSet clavesGeneradas = statement.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    pedido.setIdPedido(clavesGeneradas.getInt(1));
                }
            }
        }
        // No hay catch aquí a propósito: try-with-resources ya garantiza que la conexión,
        // el statement y el resultset se cierren aunque algo falle, y la excepción se
        // deja subir (throws SQLException) para que la ventana que llamó decida qué
        // mensaje mostrarle al usuario.
    }

    // Cambia solo el estado de un pedido que ya existe (lo usa "Iniciar entrega").
    public void actualizarEstado(int idPedido, String nuevoEstado) throws SQLException {
        String sql = "UPDATE pedido SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, nuevoEstado);
            statement.setInt(2, idPedido);
            statement.executeUpdate();
        }
    }

    // Todos los pedidos guardados, para la tabla de "Listar pedidos".
    public List<Pedido> listarTodos() throws SQLException {
        return listar("SELECT id, direccion, tipo, estado FROM pedido");
    }

    // Solo los que todavía no se han despachado (los usa "Asignar repartidor").
    public List<Pedido> listarPendientes() throws SQLException {
        return listar("SELECT id, direccion, tipo, estado FROM pedido WHERE estado = 'PENDIENTE'");
    }

    private List<Pedido> listar(String sql) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                pedidos.add(reconstruirPedido(resultado));
            }
        }
        return pedidos;
    }

    // La tabla "pedido" solo guarda dirección/tipo/estado (así lo define el modelo de
    // esta actividad), así que los datos propios de cada subtipo -distancia, peso,
    // preparación- no existen en la base de datos y se recrean en 0/false. Solo importan
    // para que quede el objeto del tipo correcto (PedidoComida, PedidoEncomienda, etc.).
    private Pedido reconstruirPedido(ResultSet resultado) throws SQLException {
        int id = resultado.getInt("id");
        String direccion = resultado.getString("direccion");
        String tipo = resultado.getString("tipo");
        String estado = resultado.getString("estado");

        Pedido pedido;
        if ("COMIDA".equalsIgnoreCase(tipo)) {
            pedido = new PedidoComida(id, "", direccion, 0, false);
        } else if ("ENCOMIENDA".equalsIgnoreCase(tipo)) {
            pedido = new PedidoEncomienda(id, "", direccion, 0, 0);
        } else {
            pedido = new PedidoExpress(id, "", direccion, 0);
        }

        pedido.setEstado(estado);
        return pedido;
    }
}
