package utils;

import java.io.*;

public class JavaRunCommand {

    public static void main(String args[]) throws InterruptedException {

        String s = null;

        try {
            
        	System.out.println("STARTING VI");
        	 ProcessBuilder processBuilder = new ProcessBuilder("nano", "test.txt");
        	 processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        	 processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        	 processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        	 Process p = processBuilder.start();
        	  // wait for termination.
        	 p.waitFor();
        	 System.out.println("Exiting VI");
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}