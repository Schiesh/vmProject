/* Title: Virtual Machine */
/* Student name: Russell Schiesser */
/* Class: CSCI 4200 - Section D1 */
/* Professor: Dr. Abi Salimi */

package vmPackage;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class SubLC3VM {

    static int progCount = 0;
    static int progExec = 0;
    static String progValue;
    static int MAX_MEMORY_SIZE = 500;
    static HashMap<Integer, String> map = new HashMap<Integer, String>();
    static HashMap<String, Integer> intVar = new HashMap<String, Integer>();

    public static void main(String[] args) throws IOException {
        // Assignment Title and Display of SubLCVM program
        try {
            // Set up Input Stream Reader for input file
            InputStream in = new FileInputStream("mySubLC3_Prog.txt");
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            bufferedReader.mark(1000);
            // Set up Output Stream Writer for output file
            FileOutputStream out = new FileOutputStream("mySubLC3_Output.txt", false);
            OutputStreamWriter writer = new OutputStreamWriter(out);

            writer.write( "Russell Schiesser, CSCI 4200, Fall 2021\n");
            writer.write("***********************************************\n");

            // Rewrite the entire program on the output file for reference
            while ((progValue = bufferedReader.readLine()) != null) {
                writer.write(progValue + "\n");
            }

            writer.write("***********************************************\n");

            // Load Phase (mySubLC3_Prog.txt loaded into memory MAX_MEMORY_SIZE = 500)
            bufferedReader.reset();
            while ((progValue = bufferedReader.readLine()) != null) {
                put(progCount, progValue);
                progCount++;
            }

            reader.close();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Reset program counter after loading all contents into memory
        progCount = 0;
        progValue = "";

        // Fetch-execute-cycle Phase
        do{
            // Fetch the instruction pointed by the program counter
            String fetchInstruct = map.get(progCount);

            // Increment the program counter
            progCount++;

            // Decode and Execute the fetched instruction
            decodeFetchInstruct(fetchInstruct);

        }while(progExec == 0);

    }

    // put() method to check if the program is larger than the MAX_MEMORY_SIZE
    // This method also continues to load each instruction into memory
    public static void put(int progCount, String progValue) {
        if (map.size() >= MAX_MEMORY_SIZE && !map.containsKey(progCount)) {
            System.out.println("Program exceeds MAX_MEMORY_SIZE");
        }
        else {
            map.put(progCount, progValue);
        }
    }

    // decodeFetchInstruct takes the fetched instruction and decodes it into it's elements and pushes them
    // into their respected methods
    public static void decodeFetchInstruct(String fetchInstruct) throws IOException {
        // Decode the fetched instruction
        String[] split = fetchInstruct.split(" ");
        if (split[0].charAt(0) == ';') {
            split[0] = ";";
        }

        // Use the first element of the fetched instruction to determine its execution
        switch (split[0]) {
            case ";":
                // do nothing, and ignore line
                break;
            case "OUT":
                int varFlag = 0;
                String Value = "";
                String strValue = "";
                for(String key : intVar.keySet()) {
                    if (split[1].equals(key)) {
                        int temp = intVar.get(key);
                        OUT(temp);
                        varFlag++;
                    }
                }
                if (varFlag == 0) {
                    for(int i = 1; i < split.length; i++) {
                        Value = Value + split[i] + " ";
                    }
                    if (Value.charAt(0) == '\"') {
                        strValue = Value.replaceAll("\"", "");
                        OUT(strValue);
                    }
                    else {
                        OUT(Value);
                    }
                }
                break;
            case "IN":
                IN(split[1]);
                break;
            case "STO":
                String DestSTO = split[1];
                isValidIdent(DestSTO);
                String Source1STO = split[2];
                int intSource1STO;
                if (isNumeric(Source1STO)) {
                    intSource1STO = Integer.parseInt(Source1STO);
                    STO(DestSTO, intSource1STO);
                }
                else {
                    intSource1STO = intVar.get(Source1STO);
                    STO(DestSTO, intSource1STO);
                }

                break;
            case "ADD":

                String Dest = split[1];
                isValidIdent(Dest);
                String Source1 = split[2];
                String Source2 = split[3];
                ADD(Dest, Source1, Source2);

                break;
            case "SUB":

                String DestSub = split[1];
                isValidIdent(DestSub);
                String Source1Sub = split[2];
                String Source2Sub = split[3];
                SUB(DestSub, Source1Sub, Source2Sub);

                break;
            case "MUL":

                String DestMul = split[1];
                isValidIdent(DestMul);
                String Source1Mul = split[2];
                String Source2Mul = split[3];
                MUL(DestMul, Source1Mul, Source2Mul);

                break;
            case "DIV":
                String DestDiv = split[1];
                isValidIdent(DestDiv);
                String Source1Div = split[2];
                String Source2Div = split[3];
                DIV(DestDiv, Source1Div, Source2Div);

                break;
            case "BRn":
                BRn(split[1], split[2]);
                break;
            case "BRz":
                BRz(split[1], split[2]);
                break;
            case "BRp":
                BRp(split[1], split[2]);
                break;
            case "BRzp":
                BRzp(split[1], split[2]);
                break;
            case "BRzn":
                BRzn(split[1], split[2]);
                break;
            case "JMP":
                JMP(split[1]);
                break;
            case "HALT":
                HALT();
                break;
            default:
                LABEL(fetchInstruct);
                break;
        }
    }

    // Method to check to see if input source is numeric
    public static boolean isNumeric (String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    // ADD Destination Source1 Source2
    /* ADD accepts two integer values, Source 1 and Source2, and stores their sum in
       the Destination variable, Destination */
    public static void ADD(String Dest, String Source1, String Source2) {

        int intSource1;
        int intSource2;
        if (isNumeric(Source1)) {
            intSource1 = Integer.parseInt(Source1);
            intSource2 = Integer.parseInt(Source2);
            intVar.put(Dest, (intSource1 + intSource2));
        }
        else if (!isNumeric(Source1) && isNumeric(Source2)) {
            intSource2 = Integer.parseInt(Source2);
            int result = (intVar.get(Source1) + intSource2);
            intVar.put(Dest, result);
        }
        else {
            int result = (intVar.get(Source1) + intVar.get(Source2));
            intVar.put(Dest, result);
        }
    }

    // SUB Destination Source1 Source2
    /* SUB accepts two integer values, Source1 and Source2, and stores the result of
       (Source1 - Source2 in the Destination variable, Destination */
    public static void SUB(String Dest, String Source1, String Source2) {

        int intSource1;
        int intSource2;
        if (isNumeric(Source1)) {
            intSource1 = Integer.parseInt(Source1);
            intSource2 = Integer.parseInt(Source2);
            intVar.put(Dest, (intSource1 - intSource2));
        }
        else if (!isNumeric(Source1) && isNumeric(Source2)) {
            intSource2 = Integer.parseInt(Source2);
            int result = (intVar.get(Source1) - intSource2);
            intVar.put(Dest, result);
        }
        else {
            int result = (intVar.get(Source1) - intVar.get(Source2));
            intVar.put(Dest, result);
        }
    }

    // MUL Destination Source1 Source2
    /* MUL accepts two integer values, Source1 and Source2, and stores the result of
       (Source1 * Source2) in the Destination variable, Destination */
    public static void MUL(String Dest, String Source1, String Source2) {

        int intSource1;
        int intSource2;
        if (isNumeric(Source1)) {
            intSource1 = Integer.parseInt(Source1);
            intSource2 = Integer.parseInt(Source2);
            intVar.put(Dest, (intSource1 * intSource2));
        }
        else if (!isNumeric(Source1) && isNumeric(Source2)) {
            intSource2 = Integer.parseInt(Source2);
            int result = (intVar.get(Source1) * intSource2);
            intVar.put(Dest, result);
        }
        else {
            int result = (intVar.get(Source1) * intVar.get(Source2));
            intVar.put(Dest, result);
        }
    }
    // DIV Destination Source1 Source2
    /* DIV accepts two integer values, Source1 and Source2, and stores the result of
       (Source1 / Source2) in the Destination variable, Destination */
    public static void DIV(String Dest, String Source1, String Source2) {

        int intSource1;
        int intSource2;
        if (isNumeric(Source1)) {
            intSource1 = Integer.parseInt(Source1);
            intSource2 = Integer.parseInt(Source2);
            intVar.put(Dest, (intSource1 / intSource2));
        }
        else if (!isNumeric(Source1) && isNumeric(Source2)) {
            intSource2 = Integer.parseInt(Source2);
            int result = (intVar.get(Source1) / intSource2);
            intVar.put(Dest, result);
        }
        else {
            int result = (intVar.get(Source1) / intVar.get(Source2));
            intVar.put(Dest, result);
        }
    }

    // IN Variable
    /* Inputs an integer value and stores it in Variable */
    public static void IN(String Variable) throws IOException {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter an integer value: ");
        String input = scan.nextLine();
        OutputStream out = new FileOutputStream("mySubLC3_Output.txt", true);
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write("" + input + "\n");
        writer.close();
        int var = Integer.parseInt(input);
        intVar.put(Variable, var);

    }

    // OUT Value for both String and Integer values
    public static void OUT(String Value) throws IOException {

        OutputStream out = new FileOutputStream("mySubLC3_Output.txt", true);
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write("" + Value + "\n");
        writer.close();

    }
    public static void OUT(Integer Value) throws IOException {

        OutputStream out = new FileOutputStream("mySubLC3_Output.txt", true);
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write("" + Value + "\n");
        writer.close();

    }

    // STO Destination Source
    /* The STO instruction stores the value of Source in Destination variable
       Source can be either a variable or an integer constant */
    public static void STO(String destination, int source) {
        intVar.put(destination, source);
    }

    // BRn Variable Label
    /* If the value of Variable is negative, jump to Label */
    public static void BRn(String var, String label) {
        int check = intVar.get(var);
        if (check < 0) {
            for(int key : map.keySet()) {
                if (Objects.equals(label, map.get(key))) {
                    progCount = key;
                }
            }
        }
    }

    // BRz Variable Label
    /* If the value of Variable is zero, jump to Label */
    public static void BRz(String var, String label) {
        int check = intVar.get(var);
        if (check == 0) {
            for(int key : map.keySet()) {
                if (Objects.equals(label, map.get(key))) {
                    progCount = key;
                }
            }
        }
    }

    // BRp Variable Label
    /* If the value of Variable is positive, jump to Label */
    public static void BRp(String var, String label) {
        int check = intVar.get(var);
        if (check > 0) {
            for(int key : map.keySet()) {
                if (Objects.equals(label, map.get(key))) {
                    progCount = key;
                }
            }
        }
    }

    // BRzp Variable Label
    /* If the value of Variable is zero or positive, jump to Label */
    public static void BRzp(String var, String label) {
        int check = intVar.get(var);
        if (check == 0 || check > 0) {
            for (int key : map.keySet()) {
                if (Objects.equals(label, map.get(key))) {
                    progCount = key;
                }
            }
        }
    }

    // BRzn Variable Label
    /* If the value of Variable if zero or negative, jump to Label */
    public static void BRzn(String var, String label) {
        int check = intVar.get(var);
        if (check ==0 || check < 0) {
            for (int key : map.keySet()) {
                if (Objects.equals(label, map.get(key))) {
                    progCount = key;
                }
            }
        }
    }

    // JMP Label
    /* Jump to Label */
    public static void JMP(String label) {
        for (int key : map.keySet()) {
            if (Objects.equals(label, map.get(key))) {
                progCount = key;
            }
        }
    }

    // HALT
    /* End the program execution */
    static void HALT() {
        progExec -= 1 ;
    }

    // Identifier
    /* The name of an identifier starts with a letter, followed by a sequence of zero or more letters
       digits or underscores */
    public static void isValidIdent(String identifier) {
        if (identifier.matches("[A-Za-z][A-Za-z0-9_]*")) {
            // Program will continue on if identifier is valid
        }
        else {
            System.out.println(identifier + " is invalid");
            HALT();
        }
    }

    // Label
    /* Label is an identifier, which indicates a location within the SubLC3 program.
       A label is a destination for JMP or BRx instruction */
    public static void LABEL(String inputLine) {
        String[] tempArray = inputLine.trim().split(" ");
        if (tempArray.length == 1) {
            isValidIdent(tempArray[0]);
        }
        else {
            System.out.println(inputLine + " is not a valid Label");
            HALT();
        }
    }
}
