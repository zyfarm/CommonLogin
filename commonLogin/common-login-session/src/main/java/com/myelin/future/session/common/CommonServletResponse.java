package com.myelin.future.session.common;


import com.myelin.future.session.config.CommonSessionConfig;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;


/**
 * Created by gabriel on 14-8-24.
 */
public class CommonServletResponse extends HttpServletResponseWrapper {
    private CommonSession session;
    private static final String EXPIRES = "Expires";
    private static final String PATH = "Path";
    private static final String DOMAIN = "Domain";
    private static final String HTTP_ONLY = "HttpOnly";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String SECURE = "Secure";

    private BufferedServletOutputStream stream;
    private BufferedServletWriter writer;
    private PrintWriter streamAdapter;
    private ServletOutputStream writerAdapter;

    private boolean flushed;
    private int status;
    private boolean isWriterBuffered = true;


    public boolean isWriterBuffered() {
        return isWriterBuffered;
    }


    public void setWriterBuffered(boolean isWriterBuffered) {
        this.isWriterBuffered = isWriterBuffered;
    }


    private static class WriterOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private Writer writer;
        private String charset;

        public WriterOutputStream(Writer writer, String charset) {
            this.writer = writer;
            this.charset = (null == charset ? "ISO-8859-1" : charset);
        }

        @Override
        public void write(int b) throws IOException {
            buffer.write((byte) b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            buffer.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            ByteArray bytes = buffer.toByteArray();

            if (bytes.getLength() > 0) {
                ByteArrayInputStream inputBytes = new ByteArrayInputStream(bytes.getBytes(), bytes.getOffset(), bytes
                        .getLength());
                InputStreamReader reader = new InputStreamReader(inputBytes, charset);

                io(reader, writer);
                writer.flush();

                buffer.reset();
            }
        }

        @Override
        public void close() throws IOException {
            this.flush();
        }

        private void io(Reader in, Writer out) throws IOException {
            char[] buffer = new char[8192];
            int amount;

            while ((amount = in.read(buffer)) >= 0) {
                out.write(buffer, 0, amount);
            }
        }
    }


    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (stream != null) {
            return stream;
        }

        if (writer != null) {
            if (writerAdapter != null) {
                return writerAdapter;
            } else {
                writerAdapter = new WriterOutputStream(writer, getCharacterEncoding());
                return writerAdapter;
            }
        }
        if (this.isWriterBuffered) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            stream = new BufferedServletOutputStream(bytes);

