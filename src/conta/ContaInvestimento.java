package conta;

import java.util.Optional;
import banco.Agencia;
import exception.NoSaldoException;
import exception.ValorErradoException;
import lombok.Getter;
import lombok.Setter;

public class ContaInvestimento extends Conta {

	@Getter
	@Setter
	private TipoInvestimento tipoInvestimento;

	public ContaInvestimento(String nome, String cpf, Agencia agencia) {
		super(nome, cpf, agencia);
		this.tipo = TipoConta.INVESTIMENTO;
	}

	public void selecionarInvestimento() {
		try {
			TipoInvestimento[] enums = TipoInvestimento.values();
			System.out.println("Selecione o seu investimento");
			for (int i = 0; i < enums.length; i++) {
				System.out.println("(" + (i + 1) + ") - " + enums[i] + ", rentabilidade anual = "
						+ enums[i].getRentabilidadeAnual() * 100 + "%");
			}
			String investimentoKey = scanner.nextLine();

			if (!isLongPositive(investimentoKey)) {
				throw new ValorErradoException("Esse valor não corresponde a um investimento.");
			}

			switch (investimentoKey) {
			case "1": {
				this.setTipoInvestimento(enums[0]);
				System.out.println(enums[0].name() + " selecionado.");
				break;
			}
			case "2": {
				this.setTipoInvestimento(enums[1]);
				System.out.println(enums[1].name() + " selecionado.");
				break;
			}

			default:
				throw new ValorErradoException("Esse valor não corresponde a um investimento.");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void simularInvestimento() {
		try {
			if (saldo == 0) {
				throw new NoSaldoException("Não é possível simular sem saldo");
			}
			
			
			Optional<TipoInvestimento> tipo = Optional.ofNullable(this.getTipoInvestimento());

			if (tipo.isEmpty()) {
				throw new NullPointerException("Você ainda não selecionou nenhum investimento.");
			}

			System.out.println("Por quantos meses você deseja simular: ");
			String meses = scanner.nextLine();

			if (!isLongPositive(meses)) {
				throw new ValorErradoException("Digite um valor inteiro positivo.");
			}

			long meses1 = Integer.parseInt(meses);
			Double saldo = this.getSaldo();
			Double rentabilidadeMensal = (tipo.get().getRentabilidadeAnual() / 12) + 1;

			for (int i = 1; i <= meses1; i++) {
				saldo *= rentabilidadeMensal;
			}

			System.out.printf("R$%.2f\n", saldo);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
