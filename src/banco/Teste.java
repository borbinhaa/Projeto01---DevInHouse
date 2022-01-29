package banco;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import conta.ContaCorrente;
import conta.ContaInvestimento;
import conta.ContaPoupanca;
import exception.HorarioErradoException;

public class Teste {
	
	public static void main(String[] args) {
		
		Banco banco = new Banco("DevInBanco");
		
		Agencia agenciaFloripa = banco.getAgencias().get(0);
		Agencia agenciaSJ = banco.getAgencias().get(1);
		
		ContaCorrente conta1 = new ContaCorrente("Pedro", "940.225.120-08", agenciaFloripa);
		ContaCorrente conta2 = new ContaCorrente("Maria", "55055593016", agenciaSJ);
		ContaCorrente conta3 = new ContaCorrente("Antonio", "53626674088", agenciaSJ);
		ContaPoupanca conta4 = new ContaPoupanca("Rafa", "79517203055", agenciaSJ);
		ContaInvestimento conta5 = new ContaInvestimento("Julia", "915.257.744-92", agenciaSJ);
	
		banco.registrarConta(conta1);
		System.out.println(conta1.getIdConta());
		banco.registrarConta(conta2);
		System.out.println(conta2.getIdConta());
		banco.registrarConta(conta3);
		System.out.println(conta3.getIdConta());
		banco.registrarConta(conta4);
		System.out.println(conta4.getIdConta());
		banco.registrarConta(conta5);
		System.out.println(conta5.getIdConta());
		
		banco.listarContas();
		System.out.println();
		banco.listarContasCorrentes();
		System.out.println();
		banco.listarContasInvestimento();
		System.out.println();
		banco.listarContasPoupanca();
		System.out.println();
		
		System.out.println("------TESTE CONTA---------");
				
		conta1.deposito();
		conta2.deposito();
		conta3.deposito();
//		System.out.println();
//		conta1.saldo();
		System.out.println();
		conta1.saque();
		System.out.println();
//		conta1.alterarDados();
//		System.out.println();
		conta1.transferir();
		conta2.transferir();
		conta3.transferir();
		System.out.println();
		banco.historicoTransferencia();
		conta2.deposito();
		System.out.println();
		conta4.deposito();
		System.out.println();
		System.out.println();
		banco.listarTransacoesCliente();
		conta4.deposito();
		System.out.println();
		conta4.simularInvestimento();
		System.out.println();
		conta5.deposito();
		System.out.println();
		conta5.selecionarInvestimento();
		System.out.println();
		conta5.simularInvestimento();
		System.out.println();
		conta5.saldo();
		System.out.println();
		conta5.extrato();
		banco.totalInvestido();
		conta1.habilitarChequeEspecial();
		conta2.habilitarChequeEspecial();
		conta1.deposito();
		conta2.deposito();
		System.out.println(conta1.getChequeEspecial());
		conta1.deposito(); // tem q mandar exceccao para se não tiver habilitado cheque especial
		conta1.saque();
		conta2.saque();
		banco.listarContasNegativas();
	}
}
