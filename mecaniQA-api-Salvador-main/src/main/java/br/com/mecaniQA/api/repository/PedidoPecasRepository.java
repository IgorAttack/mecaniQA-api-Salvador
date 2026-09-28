package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.PedidoPecas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoPecasRepository {

    private static PedidoPecasRepository instance;
    private List<PedidoPecas> tabelaPedidos;
    private Long contadorId;

    private PedidoPecasRepository() {
        this.tabelaPedidos = new ArrayList<>();
        this.contadorId = 1L;
    }

    public static PedidoPecasRepository getInstance() {
        if (instance == null) {
            instance = new PedidoPecasRepository();
        }
        return instance;
    }

    public PedidoPecas salvar(PedidoPecas pedido) {
        if (pedido.getCodigoUnico() == null) {
            pedido.setCodigoUnico(contadorId++);
            pedido.setDataPedido(LocalDateTime.now());
            this.tabelaPedidos.add(pedido);
        } else {
            deletar(pedido.getCodigoUnico());
            pedido.setDataUltimaAtualizacao(LocalDateTime.now());
            this.tabelaPedidos.add(pedido);
        }
        return pedido;
    }

    public Optional<PedidoPecas> buscarPorId(Long id) {
        return tabelaPedidos.stream()
                .filter(p -> p.getCodigoUnico().equals(id))
                .findFirst();
    }

    public boolean deletar(Long id) {
        return tabelaPedidos.removeIf(p -> p.getCodigoUnico().equals(id));
    }
}