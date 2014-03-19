/*
	Programmer: Lior Shahverdi
	Title: Interpreter.java
	Description: This interpreter reads a file with code written in 
				an assembly language defined with 16 opcodes. This interpreter is 
				only capable of performing boolean and arithmetic computations. 
				Once all the instructions in the file have been loaded into 
				the codeArray, they are executed in their proper order.
*/
import java.util.*;
import java.io.*;

public class Interpreter {

private int[] reg;
private String[] codeArray;
private Scanner file = null;
private Scanner file2 = null;
private static String name;
private String nextOp = "";

	public Interpreter(String filename)
	{
		reg = new int[16];		//instantiates integer array for registers
		reg[0] = 2;	//program counter will point to 3rd line in codeArray
		try
		{
			file = new Scanner(new File(filename));
		} catch (FileNotFoundException e)
			{
				System.out.println("File "+filename+" is not found");
				System.exit(0);
			}
		name = file.next();		//extracts the name of the program
		int numLines = file.nextInt();//extracts the number of lines in the code
		codeArray = new String[numLines];//instantiates the codeArray
		int j=2;
		while (file.hasNextLine())
		{
			String nextL = file.nextLine();
			//if the next line begins with digit (0-9) it
			//becomes a part of the codeArray
			if (nextL.matches("[0-9](.*)")) {
				Scanner instructScan = new Scanner(nextL);
				codeArray[j] = instructScan.next();
				j++;
			}
		}
		codeArray[0] = name;
		codeArray[1] = ""+numLines;
		fde(); //initiates the fetch decode execute cycle
		//for (int i =0; i<codeArray.length;i++)
		//System.out.println(codeArray[i]);
	}
	
	public void fde()
	{
		/*As long as nextOp points to an instruction, it will
		be decoded and executed.*/
		while (nextOp != null)
		{
			fetch();
			execute(decode());
		}
	}
	
	/**nextOp points to the instruction that 
	the program counter specifies and the program counter is then
	immediately incremented.*/
	public void fetch()
	{
		nextOp = codeArray[reg[0]];
		reg[0]++;
	}

	/*Based on the hexadecimal digit at the end of the instruction
	 as well its length, the type of instruction is determined and is
	 passed to the execute method*/
	public int decode()
	{
		if (nextOp.endsWith("0") && nextOp.length() == 6) return 0;
		else if (nextOp.endsWith("1") && nextOp.length() == 6) return 1;
		else if (nextOp.endsWith("2") && nextOp.length() == 5) return 2;
		else if (nextOp.endsWith("3") && nextOp.length() == 5) return 3;
		else if (nextOp.endsWith("4") && nextOp.length() == 5) return 4;
		else if (nextOp.endsWith("5") && nextOp.length() == 6) return 5;
		else if (nextOp.endsWith("6") && nextOp.length() == 6) return 6;
		else if (nextOp.endsWith("7") && nextOp.length() == 6) return 7;
		else if (nextOp.endsWith("8") && nextOp.length() == 7) return 8;
		else if (nextOp.endsWith("9") && nextOp.length() == 4) return 9;		
		else if (nextOp.endsWith("A") && nextOp.length() == 4) return 10;
		else if (nextOp.endsWith("B") && nextOp.length() == 6) return 11;
		else if (nextOp.endsWith("C") && nextOp.length() == 6) return 12;
		else if (nextOp.endsWith("D") && nextOp.length() == 6) return 13;
		else if (nextOp.endsWith("E") && nextOp.length() == 4) return 14;
		else if (nextOp.endsWith("F") && nextOp.length() == 3) return 15;
		return -1;
	}
	
