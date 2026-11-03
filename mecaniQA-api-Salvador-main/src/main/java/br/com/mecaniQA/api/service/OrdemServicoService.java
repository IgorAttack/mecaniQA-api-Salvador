package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.OrdemServicoDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.OrdemServicoNaoEncontradaException;
import br.com.mecaniQA.api.mapper.OrdemServicoMapper;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.model.StatusOrdemServico;
import br.com.mecaniQA.api.repository.OrdemServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository repository = OrdemServicoRepository.getInstance();

    public OrdemServicoDTO criarOS(OrdemServicoDTO osDTO) {
        validarDTO(osDTO);

        OrdemServico novaOS = OrdemServicoMapper.toEntity(osDTO);
        novaOS.setStatus(StatusOrdemServico.ABERTO);
        calcularValorTotalSeNecessario(novaOS);

        OrdemServico osSalva = repository.salvar(novaOS);
        return OrdemServicoMapper.toDTO(osSalva);
    }

    public List<OrdemServicoDTO> listarTodas() {
        return repository.buscarTodos().stream()
                .map(OrdemServicoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrdemServicoDTO buscarPorId(Long codigoUnico) {
        validarId(codigoUnico);
        OrdemServico os = repository.buscarPorId(codigoUnico)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada para o ID " + codigoUnico + "."));
        return OrdemServicoMapper.toDTO(os);
    }

    public OrdemServicoDTO atualizarStatus(Long codigoUnico, OrdemServicoDTO osDTO) {
        validarId(codigoUnico);
        if (osDTO == null || osDTO.getStatus() == null) {
            throw new DadosInvalidosException("O novo status da Ordem de Serviço é obrigatório.");
        }

        OrdemServico osExistente = repository.buscarPorId(codigoUnico)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada para o ID " + codigoUnico + "."));

        osExistente.setStatus(osDTO.getStatus());
        OrdemServico salva = repository.salvar(osExistente);
        return OrdemServicoMapper.toDTO(salva);
    }

    public OrdemServicoDTO atualizarOS(Long codigoUnico, OrdemServicoDTO osDTO) {
        validarId(codigoUnico);
        validarDTO(osDTO);

        if (repository.buscarPorId(codigoUnico).isEmpty()) {
            throw new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada para o ID " + codigoUnico + ".");
        }

        OrdemServico osAtualizada = OrdemServicoMapper.toEntity(osDTO);
        osAtualizada.setCodigoUnico(codigoUnico);
        if (osAtualizada.getStatus() == null) {
            osAtualizada.setStatus(StatusOrdemServico.ABERTO);
        }
        calcularValorTotalSeNecessario(osAtualizada);

        OrdemServico salva = repository.atualizar(codigoUnico, osAtualizada)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada para o ID " + codigoUnico + "."));
        return OrdemServicoMapper.toDTO(salva);
    }

    public void deletarOS(Long codigoUnico) {
        validarId(codigoUnico);
        if (!repository.deletar(codigoUnico)) {
            throw new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada para o ID " + codigoUnico + ".");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new DadosInvalidosException("O ID da Ordem de Serviço deve ser um número positivo.");
        }
    }

    private void validarDTO(OrdemServicoDTO osDTO) {
        if (osDTO == null || osDTO.getDescricaoProblema() == null || osDTO.getDescricaoProblema().isBlank()) {
            throw new DadosInvalidosException("A descrição do problema da Ordem de Serviço é obrigatória.");
        }
        if (osDTO.getValorTotal() != null && osDTO.getValorTotal() < 0) {
            throw new DadosInvalidosException("O valor total da Ordem de Serviço não pode ser negativo.");
        }
    }

    private void calcularValorTotalSeNecessario(OrdemServico os) {
        if (os.getValorTotal() == null || os.getValorTotal() == 0.0) {
            double totalServicos = os.getServicos() != null
                    ? os.getServicos().stream()
                    .mapToDouble(s -> s.getCustoTabelado() != null ? s.getCustoTabelado() : 0.0)
                    .sum()
                    : 0.0;

            double totalPedidos = os.getPedidosPecas() != null
                    ? os.getPedidosPecas().stream()
                    .mapToDouble(p -> p.getValorTotal() != null ? p.getValorTotal() : 0.0)
                    .sum()
                    : 0.0;

            os.setValorTotal(totalServicos + totalPedidos);
        }
    }
}