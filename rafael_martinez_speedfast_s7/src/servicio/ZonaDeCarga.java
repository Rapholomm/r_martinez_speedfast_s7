package servicio;

import modelo.Pedido;

import java.util.LinkedList;
import java.util.Queue;

public class ZonaDeCarga {

    private final Queue<Pedido> pedidosPendientes = new LinkedList<>();

    public synchronized void agregarPedido(Pedido pedido) {
        pedidosPendientes.add(pedido);
        System.out.println("Pedido #" + pedido.getIdPedido() + " agregado. Destino: "
                + pedido.getDireccionEntrega());
    }


    // quede marcado como EN_REPARTO.
    public synchronized Pedido retirarPedido() {
        Pedido pedido = pedidosPendientes.poll();
        if (pedido != null) {
            pedido.setEstado("EN_REPARTO");
        }
        return pedido; // null cuando ya no quedan pedidos por retirar
    }

    public synchronized boolean estaVacia() {
        return pedidosPendientes.isEmpty();
    }
}