	/*The execute method knows which block to execute based on the integer
	passed from the decode method.*/
	public void execute(int op)
	{
		if (op==0) 
		{
			//LOA
			//Load contents at location AD to RA.
			String adStr = nextOp.substring(2,4);	//extracts the address
			int address = Integer.parseInt(adStr, 16);	//converts address to base 10
			String regStr = nextOp.substring(4,5); //extracts the register
			int register = Integer.parseInt(regStr, 16); //converts register to base 10
			int instruction = Integer.parseInt(codeArray[address+2]); //converts the instruction to base 10
			reg[register] = instruction;//loads the instruction into specified register
			System.out.println("LOA\tR"+register+" now holds "+reg[register]);
		}
		else if (op==1) 
		{
			//STO
			//Store contents of RA to location AD.
			String adNum = nextOp.substring(2,4);//extracts address number
			int ad = Integer.parseInt(adNum,16);//converts address to base 10
			String raNum = nextOp.substring(4,5);//extracts register
			int ra = Integer.parseInt(raNum,16);//converts register to base 10
			codeArray[ad] = ""+reg[ra];//stores contents of register ra to address in codeArray
			System.out.println("STO\tcodeArray["+ad+"] now holds "+codeArray[ad]);
		}
		else if (op==2) 
		{
			//CPR
			//Copy RB into RA.
			String rbStr = nextOp.substring(2,3);//extracts register b number
			int rb = Integer.parseInt(rbStr, 16);//converts register b number to base 10
			String raStr = nextOp.substring(3,4);//extracts register a number
			int ra = Integer.parseInt(raStr, 16);//converts register a number to base 10
			reg[ra] = reg[rb];//copies content of second register to first register
			System.out.println("CPR\tR"+ra+" now has the contents of R"+rb+": "+reg[ra]);
		}
		else if (op==3) 
		{
			//LOI
			//RB holds the address of AD. Load the value of AD into RA.
			String rbStr = nextOp.substring(2,3);//extracts register b number
			int rb = Integer.parseInt(rbStr, 16);//converts register b number to base 10
			String raStr = nextOp.substring(3,4);//extracts register a number
			int ra = Integer.parseInt(raStr, 16);//converts register a number to base 10
			reg[ra] = reg[rb];//loads the address in register b to register a
			System.out.println("LOI\tR"+ra+" now has the contents of R"+rb+": "+reg[ra]);
		}
		else if (op==4)
		{
			//STI
			//RA holds the address of AD. Store the value in RB to AD.
			String rbStr = nextOp.substring(2,3);//extracts register b number
			int rb = Integer.parseInt(rbStr, 16);//converts register b number to base 10
			String raStr = nextOp.substring(3,4);//extracts register a number
			int ra = Integer.parseInt(raStr, 16);//converts register a number to base 10
			codeArray[reg[ra]] = ""+reg[rb];//stores address in register to the specified line (or element) in codeArray
			System.out.println("STI\tcodeArray["+ra+"] now has the contents of R"+rb+": "+codeArray[reg[ra]]);
		}
		else if (op==5) 
		{
			//RC = RA + RB
			String rcStr = nextOp.substring(2,3);//extracts register c number
			int rc = Integer.parseInt(rcStr, 16);//converts register c to base 10
			String rbStr = nextOp.substring(3,4);//extracts register b number
			int rb = Integer.parseInt(rbStr, 16);//converts register b to base 10
			String raStr = nextOp.substring(4,5);//extracts register a number
			int ra = Integer.parseInt(raStr, 16);//converts register a to base 10
			reg[rc] = reg[ra]+reg[rb];//stores sum of content in registers a and b in register c
			System.out.println("ADD\tR"+ra+" + R"+rb+" = R"+rc+" = "+reg[rc]);
		}
		else if (op==6) 
		{	
			//RC = RA - RB
			String rcStr = nextOp.substring(2,3);//extracts register c number
			int rc = Integer.parseInt(rcStr, 16);//converts register c to base 10
			String rbStr = nextOp.substring(3,4);//extracts register b number
			int rb = Integer.parseInt(rbStr, 16);//converts register b to base 10
			String raStr = nextOp.substring(4,5);//extracts register a number
			int ra = Integer.parseInt(raStr, 16);//converts register a to base 10
			reg[rc] = reg[ra]-reg[rb];//stores difference of content in registers a and b in register c
			System.out.println("SUB\tR"+rc+" = R"+ra+" - R"+rb+" = "+reg[rc]);	
		}
		else if (op==7) 
		{
			//RC = RA * RB
			String rcStr = nextOp.substring(2,3);//extracts register c number
			int rc = Integer.parseInt(rcStr, 16);//converts register c to base 10
			String rbStr = nextOp.substring(3,4);//extracts register b number
			int rb = Integer.parseInt(rbStr, 16);//converts register b to base 10
			String raStr = nextOp.substring(4,5);//extracts register a number
			int ra = Integer.parseInt(raStr, 16);//converts register a to base 10
			reg[rc] = reg[ra]*reg[rb];//stores product of content in registers a and b in register c
			System.out.println("MUL\tR"+rc+" = "+reg[rc]);
		}
		else if (op==8) 
		{
			//RC = RA / RB, and RD = RA % RB
			String rdStr = nextOp.substring(2,3);//extracts register d number
			int rd = Integer.parseInt(rdStr, 16);//converts register d to base 10
			String rcStr = nextOp.substring(3,4);//extracts register c number
			int rc = Integer.parseInt(rcStr, 16);//converts register c to base 10
			String rbStr = nextOp.substring(4,5);//extracts register b number
			int rb = Integer.parseInt(rbStr, 16);//converts register b to base 10
			String raStr = nextOp.substring(5,6);//extracts register a number
			int ra = Integer.parseInt(raStr, 16);//converts register a to base 10
			reg[rc] = reg[ra] / reg[rb]; //stores quotient of a/b in register c
			reg[rd] = reg[ra] % reg[rb]; //stores remainder of a/b in register d
			System.out.println("DIV\tR"+ra+" divided by R"+rb+"\tR"+rc+" = "+reg[rc]+"\tR"+rd+" = "+reg[rd]);
		}
		else if (op==9) 
		{
			//RA++
			String raStr = nextOp.substring(2,3);//extracts register a
			int ra = Integer.parseInt(raStr, 16);//converts register a to base 10
			reg[ra]+=1;//adds 1 to value in register a
			System.out.println("ICR\tR"+ra+" = "+reg[ra]);
		}
		else if (op==10) 
		{
			//RA--
			String raStr = nextOp.substring(2,3);//extracts register a
			int ra = Integer.parseInt(raStr, 16);//converts register a to base 10
			reg[ra]-=1;//subtracts 1 from value in register a 
			System.out.println("DCR\tR"+ra+" = "+reg[ra]);
		}
		else if (op==11) 
		{
			//GTR
			//RC = (RA > RB)
			String rcStr = nextOp.substring(2,3);//extracts register c
			int rc = Integer.parseInt(rcStr, 16);//converts register c to base 10
			String rbStr = nextOp.substring(4,5);//extracts register b
			int rb = Integer.parseInt(rbStr, 16);//converts register b to base 10
			String raStr = nextOp.substring(3,4);//extracts register a
			int ra = Integer.parseInt(raStr, 16);//converts register a to base 10
			if (reg[ra] > reg[rb]) reg[rc] = 1; //register c contains 1 if ra > rb
			else reg[rc] = 0;//register c contains 0 otherwise
			System.out.println("GTR\tR"+rc+" = "+reg[rc]);	
		}
		else if (op==12) 
		{
			//JMP
			//Jump to AD. (RA is unused).
			String ad = nextOp.substring(2,4); //extracts address
			int adNum = Integer.parseInt(ad, 16);//converts address to base 10
			reg[0] = adNum+2;//program counter now points to address
			System.out.println("JMP\tR0 = "+reg[0]);
		}
		else if (op==13)
		{
			//IFZ
			//If (RA == 0) then goto AD.
			String adStr = nextOp.substring(2,4);//extracts address
			int address = Integer.parseInt(adStr, 16);//converts address to base 10
			String r1Str = nextOp.substring(4,5);//extracts register number
			int r1 = Integer.parseInt(r1Str,16);//converts register number to base 10
			if (reg[r1] == 0)	
			{	//program counter contains address if value in register equals 0
				reg[0] = address+2;
				System.out.println("IFZ\tR0 = "+reg[0]);
			}
			else 	
				System.out.println("IFZ\tR0 = "+reg[0]);
		}
		else if (op==14)
		{
			//JMI
			//RA holds the address of AD. Jump to AD
			String rgStr = nextOp.substring(2,3); //extracts register number
			int ra = Integer.parseInt(rgStr, 16);//converts register number to base 10
			int address = reg[ra];
			address+=2;
			reg[0] = address;//program counter points to address
			System.out.println("JMI\tR0 = "+reg[0]);
		}
		else if (op==15)
		{
			//Trap to the kernel.
			if (reg[15] == 0)  System.exit(0);
			else if (reg[15] == 1)
			{	//reads numerical input into register 14
				System.out.print("stdin\t");
				Scanner frtnScan = new Scanner(System.in);
				reg[14] = frtnScan.nextInt();
			}
			else if (reg[15] == 2)//prints output from register 14
				System.out.println("stdout\t"+reg[14]);
		}
	}
	
	public static void main (String[] args) 
	{
		System.out.print("Enter a filename.. ");
		Scanner input = new Scanner(System.in);
		String inStr = input.next();
		Interpreter myInterpreter = new Interpreter(inStr);
	}
}