import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class UploadClient {

    public static void main(String[] args) {
        UploadClient client = new UploadClient();
        client.upload("C:\\Users\\NicolasL\\Desktop\\TestFile.txt");
    }

    /**
     * 作用：上传文件到服务器
     * 1.建立到服务器的连接
     * 2.获取输出流
     * 3.将文件内容写入到输出流
     * 4.获取服务器的响应
     */
    private void upload(String name) {
        Socket socket = null;
        OutputStream out;
        try {
            socket = new Socket("127.0.0.1", 55554);
            out = socket.getOutputStream();
            write2OutputStream(name, out);
            //异常捕获
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testUpload() {
        upload("src\\status.xml");
    }

    /**
     * 作用：传入文件名和输出流，将文件写入到输出流
     *
     * @param path
     * @throws IOException
     */
    private void write2OutputStream(String path, OutputStream out) throws IOException {

        FileInputStream fis = null;
        byte[] buf = new byte[1024];
        String fileName = "";
        //业务逻辑
        try {

            //1.写入文件名
            fileName = path.substring(path.lastIndexOf('\\') + 1);
            System.out.println("您要上传的文件名为：" + fileName);
            out.write(fileName.getBytes());
            out.write('\n');
            //2.写入文件内容
            fis = new FileInputStream(path);
            int len;
            while ((len = fis.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
            //异常处理
        } catch (IOException e) {
            e.printStackTrace();
            //关闭资源
        } finally {
            fis.close();
            out.close();
        }
    }//End of upload

    public void testWrite2OutputStream() throws IOException {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            write2OutputStream("C:\\Users\\NicolasL\\Desktop", out);
            System.out.println(out.toString("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }

    }
}