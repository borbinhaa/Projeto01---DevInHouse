package conta;

import lombok.Getter;

public enum TipoInvestimento {

	SELIC(0.09), ACOES(0.12);
	
	@Getter
	private Double rentabilidadeAnual;

	TipoInvestimento(Double rentabilidadeAnual) {
		this.rentabilidadeAnual = rentabilidadeAnual;
	}
	
	
}
