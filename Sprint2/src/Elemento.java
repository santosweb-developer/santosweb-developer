import java.net.*;
import java.util.*;

public class Elemento {
    private String nome_elemento;
    private int tipo; // 1 para líder, 0 para não líder
    private HbSender hbSender;
    private DatagramSocket socket;
    private InetAddress grupo;
    private static final int MAIORIA = 2;
    private static final String NOVO_DOCUMENTO = "Nova versão do documento: ";
    private static final String COMMIT = "Commit do documento";
    private int ackCount = 0;
    private List<String> documentosRecebidos = new ArrayList<>();

    public Elemento(String nome_elemento, int tipo) {
        this.nome_elemento = nome_elemento;
        this.tipo = tipo;
        try {
            this.socket = new DatagramSocket();
            this.grupo = InetAddress.getByName("230.0.0.0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tipo == 1) {
            this.hbSender = new HbSender(nome_elemento);
        }
    }

    public void iniciarRecepcao() {
        Thread recepcaoThread = new Thread(() -> receberMensagens());
        recepcaoThread.start();
    }

    private void receberMensagens() {
        try (MulticastSocket socket = new MulticastSocket(4446)) {
            socket.joinGroup(grupo);

            System.out.println(nome_elemento + " entrou no socket multicast.");

            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                socket.receive(pacote);
                String mensagemRecebida = new String(pacote.getData(), 0, pacote.getLength());
                System.out.println(nome_elemento + " recebeu a mensagem: " + mensagemRecebida);

                if (mensagemRecebida.startsWith(NOVO_DOCUMENTO)) {
                    enviarACK();
                } else if (mensagemRecebida.equals(COMMIT)) {
                    System.out.println(nome_elemento + " recebeu o commit e sincronizou o documento!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarACK() {
        try {
            String ackMensagem = "ACK de " + nome_elemento;
            DatagramPacket ackPacote = new DatagramPacket(ackMensagem.getBytes(), ackMensagem.length(), InetAddress.getByName("127.0.0.1"), 4447);
            socket.send(ackPacote);
            System.out.println(nome_elemento + " enviou ACK ao líder.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniciarEnvio() {
        if (hbSender != null) {
            hbSender.iniciarEnvio();
        }
    }

    public void processarDocumento(String documento) {
        if (this.tipo == 1) {
            enviarNovaVersaoDocumento(documento);
        }
    }

    private void enviarNovaVersaoDocumento(String documento) {
        try {
            String mensagemDocumento = NOVO_DOCUMENTO + documento;
            byte[] buffer = mensagemDocumento.getBytes();
            DatagramPacket pacote = new DatagramPacket(buffer, buffer.length, grupo, 4446);
            socket.send(pacote);
            System.out.println(nome_elemento + " enviou nova versão do documento.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receberACK() {
        ackCount++;
        if (ackCount >= MAIORIA) {
            enviarCommit();
        }
    }

    private void enviarCommit() {
        try {
            byte[] commitMensagem = COMMIT.getBytes();
            DatagramPacket pacoteCommit = new DatagramPacket(commitMensagem, commitMensagem.length, grupo, 4446);
            socket.send(pacoteCommit);
            System.out.println(nome_elemento + " enviou commit para todos os elementos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

