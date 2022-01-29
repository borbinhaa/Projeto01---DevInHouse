package banco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import conta.Conta;
import conta.TipoConta;
import exception.ValorErradoException;
import lombok.Getter;
import lombok.Setter;

public class Banco {

	@Getter
	private String nome;
	@Getter
	private List<Agencia> agencias = new ArrayList<Agencia>();
	@Getter
	private List<Conta> contas = new ArrayList<Conta>();
	@Getter
	private List<Map<String, Object>> transferencia = new ArrayList<Map<String, Object>>();
	private Scanner scanner = new Scanner(System.in);
	@Setter
	@Getter
	private double totalInvestido;

	Banco(String nome) {
		this.nome = nome;
		this.agencias.add(new Agencia("001-Florianópolis", this));
		this.agencias.add(new Agencia("002-São José", this));
	}

	public void totalInvestido() {
		System.out.printf("Total Investido = R$%.2f", totalInvestido);
	}

	public void listarTransacoesCliente() {
		System.out.println("Digite o cpf do cliente: ");
		String cpf = scanner.nextLine();
		try {
			this.validaCpf(cpf);

			String cpfFormatado = formataCpf(cpf);

			List<Conta> contasCliente = this.getContas().stream()
					.filter(account -> formataCpf(account.getCpf()).equals(cpfFormatado)).collect(Collectors.toList());

			if (contasCliente.isEmpty()) {
				throw new ValorErradoException("Cliente não encontrado");
			}

			contasCliente.forEach(e -> System.out.println(e.getExtrato()));
		} catch (ValorErradoException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void listarContasNegativas() {
		List<Conta> contasNegativas = this.contas.stream().
				filter(account -> account.getSaldo() < 0).
				collect(Collectors.toList());
		
		contasNegativas.forEach(System.out::println);
	}

	public void listarContas() {
		this.contas.forEach(System.out::println);
	}

	public void listarContasCorrentes() {
		List<Conta> lista = listaTipoConta(TipoConta.CORRENTE);

		lista.forEach(System.out::println);
	}

	public void listarContasPoupanca() {
		List<Conta> lista = listaTipoConta(TipoConta.POUPANCA);
		lista.forEach(System.out::println);
	}

	public void listarContasInvestimento() {
		List<Conta> lista = listaTipoConta(TipoConta.INVESTIMENTO);
		lista.forEach(System.out::println);
	}

	private List<Conta> listaTipoConta(TipoConta tipo) {
		return this.contas.stream().filter(conta -> conta.getTipo().equals(tipo)).collect(Collectors.toList());
	}
	

	public void historicoTransferencia() {
		System.out.println(getTransferencia());
	}

	public void registrarConta(Conta conta) {
		boolean validado = false;
		String cpf = conta.getCpf();

		while (!validado) {
			try {
				this.validaCpf(cpf);
				validado = true;
			} catch (ValorErradoException e) {
				System.out.println(e.getMessage());
				System.out.println("Tente novamente.");
				cpf = scanner.nextLine();
			}
		}

		conta.setIdConta(contas.size() + 1);
		conta.getAgencia().getContas().add(conta);
		contas.add(conta);
	}

	public boolean validaCpf(String cpf) throws ValorErradoException {
		List<Integer> inteiros = Arrays.asList(11, 10, 9, 8, 7, 6, 5, 4, 3, 2);
		String newCpf = formataCpf(cpf);
		long soma = 0, soma2 = 0;

		if (cpf.length() != 14 && cpf.length() != 11) {
			throw new ValorErradoException("CPF inválido");
		} else if (newCpf.length() != 11) {
			throw new ValorErradoException("CPF inválido");
		}

		String cpfFirstChars = newCpf.substring(0, 9);
		String firstChar = newCpf.substring(0, 1);

		if (newCpf.replaceAll(firstChar, "").length() == 0) {
			throw new ValorErradoException("CPF inválido");
		}

		for (int i = 0; i < 9; i++) {
			soma += Character.getNumericValue(cpfFirstChars.charAt(i)) * inteiros.get(i + 1);
		}

		long nextChar = getNextChar(soma);

		cpfFirstChars = cpfFirstChars.concat(Long.toString(nextChar));

		for (int i = 0; i < 10; i++) {
			soma2 += Character.getNumericValue(cpfFirstChars.charAt(i)) * inteiros.get(i);
		}

		long nextChar2 = (soma2 * 10) % 11;

		if (nextChar2 == 10) {
			nextChar2 = 0;
		}

		cpfFirstChars = cpfFirstChars.concat(Long.toString(nextChar2));

		if (!cpfFirstChars.equals(newCpf)) {
			throw new ValorErradoException("CPF inválido");
		}
		return true;
	}

	public String formataCpf(String cpf) {
		String newCpf = cpf.replaceAll("[^0-9]", "");
		return newCpf;
	}

	public void transferir(Conta conta1, Conta conta2, Double valor) {
		conta1.setSaldo(conta1.getSaldo() - valor);
		conta2.setSaldo(conta2.getSaldo() + valor);

		System.out.println("Transferencia realizada com sucesso");
	}

	public void sacar(Conta conta, Double valor) {
		conta.setSaldo(conta.getSaldo() - valor);
	}

	public void depositar(Conta conta, Double valor) {
		conta.setSaldo(conta.getSaldo() + valor);
	}

	private long getNextChar(long soma) {
		long nextChar = (soma * 10) % 11;

		if (nextChar == 10) {
			nextChar = 0;
		}

		return nextChar;
	}

}
