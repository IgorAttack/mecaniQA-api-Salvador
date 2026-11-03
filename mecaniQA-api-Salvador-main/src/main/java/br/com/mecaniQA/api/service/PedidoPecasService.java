package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.ItemPedidoPecaDTO;
import br.com.mecaniQA.api.dto.PedidoPecasDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.PecaNaoEncontradaException;
import br.com.mecaniQA.api.exception.PedidoNaoEncontradoException;
import br.com.mecaniQA.api.mapper.PedidoPecasMapper;
import br.com.mecaniQA.api.model.ItemPedidoPeca;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedidoPecas;
import br.com.mecaniQA.api.repository.PecaRepository;
import br.com.mecaniQA.api.repository.PedidoPecasRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoPecasService {

    private final PedidoPecasRepository repository = PedidoPecasRepository.getInstance();
    private final PecaRepository pecaRepository = PecaRepository.getInstance();

    public PedidoPecasDTO criarPedido(PedidoPecasDTO pedidoDTO) {
        if (pedidoDTO == null || pedidoDTO.getFornecedor() == null || pedidoDTO.getFornecedor().isBlank()) {
            throw new DadosInvalidosException("O fornecedor do pedido é obrigatório.");
        }

        if (pedidoDTO.getItens() != null) {
            for (ItemPedidoPecaDTO item : pedidoDTO.getItens()) {
                validarItemDTO(item);
            }
        }

        PedidoPecas novoPedido = PedidoPecasMapper.toEntity(pedidoDTO);
        novoPedido.setStatus(StatusPedidoPecas.ORCANDO);
        recalcularValorTotalSeZero(novoPedido);

        PedidoPecas salvo = repository.salvar(novoPedido);
        return PedidoPecasMapper.toDTO(salvo);
    }

    public List<PedidoPecasDTO> listarTodos() {
        return repository.buscarTodos().stream()
                .map(PedidoPecasMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PedidoPecasDTO buscarPorId(Long codigoUnico) {
        validarId(codigoUnico);
        PedidoPecas pedido = repository.buscarPorId(codigoUnico)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido de peças não encontrado para o ID " + codigoUnico + "."));
        return PedidoPecasMapper.toDTO(pedido);
    }

    public PedidoPecasDTO adicionarPecasAoPedido(Long codigoUnico, List<ItemPedidoPecaDTO> novasPecas) {
        validarId(codigoUnico);
        if (novasPecas == null || novasPecas.isEmpty()) {
            throw new DadosInvalidosException("A lista de peças para adicionar ao pedido não pode ser vazia.");
        }

        PedidoPecas pedidoExistente = repository.buscarPorId(codigoUnico)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido de peças não encontrado para o ID " + codigoUnico + "."));

        for (ItemPedidoPecaDTO dto : novasPecas) {
            Peca pecaReal = validarItemDTO(dto);
            Double precoUnitario = dto.getPrecoUnitario() != null ? dto.getPrecoUnitario() : pecaReal.getPrecoVenda();
            ItemPedidoPeca novoItem = new ItemPedidoPeca(pedidoExistente, pecaReal, dto.getQuantidade(), precoUnitario);
            pedidoExistente.getItens().add(novoItem);
        }

        recalcularValorTotal(pedidoExistente);
        PedidoPecas salvo = repository.salvar(pedidoExistente);
        return PedidoPecasMapper.toDTO(salvo);
    }

    public PedidoPecasDTO atualizarStatusPedido(Long codigoUnico, PedidoPecasDTO pedidoDTO) {
        validarId(codigoUnico);
        if (pedidoDTO == null || pedidoDTO.getStatus() == null) {
            throw new DadosInvalidosException("O novo status do pedido é obrigatório.");
        }

        PedidoPecas pedidoExistente = repository.buscarPorId(codigoUnico)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido de peças não encontrado para o ID " + codigoUnico + "."));

        pedidoExistente.setStatus(pedidoDTO.getStatus());
        PedidoPecas salvo = repository.salvar(pedidoExistente);
        return PedidoPecasMapper.toDTO(salvo);
    }

    public PedidoPecasDTO atualizarPedido(Long codigoUnico, PedidoPecasDTO pedidoDTO) {
        validarId(codigoUnico);
        if (pedidoDTO == null || pedidoDTO.getFornecedor() == null || pedidoDTO.getFornecedor().isBlank()) {
            throw new DadosInvalidosException("O fornecedor do pedido é obrigatório.");
        }

        if (repository.buscarPorId(codigoUnico).isEmpty()) {
            throw new PedidoNaoEncontradoException("Pedido de peças não encontrado para o ID " + codigoUnico + ".");
        }

        if (pedidoDTO.getItens() != null) {
            for (ItemPedidoPecaDTO item : pedidoDTO.getItens()) {
                validarItemDTO(item);
            }
        }

        PedidoPecas entidade = PedidoPecasMapper.toEntity(pedidoDTO);
        entidade.setCodigoUnico(codigoUnico);
        if (entidade.getStatus() == null) {
            entidade.setStatus(StatusPedidoPecas.ORCANDO);
        }
        recalcularValorTotalSeZero(entidade);

        PedidoPecas atualizado = repository.atualizar(codigoUnico, entidade)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido de peças não encontrado para o ID " + codigoUnico + "."));
        return PedidoPecasMapper.toDTO(atualizado);
    }

    public void deletarPedido(Long codigoUnico) {
        validarId(codigoUnico);
        if (!repository.deletar(codigoUnico)) {
            throw new PedidoNaoEncontradoException("Pedido de peças não encontrado para o ID " + codigoUnico + ".");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new DadosInvalidosException("O ID do pedido deve ser um número positivo.");
        }
    }

    private Peca validarItemDTO(ItemPedidoPecaDTO item) {
        if (item == null || item.getPecaId() == null || item.getPecaId() <= 0) {
            throw new DadosInvalidosException("O ID da peça no item do pedido é obrigatório e deve ser positivo.");
        }
        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            throw new DadosInvalidosException("A quantidade de cada item do pedido deve ser maior que zero.");
        }
        if (item.getPrecoUnitario() != null && item.getPrecoUnitario() < 0) {
            throw new DadosInvalidosException("O preço unitário não pode ser negativo.");
        }
        return pecaRepository.buscarPorId(item.getPecaId())
                .orElseThrow(() -> new PecaNaoEncontradaException("Peça não encontrada para o ID " + item.getPecaId() + "."));
    }

    private void recalcularValorTotalSeZero(PedidoPecas pedido) {
        if ((pedido.getValorTotal() == null || pedido.getValorTotal() == 0.0)
                && pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            recalcularValorTotal(pedido);
        }
    }

    private void recalcularValorTotal(PedidoPecas pedido) {
        double soma = pedido.getItens().stream()
                .mapToDouble(i -> (i.getQuantidade() != null ? i.getQuantidade() : 0)
                        * (i.getPrecoUnitario() != null ? i.getPrecoUnitario() : 0.0))
                .sum();
        pedido.setValorTotal(soma);
    }
}