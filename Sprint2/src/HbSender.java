import java.util.ArrayList;
import java.util.List;

public class HbSender {
    private EnviarMensagens enviarMensagens;
    private List<String> listaMensagens;

    public HbSender(String nome_elemento) {
        this.enviarMensagens = new EnviarMensagens(nome_elemento);
        this.listaMensagens = new ArrayList<>();
    }

    public void adicionarMensagem(String mensagem) {
        listaMensagens.add(mensagem);
    }

    public void iniciarEnvio() {
        Thread envioThread = new Thread(() -> {
            while (true) {
                try {
                    if (!listaMensagens.isEmpty()) {
                        StringBuilder mensagemAgregada = new StringBuilder();
                        for (String mensagem : listaMensagens) {
                            mensagemAgregada.append(mensagem).append(" | ");
                        }

                        String mensagemParaEnviar = mensagemAgregada.substring(0, mensagemAgregada.length() - 3);
                        enviarMensagens.enviarMensagem(mensagemParaEnviar);
                        listaMensagens.clear();
                    }
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        envioThread.start();
    }
}
