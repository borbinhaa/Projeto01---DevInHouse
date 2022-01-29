package conta;

import banco.Agencia;
import exception.NoSaldoException;
import exception.ValorErradoException;

public class ContaPoupanca extends Conta {

	public ContaPoupanca(String nome, String cpf, Agencia agencia) {
		super(nome, cpf, agencia);
		this.tipo = TipoConta.POUPANCA;
	}

	public void simularInvestimento() {
		try {
			if (saldo == 0) {
				throw new NoSaldoException("Não é possível simular sem saldo");
			}

			System.out.println("Informe a rentabilidade anual da poupança em porcentagem: ");
			String rentabilidadePorcentagem = scanner.nextLine();
			if (!isDoublePositive(rentabilidadePorcentagem)) {
				throw new ValorErradoException("Digite um valor numérico positivo.");
			}

			Double rentabilidadePorcentagem1 = Double.parseDouble(rentabilidadePorcentagem);
			Double rentabilidadeMensal = rentabilidadePorcentagem1 / 12;

			System.out.println("Informe a quantidade de meses: ");
			String meses = scanner.nextLine();

			if (!isLongPositive(meses)) {
				throw new ValorErradoException("Digite um valor inteiro positivo.");
			}

			long meses1 = Integer.parseInt(meses);
			Double saldo = this.getSaldo();
			Double rentabilidade = (rentabilidadeMensal / 100.0) + 1;

			for (int i = 1; i <= meses1; i++) {
				saldo = saldo * rentabilidade;
			}

			System.out.printf("R$%.2f\n", saldo);

		} catch (ValorErradoException | NoSaldoException e) {
			System.out.println(e.getMessage());
		}

	}

}
