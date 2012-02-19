/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.plugin.pear;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * A class to hold process runtime environments being executed
 * TODO migrate
 * 
 * @author mepeisen
 */
public class Proc
{

	/**
	 * The working directory
	 */
	private File workingDirectory;
	
	/**
	 * The command being executed
	 */
	private String command;
	
	/**
	 * Arguments
	 */
	private List<String> args = new ArrayList<String>();
	
	/**
	 * True to redirect the stdout to a string and not to a log
	 */
	private boolean redirectOutputStream = false;
	
	/**
	 * The maven log to be used for non wrapped output
	 */
	private Log log;

	/**
	 * The buffer holding the wrapped output
	 */
	private StringBuffer outputStream = new StringBuffer();
	
	/**
	 * Environment variables
	 */
	private Map<String, String> env = new HashMap<String, String>();

	/**
	 * Sets the maven log for non wrapped output
	 * @param log
	 */
	public void setLog(Log log)
	{
		this.log = log;
	}
	
	/**
	 * The working directory
	 * @return
	 */
	public File getWorkingDirectory()
	{
		return workingDirectory;
	}

	/**
	 * The working directory
	 * @param workingDirectory
	 */
	public void setWorkingDirectory(File workingDirectory)
	{
		this.workingDirectory = workingDirectory;
	}

	/**
	 * The command to be executed
	 * @return
	 */
	public String getCommand()
	{
		return command;
	}

	/**
	 * The command to be executed
	 * @param command
	 */
	public void setCommand(String command)
	{
		this.command = command;
	}
	
	/**
	 * Adds an environment variable
	 * @param key
	 * @param value
	 */
	public void addEnv(String key, String value)
	{
		this.env.put(key, value);
	}
	
	/**
	 * True to redirect the output to a string
	 * @return
	 */
	public boolean isRedirectOutputStream()
	{
		return redirectOutputStream;
	}

	/**
	 * True to redirect the output to a string
	 * @return
	 */
	public void setRedirectOutputStream(boolean redirectOutputStream)
	{
		this.redirectOutputStream = redirectOutputStream;
	}

	/**
	 * Redirected console output
	 * @return
	 */
	public String getOutput()
	{
		return outputStream.toString();
	}
	
	/**
	 * Adds a cli argument to be sent to the script
	 * @param arg
	 */
	public void addArgument(String arg)
	{
		this.args.add(arg);
	}
	
	/**
	 * Executes the php command
	 * @throws MojoExecutionException
	 */
	public void execute() throws MojoExecutionException
	{
		final List<String> cmd = new ArrayList<String>();
		cmd.add(this.command);
		cmd.addAll(this.args);

		if (log != null) log.info("executing " + cmd.toString());
		this.executeProcess(log, cmd.toArray(new String[cmd.size()]), this.workingDirectory);
	}
    
    /**
     * Executes a process
     * 
     * @param log
     * @param args
     * @param targetFolder
     * 
     * @throws MojoExecutionException 
     */
    protected void executeProcess(Log log, String[] args, File targetFolder) throws MojoExecutionException
    {
    	Thread t1 = null;
    	Thread t2 = null;
    	try
    	{
	    	Process proc = null;
	        try
	        {
	        	final ProcessBuilder pb = new ProcessBuilder(args).directory(targetFolder);
	        	final Map<String, String> pbEnv = pb.environment();
	        	for (final Map.Entry<String, String> e : env.entrySet())
	        	{
	        		pbEnv.put(e.getKey(), e.getValue());
	        	}
	        	proc = pb.start();
	        	t1 = new Thread(new Receiver(proc.getInputStream(), isRedirectOutputStream() ? new StringBufferOutputWrapper(this.outputStream) : new LogOutputWrapper(log)));
	        	t2 = new Thread(new Receiver(proc.getErrorStream(), new LogOutputWrapper(log)));
	        	t1.start();
	        	t2.start();
	        	try
	        	{
					proc.waitFor();
				}
	        	catch (InterruptedException e)
	        	{
					// ignore
				}
	        	if (isRedirectOutputStream() && log != null)
	        	{
	        	    log.info("proc output:\n" + this.outputStream);
	        	}
	        }
	        finally
	        {
	        	if (proc != null)
	        	{
	        		try
	        		{
						if (proc.getInputStream() != null) proc.getInputStream().close();
					}
	        		catch (IOException e)
	        		{
						// ignore
					}
	        		try
	        		{
						if (proc.getOutputStream() != null) proc.getOutputStream().close();
					}
	        		catch (IOException e)
	        		{
						// ignore
					}
	        		try
	        		{
						if (proc.getErrorStream() != null) proc.getErrorStream().close();
					}
	        		catch (IOException e)
	        		{
						// ignore
					}
	        	}
	        }
	        
//        	// wait for the output to be received
//	        try
//	        {
//	        	if (t1 != null) t1.join();
//	        }
//	        catch (InterruptedException e)
//	        {
//	        	// ignore
//	        }
//	        try
//	        {
//	        	if (t2 != null) t2.join();
//	        }
//	        catch (InterruptedException e)
//	        {
//	        	// ignore
//	        }
    	}
    	catch (IOException e)
    	{
            throw new MojoExecutionException( "Error executing command " + this.command, e ); //$NON-NLS-1$
    	}
    }

	/**
     * thread to read output from child
     */
    private static final class Receiver implements Runnable
    {
    	/**
         * stream to receive data from child
         */
        private final InputStream is;
        
        /**
         * The output
         */
        private final OutputWrapper out;

        /**
         * method invoked when Receiver thread started.  Reads data from child and displays in on System.out.
         */
        public void run()
        {
            try
            {
            	final BufferedReader br = new BufferedReader( new InputStreamReader( is ), 500);
            	String line;
            	while ( ( line = br.readLine() ) != null )
            	{
            		out.append(line);
            	}
            	br.close();
            }
            catch ( IOException e )
            {
                throw new IllegalArgumentException( "IOException receiving data from child process." ); //$NON-NLS-1$
            }
        }
        
        /**
         * contructor
         *
         * @param is stream to receive data from child
         */
        Receiver(InputStream is, OutputWrapper out )
        {
            this.is = is;
            this.out = out;
        }
    }
    
    private interface OutputWrapper
    {
    	public void append(String line);
    }
    
    private static final class LogOutputWrapper implements OutputWrapper
    {
    	
    	private final Log log;
    	
    	public LogOutputWrapper(Log log)
    	{
    		this.log = log;
    	}

		@Override
		public void append(String line)
		{
			if (log != null) log.info(line);
		}
    	
    }
    
    private static final class StringBufferOutputWrapper implements OutputWrapper
    {
    	
    	private final StringBuffer buffer;
    	
    	public StringBufferOutputWrapper(StringBuffer buffer)
    	{
    		this.buffer = buffer;
    	}

		@Override
		public void append(String line)
		{
			buffer.append(line).append("\n"); //$NON-NLS-1$
		}
    	
    }


}
