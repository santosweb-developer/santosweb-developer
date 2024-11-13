import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class EnviarMensagens {

    protected String nome_elemento;

    public EnviarMensagens(String nome_elemento) {
        this.nome_elemento = nome_elemento;
    }

    public void enviarMensagem(String mensagem) {
        try (MulticastSocket socket = new MulticastSocket()) {
            byte[] buffer = mensagem.getBytes();
            InetAddress grupo = InetAddress.getByName("230.0.0.0");
            DatagramPacket pacote = new DatagramPacket(buffer, buffer.length, grupo, 4446);
            socket.send(pacote);
            System.out.println(nome_elemento + " enviou uma mensagem: " + mensagem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
