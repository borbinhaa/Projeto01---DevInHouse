package conta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import banco.Agencia;
import banco.Banco;
import exception.HorarioErradoException;
import exception.NoSaldoException;
import exception.ValorErradoException;
import lombok.Getter;
import lombok.Setter;

public abstract class Conta {

	@Getter
	protected String nome;
	@Getter
	protected String cpf;
	@Getter
	@Setter
	protected long idConta;
	@Getter
	protected Agencia agencia;
	protected Banco banco;
	@Getter
	protected TipoConta tipo;
	@Getter
	@Setter
	protected double saldo;
	@Getter
	@Setter
	protected double rendaMensal;
	protected Scanner scanner = new Scanner(System.in);
	@Getter
	protected Map<String, Object> extrato = new HashMap<String, Object>();

	public Conta(String nome, String cpf, Agencia agencia) {
		this.nome = nome;
		this.cpf = cpf;
		this.agencia = agencia;
		this.banco = agencia.getBanco();
	}

	public void saque() {
		try {
			System.out.println("Digite quanto você deseja sacar: ");
			String valorSaque = this.scanner.nextLine();
			if (!isDoublePositive(valorSaque)) {
				throw new ValorErradoException("Digite um valor numérico positivo.");
			}

			Double valorSaqueDouble = Double.parseDouble(valorSaque);

			if (saldoInsuficiente(valorSaqueDouble)) {
				throw new NoSaldoException("Valor de saque maior que seu saldo.");
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

	public void deposito() {
		try {
			System.out.println("Digite quanto você deseja depositar: ");
			String valorDeposito = this.scanner.nextLine();
			if (!isDoublePositive(valorDeposito)) {
				throw new ValorErradoException("Digite um valor numérico positivo.");
			}

			Double valorDepositoDouble = Double.parseDouble(valorDeposito);

			banco.depositar(this, valorDepositoDouble);
			banco.setTotalInvestido(banco.getTotalInvestido() + valorDepositoDouble);

			saldo();
			String horario = getLocalDateTimeNow();
			this.extrato.put(horario, "Deposito: " + valorDepositoDouble);
		} catch (ValorErradoException e) {
			System.out.println(e.getMessage());
		}
	}

	public void saldo() {
		System.out.println("Seu saldo atual é de: R$" + this.saldo + ".");
	}

	public void extrato() {
		for (String key : this.extrato.keySet()) {
			System.out.println(key + " - " + this.extrato.get(key));
		}
	}

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

			if (saldoInsuficiente(valorTransferenciaDouble)) {
				throw new NoSaldoException("Valor de transferência maior que seu saldo.");
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

	public void alterarDados() {
		System.out.println("Que dados vc deseja apagar?\n(1) - Nome\n(2) - Agencia");
		String dado = scanner.nextLine();
		try {
			switch (dado) {
			case "1": {
				System.out.println("Digite seu novo nome: ");
				String novoDado = scanner.nextLine();
				this.nome = novoDado;

				System.out.println("Seu novo nome é " + this.nome + ".");
				break;
			}
			case "2": {
				Agencia agenciaFloripa = this.agencia.getBanco().getAgencias().get(0);
				Agencia agenciaSJ = this.agencia.getBanco().getAgencias().get(1);

				if (this.agencia.equals(agenciaFloripa)) {
					this.agencia = agenciaSJ;
				} else {
					this.agencia = agenciaFloripa;
				}

				System.out.println("Agência nova = " + this.agencia.getNome() + ".");
				break;
			}
			default:
				throw new ValorErradoException("Por favor digite um valor valido.");
			}
		} catch (ValorErradoException e) {
			System.out.println(e.getMessage());
		}
	}

	protected static boolean isDoublePositive(String value) {
		try {
			Double value2 = Double.parseDouble(value);
			if (value2 < 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	protected static boolean isLongPositive(String value) {
		try {
			Long value2 = Long.parseLong(value);
			if (value2 < 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	protected String getLocalDateTimeNow() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return now.format(formatter);
	}

	protected boolean isCorrectDay() {
		GregorianCalendar gc = new GregorianCalendar();
		int dayWeek = gc.get(Calendar.DAY_OF_WEEK);
		if (dayWeek == 1 || dayWeek == 6) {
			return false;
		}
		return true;

	}

	private boolean saldoInsuficiente(Double valor) {
		return valor > this.saldo;
	}

	@Override
	public String toString() {
		return "[nome=" + nome + ", cpf=" + cpf + ", idConta=" + idConta + ", agencia=" + agencia.getNome() + "]";
	}
}
