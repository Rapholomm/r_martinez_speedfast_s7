package servicio;

import interfaces.Rastreable;
import modelo.Pedido;
import java.util.ArrayList;
import java.util.List;

public class ControladorDeEnvios implements Rastreable {

    private final List<Pedido> historialEntregas;

    public ControladorDeEnvios() {
        historialEntregas = new ArrayList<>();
    }

    // Despacha un pedido de Despachable y lo agrega al historial
    public void despacharPedido(Pedido pedido) {
        pedido.despachar();
        registrarEnHistorial(pedido);
    }



    // Cancela un pedido de Cancelable y lo agrea al historial
    public void cancelarPedido(Pedido pedido) {
        pedido.cancelar();
        registrarEnHistorial(pedido);
    }

    // synchronized: evita que dos repartidores (hilos distintos) modifiquen el hisotrial de entrega al mismo tiempo

    private synchronized void registrarEnHistorial(Pedido pedido) {
        if (!historialEntregas.contains(pedido)) {
            historialEntregas.add(pedido);
        }
    }

    // desde aqui se implementa restreable y se muestra el resumen de lo s pedidos que esten  en el historial
    @Override
    public synchronized void verHistorial() {
        System.out.println("\n===== HISTORIAL DE ENTREGAS =====");
        if (historialEntregas.isEmpty()) {
            System.out.println("   No hay pedidos registrados todavía.");
            return;
        }
        for (Pedido pedido : historialEntregas) {
            pedido.mostrarResumen();
        }
        System.out.println("==================================");
    }
}

