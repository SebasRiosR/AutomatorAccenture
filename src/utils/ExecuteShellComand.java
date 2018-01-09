package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteShellComand {
	
	//Este metodo permite ejecutar un comando en el cmd
	public void executeCommand(String command) {
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	public void executeCommandWithoutWait(String command) {
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Es el mismo que el anterior, pero este retorna la respuesta tras la ejecucion del comando
	public String executeCommandReturn(String command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
}