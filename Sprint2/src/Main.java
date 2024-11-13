public class Main {
    public static void main(String[] args) {
        Elemento lider = new Elemento("Líder", 1);
        lider.iniciarEnvio();

        Elemento nao_lider1 = new Elemento("Não Líder 1", 0);
        Elemento nao_lider2 = new Elemento("Não Líder 2", 0);
        nao_lider1.iniciarRecepcao();
        nao_lider2.iniciarRecepcao();

        lider.processarDocumento("Documento atualizado, com nova versão!!!");
    }
}
