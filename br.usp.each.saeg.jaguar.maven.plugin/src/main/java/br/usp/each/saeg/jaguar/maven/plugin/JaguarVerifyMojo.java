package br.usp.each.saeg.jaguar.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import java.io.*;
import org.apache.maven.plugin.MojoExecutionException;
import java.net.URL;
import java.io.File;

/**
 * Goal - Verify Jaguar
 *
 * @goal jaguarVerify
 * 
 * @phase process-test-classes
 */
public class JaguarVerifyMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter property="project.build.directory"
     * @required
     */
    private File directory;
    /**
     * Location of the file.
     * @parameter property="project.build.testOutputDirectory"
     * @required
     */
    private File testOutputDirectory;

    /**
     * Location of the file.
     * @parameter property="project.build.outputDirectory"
     * @required
     */
    private File outputDirectory;
    
    /**
     * Log Level: ERROR / INFO / DEBUG / TRACE.
     * @parameter defaultValue = "TRACE"
     */
    private String logLevel;


    /**
     * Type: DataFlow / ControlFlow
     * @parameter defaultValue = "ControlFlow"
     */
    private String type;

    /**
     * Type: XML / HTML
     * @parameter defaultValue = "XML"
     */
    private String format;
       
    /**
     * Execution plugin maven
     */
    public void execute()
        throws MojoExecutionException
    {
    	 try
         {
    		 System.out.println("INIT JAGUAR MAVEN PLUGIN");
    		 
    		 //FORMATA CAMPO FORMAT
    		 if (format != null && 
	    	      (format.toUpperCase() != "XML" || 
		    	   format.toUpperCase() != "HTML"))
    			 format = format.toUpperCase();
	    		 else
	    			 format =  "XML";

    		 //FORMATA CAMPO LOGLEVEL
    		 if (logLevel != null && 
    	      (logLevel.toUpperCase() != "TRACE" || 
			    logLevel.toUpperCase() != "INFO" || 
			    logLevel.toUpperCase() != "DEBUG"))
    			logLevel = logLevel.toUpperCase();
    		 else
    			logLevel =  "ERROR";
    		 
			// URL f = JaguarAgentJar.getResource();

			 //VERIFICA SE O SO É WINDOWS
    		 boolean isWindows = System.getProperty("os.name")
    				  .toLowerCase().startsWith("windows");

		
    		 
    		 System.out.println("Is Windows: " + isWindows); 
    		 System.out.println("logLevel: " + logLevel);  
    		 System.out.println("type: " + type);  
    		 System.out.println("directory: " + directory.toString());  
    		 System.out.println("testOutputDirectory: " + testOutputDirectory.toString());  
    		 System.out.println("outputDirectory: " + outputDirectory.toString());  
    		 System.out.println("format: " + format.toString());  

			 File jaguar = JaguarAgentJar.extractJaguarToTempLocation();

			 File jacocoagent = JacocoAgentJar.extractJacocoToTempLocation();


			 System.out.println("Jaguar Agent Path: " + jaguar);
			 System.out.println("Jacoco Agent Path: " + jacocoagent);

    		 
    		 //DEFINE O CARACTER QUE FAZ A CONCATENAÇÃO DE COMANDOS
    		 String commandConcat = (isWindows) ? "&" : ";";   		     		 
    		  		     		
    		 //VERIFICA SE USARÁ TIPO DATAFLOW
    		 boolean isDataFlow = (type != null && type.toLowerCase() == "dataflow");
    		 
    		 //DEFINE O NOME DO ARQUIVO DE SAÍDA
    		 String outputFile = ((isDataFlow) ? "data-flow" : "control-flow");
    		 
    		 String command = "set -x " + commandConcat
    				 		//TODO: VERIFICAR FORMA DE PEGAR DE ALGUM LUGAR AS LIBS DA JAGUAR E JACOCO
    	            		+ "JAGUAR_JAR=\"" + jaguar + "\"" + commandConcat    	            		
    	            		+ "JACOCO_JAR=\""+ jacocoagent + "\"" + commandConcat   
    	            		+ "java -javaagent:$JACOCO_JAR=output=tcpserver "
    	            		+ ((isDataFlow) ? ",dataflow=true " : "")
    	            		+ "-cp " + outputDirectory + "/:" + testOutputDirectory + "/:$JAGUAR_JAR:$JACOCO_JAR "
    	            		+ "		\"br.usp.each.saeg.jaguar.core.cli.JaguarRunner\" "
    	            		+ ((isDataFlow) ? " --dataflow \\" : "")
    	            		//TODO: IMPLEMENTAR PARÂMETRO PARA ENVIO DE FORMATO PARA A CLI DA JAGUAR (XML ou HTML)
    	            		//+ "         --format \"" + format + "\" "
    	            		+ "			--outputType F "
    	            		+ "			--output \"" + outputFile + "\" "
    	            		+ "			--logLevel \"" + logLevel + "\" "
    	            		+ "			--projectDir \"" + directory + "\" "
    	            		+ "			--classesDir \"" + outputDirectory + "/\" "
    	            		+ "			--testsDir \"" + testOutputDirectory + "/\" "
    	            		+ "			--testsListFile \"" + directory + "/testListFile.txt\"";

    			 System.out.println("Command: " + command);
    			 System.out.println("");
    			 System.out.println("");
        		 ProcessBuilder builder = new ProcessBuilder();
        		 if (isWindows) {
        		     builder.command("cmd.exe", "/c", command);
        		 } else {
        		     builder.command("sh", "-c", command);
        		 }
    	            		
    	        builder.redirectErrorStream(true);
    	        Process p = builder.start();
    	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    	        String line;
    	        while (true) {
    	            line = r.readLine();
    	            if (line == null) { break; }
    	            System.out.println(line);
    	        }
    	            	        
         }
         catch ( Exception e )
         {
             throw new MojoExecutionException( "Error in jaguar-maven-plugin", e );
         }
    	 finally {
    		 System.out.println("FINAL JAGUAR MAVEN PLUGIN");
    	 }
    }
}
