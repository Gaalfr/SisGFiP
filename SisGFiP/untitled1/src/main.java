import java.util.Scanner;
import java.util.ArrayList;
//sistema de gestao de financias pessoais
public class main {

    public static void main(String[] args){
int saldo= 0;
        Scanner scanner = new Scanner(System.in);


        ArrayList<String> extrato =new ArrayList<>();
while (true){

    System.out.println("entre com sua receita (salario ou lucros):-------> 1");
    System.out.println("entre com suas despesas:-------------------------> 2");
    System.out.println("ver saldo:---------------------------------------> 3");
    System.out.println("mostrar extrato:---------------------------------> 4");
    System.out.println("sair---------------------------------------------->5");

    Integer select = scanner.nextInt();

    switch (select){

        case 1:
            System.out.println("receita mensal:");
            int receita = scanner.nextInt();
            saldo = saldo + receita;
            extrato.add("+"+receita);
        break;
        case 2:
            System.out.println("despesas mensais:");
            int dispesa= scanner.nextInt();
            saldo = saldo - dispesa;
            extrato.add("-"+dispesa);
            break;
        case 3:

            System.out.println("saldo mensal = " + saldo);
            break;
        case 4:
            System.out.println("extrato: ");
            for(String registro:extrato){
            System.out.println(registro);


            }

            System.out.println("saldo atual:"+ saldo);

        case 5:
            System.out.println("saindo");
            return;
        default:
            System.out.println("opcao invalida");



    }









}

    }



}

