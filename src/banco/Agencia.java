package banco;

import java.util.HashSet;
import java.util.Set;
import conta.Conta;
import lombok.Getter;

public class Agencia {

	@Getter
	private String nome;
	@Getter
	private Set<Conta> contas = new HashSet<Conta>();
	@Getter
	private double valorInvestido;
	@Getter
	private Banco banco;

	Agencia(String nome, Banco banco) {
		this.nome = nome;
		this.banco = banco;
	}
}
