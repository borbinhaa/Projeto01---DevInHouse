package conta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import banco.Agencia;
import exception.HorarioErradoException;
import exception.NoSaldoException;
import exception.ValorErradoException;
import lombok.Getter;
import lombok.Setter;

public class ContaCorrente extends Conta {

	@Setter
	@Getter
	private double chequeEspecial=0;

	public ContaCorrente(String nome, String cpf, Agencia agencia) {
		super(nome, cpf, agencia);
		this.tipo = TipoConta.CORRENTE;
	}

	public void habilitarChequeEspecial() {
		try {
			System.out.println("Informe sua renda mensal: ");
			String renda = scanner.nextLine();
			if (!isDoublePositive(renda)) {
				throw new ValorErradoException("Valor inválido.");
			}
			
			Double rendaDouble = Double.parseDouble(renda);
			
			this.setRendaMensal(rendaDouble);
			this.setChequeEspecial(rendaDouble / 10);
		} catch (ValorErradoException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void saque() {
		try {
			System.out.println("Digite quanto você deseja sacar: ");
			String valorSaque = this.scanner.nextLine();
			if (!isDoublePositive(valorSaque)) {
				throw new ValorErradoException("Digite um valor numérico positivo.");
			}

			Double valorSaqueDouble = Double.parseDouble(valorSaque);

			if (isNegative()) {
				throw new NoSaldoException("Seu saldo está negativo, favor depositar o suficiente para ficar positivo para poder realizar essa operação.");
			} else if (saldoInsuficienteCorrente(valorSaqueDouble)) {
				throw new NoSaldoException("Valor de saque maior que seu saldo e seu cheque especial.");
			}

			banco.sacar(this, valorSaqueDouble);
			banco.setTotalInvestido(banco.getTotalInvestido() - valorSaqueDouble);

			saldo();

			String horario = getLocalDateTimeNow();
			this.extrato.put(horario, "Saque: " + valorSaqueDouble);
		} catch (ValorErradoException | NoSaldoException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void transferir() {
		try {
			System.out.println("Digite o id da conta que vc quer transferir: ");
			String idContaTransferencia = scanner.nextLine();
			if (!isLongPositive(idContaTransferencia)) {
				throw new ValorErradoException("Digite um valor inteiro positivo.");
			}

			long idContaTransferenciaLong = Long.parseLong(idContaTransferencia);

			if (idContaTransferenciaLong == this.getIdConta()) {
				throw new ValorErradoException("Não é possível realizar uma transferência para si mesmo.");
			}

			List<Conta> contaTransferenciaLista = this.getAgencia().getBanco().getContas().stream()
					.filter(account -> account.getIdConta() == idContaTransferenciaLong).collect(Collectors.toList());

			if (contaTransferenciaLista.isEmpty()) {
				throw new ValorErradoException("Conta não encontrada.");
			}

			Conta contaTransferencia = contaTransferenciaLista.get(0);

			System.out.println("Digite o valor da transferência: ");
			String valorTransferencia = scanner.nextLine();

			if (!isDoublePositive(valorTransferencia)) {
				throw new ValorErradoException("Digite um valor numérico positivo.");
			}

			Double valorTransferenciaDouble = Double.parseDouble(valorTransferencia);

			if (isNegative()) {
				throw new NoSaldoException("Seu saldo está negativo, favor depositar o suficiente para ficar positivo para poder realizar essa operação.");
			} else if (saldoInsuficienteCorrente(valorTransferenciaDouble)) {
				throw new NoSaldoException("Valor de saque maior que seu saldo e seu cheque especial.");
			}
			
			if (!isCorrectDay()) {
				throw new HorarioErradoException("Só é possivel realizar transacoes de segunda a sexta");
			}
			
			this.getAgencia().getBanco().transferir(this, contaTransferencia, valorTransferenciaDouble);

			Map<String, Object> mapa = new HashMap<>();
			mapa.put("Conta Origem", this);
			mapa.put("Conta Destino", contaTransferencia);
			mapa.put("Valor", valorTransferencia);
			mapa.put("Data", getLocalDateTimeNow());
			
			this.extrato.put("Transferencia", mapa);
			this.getAgencia().getBanco().getTransferencia().add(mapa);
		} catch (ValorErradoException | NoSaldoException | HorarioErradoException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private boolean saldoInsuficienteCorrente(Double valor) {
		return valor > this.saldo + this.getChequeEspecial();
	}
	
	private boolean isNegative() {
		return this.getSaldo() < 0;
	}

	
}
