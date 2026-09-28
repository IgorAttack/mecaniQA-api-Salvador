package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.OrdemServico;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdemServicoRepository {

    private static OrdemServicoRepository instance;
    private List<OrdemServico> tabelaOS;
    private Long contadorId;

    private OrdemServicoRepository() {
        this.tabelaOS = new ArrayList<>();
        this.contadorId = 1L;
    }

    public static OrdemServicoRepository getInstance() {
        if (instance == null) {
            instance = new OrdemServicoRepository();
        }
        return instance;
    }

    public OrdemServico salvar(OrdemServico os) {
        if (os.getCodigoUnico() == null) {
            os.setCodigoUnico(contadorId++);
            os.setDataAbertura(LocalDateTime.now());
            this.tabelaOS.add(os);
        } else {
            deletar(os.getCodigoUnico());
            os.setDataUltimaAtualizacao(LocalDateTime.now());
            this.tabelaOS.add(os);
        }
        return os;
    }

    public Optional<OrdemServico> buscarPorId(Long id) {
        return tabelaOS.stream()
                .filter(os -> os.getCodigoUnico().equals(id))
                .findFirst();
    }

    public boolean deletar(Long id) {
        return tabelaOS.removeIf(os -> os.getCodigoUnico().equals(id));
    }
}