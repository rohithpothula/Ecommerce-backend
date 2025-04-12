package com.flipkart.ecommerce_backend.wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//import org.springframework.data.util.StreamUtils;
import org.springframework.util.StreamUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper{
    
    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
	super(request);
	ServletInputStream inputStream = request.getInputStream();
	this.cachedBody = StreamUtils.copyToByteArray(inputStream);
    }
    
    @Override
    public ServletInputStream getInputStream() {
	return new CachedServletInputStream(this.cachedBody);
    }
    
    public BufferedReader getReader() {
	ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
	return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
    
    public String getbody() {
	return new String(this.cachedBody);
    }
    
    private static class CachedServletInputStream extends ServletInputStream {
	private InputStream cachedBodyInputStream;
	
	private CachedServletInputStream (byte[] cacheRequest) {
	    this.cachedBodyInputStream=new ByteArrayInputStream(cacheRequest);
	}

	@Override
	public boolean isFinished() {
	    try {
	            return cachedBodyInputStream.available() == 0;
	        } catch (IOException e) {
	            return true;
	        }
	}

	@Override
	public boolean isReady() {
	    return true;
	}

	@Override
	public void setReadListener(ReadListener listener) {
	    throw new UnsupportedOperationException();
	}

	@Override
	public int read() throws IOException {
	    return cachedBodyInputStream.read();
	}
	
    }

}