            return stream;
        } else {
            this.getSession().commit();
            return super.getOutputStream();
        }

    }


    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }

        if (stream != null) {
            if (streamAdapter != null) {
                return streamAdapter;
            } else {
                streamAdapter = new PrintWriter(new OutputStreamWriter(stream, getCharacterEncoding()), true);
                return streamAdapter;
            }
        }
        if (this.isWriterBuffered) {
            StringWriter chars = new StringWriter();

            writer = new BufferedServletWriter(chars);

            return writer;
        } else {
            this.getSession().commit();
            return super.getWriter();
        }
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public SendError getSendError() {
        return sendError;
    }


    public void setSendError(SendError sendError) {
        this.sendError = sendError;
    }


    public String getSendRedirect() {
        return sendRedirect;
    }


    public void setSendRedirect(String sendRedirect) {
        this.sendRedirect = sendRedirect;
    }

    private SendError sendError;
    private String sendRedirect;

    private static class ByteArray {
        private byte[] bytes;
        private int offset;
        private int length;


        public ByteArray(byte[] bytes, int offset, int length) {
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        }


        public byte[] getBytes() {
            return bytes;
        }


        public int getOffset() {
            return offset;
        }


        public int getLength() {
            return length;
        }


        public void writeTo(OutputStream out) throws IOException {
            out.write(bytes, offset, length);
        }
    }

    private static class SendError {
        public final int status;
        public final String message;


        public SendError(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    /**
     * 代表一个将内容保存在内存中的<code>PrintWriter</code>。
     */
    private static class BufferedServletWriter extends PrintWriter {
        public BufferedServletWriter(StringWriter chars) {
            super(chars);
        }


        public Writer getChars() {
            return this.out;
        }


        public void updateWriter(StringWriter chars) {
            this.out = chars;
        }


        public void close() {
            try {
                this.out.close();
            } catch (IOException e) {
            }

        }
    }

    private static class ByteArrayOutputStream extends OutputStream {
        private static final int DEFAULT_INITIAL_BUFFER_SIZE = 8192;

        // internal buffer
        private byte[] buffer;
        private int index;
        private int capacity;

        // is the stream closed?
        private boolean closed;

        // is the buffer shared?
        private boolean shared;


        public ByteArrayOutputStream() {
            this(DEFAULT_INITIAL_BUFFER_SIZE);
        }


        public ByteArrayOutputStream(int initialBufferSize) {
            capacity = initialBufferSize;
            buffer = new byte[capacity];
        }


        @Override
        public void write(int datum) throws IOException {
            if (closed) {
                throw new IOException("Stream closed");
            } else {
                if (index >= capacity) {
                    // expand the internal buffer
                    capacity = (capacity * 2) + 1;

                    byte[] tmp = new byte[capacity];

                    System.arraycopy(buffer, 0, tmp, 0, index);
                    buffer = tmp;

                    // the new buffer is not shared
                    shared = false;
                }

                // store the byte
                buffer[index++] = (byte) datum;
            }
        }


        @Override
        public void write(byte[] data, int offset, int length) throws IOException {
            if (data == null) {
                throw new NullPointerException();
            } else if ((offset < 0) || ((offset + length) > data.length) || (length < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (closed) {
                throw new IOException("Stream closed");
            } else {
                if ((index + length) > capacity) {
                    // expand the internal buffer
                    capacity = (capacity * 2) + length;

                    byte[] tmp = new byte[capacity];

                    System.arraycopy(buffer, 0, tmp, 0, index);
                    buffer = tmp;

                    // the new buffer is not shared
                    shared = false;
                }

                // copy in the subarray
                System.arraycopy(data, offset, buffer, index, length);
                index += length;
            }
        }


        @Override
        public void close() {
            closed = true;
        }


        public ByteArray toByteArray() {
            shared = true;
            return new ByteArray(buffer, 0, index);
        }


        public void reset() throws IOException {
            if (closed) {
                throw new IOException("Stream closed");
            } else {
                if (shared) {
                    // create a new buffer if it is shared
                    buffer = new byte[capacity];
                    shared = false;
                }

                // reset index
                index = 0;
            }
        }
    }

    private static class BufferedServletOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bytes;


        public BufferedServletOutputStream(ByteArrayOutputStream bytes) {
            this.bytes = bytes;
        }


        public void updateOutputStream(ByteArrayOutputStream bytes) {
            this.bytes = bytes;
        }


        public ByteArrayOutputStream getBytes() {
            return this.bytes;
        }


        @Override
        public void write(int b) throws IOException {
            bytes.write((byte) b);
        }


        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }


        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            bytes.write(b, off, len);
        }


        @Override
        public void flush() throws IOException {
            bytes.flush();
        }


        @Override
        public void close() throws IOException {
            bytes.flush();
            bytes.close();
        }
    }


    public CommonSession getSession() {
        return session;
    }


    public void setSession(CommonSession session) {
        this.session = session;
    }


    public CommonServletResponse(HttpServletResponse response) {
        super(response);
        this.flushed = false;
    }


    private void flushBufferAdapter() {
        if (streamAdapter != null) {
            streamAdapter.flush();
        }

        if (writerAdapter != null) {
            try {
                writerAdapter.flush();
            } catch (IOException e) {
            }
        }
    }


    public void commitBuffer() throws IOException {
        if (CommonSessionConfig.crossDomainAllow) {
            this.setHeader("Access-Control-Allow-Origin", "*");
        }

        if (containsHeader("Set-Cookie") && !containsHeader("P3P")) { // 第三方隐私保护cookie
            this.setHeader("P3P",
                    "CP='CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR'");
        }

        if (status > 0) {
            super.setStatus(status);
        }

        if (sendError != null) {
            if (sendError.message == null) {
                super.sendError(sendError.status);
            } else {
                super.sendError(sendError.status, sendError.message);
            }
        } else if (sendRedirect != null) {
            super.sendRedirect(sendRedirect);
        } else if (stream != null) {
            flushBufferAdapter();
            OutputStream ostream = super.getOutputStream();
            ByteArray bytes = this.stream.getBytes().toByteArray();
            bytes.writeTo(ostream);
        } else if (writer != null) {
            flushBufferAdapter();
            PrintWriter writer = super.getWriter();
            try {
                String chars;
                chars = this.writer.getChars().toString();
                writer.write(chars);
            } catch (NullPointerException e) {

            }
        }

        if (this.flushed) {
            super.flushBuffer();
        }
    }
}
