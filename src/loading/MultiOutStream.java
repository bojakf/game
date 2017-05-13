package loading;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * 
 * This outputStream is for outputting to multiple output streams at once
 * 
 * @author jafi2
 *
 */
public class MultiOutStream extends OutputStream {

	/*
	 * FIXME throw exceptions individually (own exception object) at end of execution
	 */
	
	/**
	 * List of outputStreams to write to
	 */
	public ArrayList<OutputStream> out = new ArrayList<>();
	
	@Override
	public void write(int b) throws IOException {
		for(int i = 0; i < out.size(); i++) {
			out.get(i).write(b);
		}
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for(int i = 0; i < out.size(); i++) {
			out.get(i).write(b, off, len);
		}
	}
	
	@Override
	public void close() throws IOException {
		for(int i = 0; i < out.size(); i++) {
			out.get(i).close();
		}
	}
	
	@Override
	public void flush() throws IOException {
		for(int i = 0; i < out.size(); i++) {
			out.get(i).flush();
		}
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		for(int i = 0; i < out.size(); i++) {
			out.get(i).write(b);
		}
	}
	

}
