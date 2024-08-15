import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class UploadServer {

    public static void main(String[] args) {
        UploadServer server=new UploadServer();
        UploadThread command=new UploadThread();
        server.start(command);
    }

    /**
     * 功能：接受连接，开启子线程，循环
     * @param command 用于下载的子线程对象，该对象实现了Runnable接口
     */
    private void start(UploadThread command){
        //局部变量
        ServerSocket serverSocket = null;
        Socket transSocket;
        //业务逻辑
        try {
            serverSocket=new ServerSocket(55554);
            while(true){
                System.out.println("等待连接……");
                transSocket=serverSocket.accept();
                int i=0;
                i++;
                System.out.println("第"+i+"个连接");
                //用不用在下载完后关闭线程呢？？？
                command.setSocket(transSocket);
                Executors.newFixedThreadPool(5).execute(command);
            }
            //异常捕获
        } catch (IOException e) {
            e.printStackTrace();
            //关闭资源
        } finally{
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//End of try
    }//End of connect

    public void testConnect() {
        //测试任务：先运行服务器端，然后多次运行客户端，服务器段可以不断创建子线程，则测试成功
        //测试准备：构造一个线程，用于模拟下载线程
        UploadThread command=new UploadThread();
        start(command);

    }

    public void testDown() throws IOException {
        byte[] buf;
        ByteArrayInputStream bis;
        String str="canglaoshi.avi\ncontent,content,content";
        buf=str.getBytes();
        bis=new ByteArrayInputStream(buf);
        UploadThread ut=new UploadThread();
        ut.down(bis);
    }
}
//完成各个传输任务的子线程
class UploadThread implements Runnable{

    Socket socket;
    public UploadThread(){}
    public UploadThread(Socket socket){
        this.socket=socket;
    }
    @Override
    public void run() {
        InputStream in;
        try {

            in = socket.getInputStream();
            down(in);

            //异常处理
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //测试代码
		/*try {
			Thread.sleep(5000);
			System.out.println(Thread.currentThread().getName()+",复制完毕");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
    }//End of run
    public void setSocket(Socket socket){
        this.socket=socket;
    }
    //下载方法
    /**
     * 目标：把InputStream中的数据写入到本地
     * 思路：
     * 1.获取输入流,最好传入输入流，而不是直接从Socket获取，传入有利用单元测试
     * 2.从输入流中读到文件名
     * 3.新建文件和文件输出流
     * 4.从输入流中读到文件内容到文件输出流
     * 5.
     * @throws IOException
     */
    public void down(InputStream in) throws IOException{
        //局部变量
        char ch;
        char[] nameArr=new char[256];
        byte[] buf=new byte[1024];
        String name="";
        OutputStream out = null;
        //业务逻辑
        try {
            //第一步：获取文件名，构造文件输出流
            int i=0;
            while((ch=(char) in.read())!='\n'){
                nameArr[i++]= ch;
            }
            //name=nameArr.toString();//这句话无法将字符数组转换为字符串，需用下面的语句
            name=new String(nameArr);
            System.out.println("要下载的文件为："+name);
            out=new FileOutputStream("src\\down\\"+name);
            //第二步：将输入流中的其他内容写入到文件
            int len;
            while((len=in.read(buf))!=-1){
                out.write(buf,0,len);
            }
            out.flush();
            //异常捕获
        } catch (IOException e) {
            e.printStackTrace();
            //关闭资源
        }finally{
            //疑问：两个捕获可不可以放到一块呢，怎样处理关闭流时的异常最好呢？
            in.close();
            out.close();
        }
        //调试
        System.out.println(name);
    }

}//End of UploadThread